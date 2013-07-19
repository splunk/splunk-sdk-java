/*
 * Copyright 2012 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/* 
 * shim - Windows launcher for Splunk modular inputs written in Java.
 *
 * shim is provided as both 32-bit and 64-bit binaries. If you are setting up a
 * modular input written in Java in your app, you should make it into an executable
 * jar, and put it in a jars/ directory in your app. The jar should be named the
 * with the same name as the modular input kind defined in Splunk. That is, if your
 * stanza in README/inputs.conf.spec that defines the modular input kind is called
 * 'abc', then the jar should be in jars/abc.jar. You can create an ASCII (well, UTF-8)
 * text file in jars/abc.vmopts that contains options to pass to java when creating
 * the virtual machine (i.e., -Xms512M -agent something.jar).
 *
 * Then place the 32-bit binary of shim in windows_x86/bin/abc.exe and the 64-bit
 * binary in windows_x86_64/bin/abc.exe (changing 'abc' to the name of your modular
 * input kind). Splunk will launch them and they in turn will look for the jar,
 * launch it, and handle all the control signals from Splunk.
 *
 * Once shim starts and executes the JVM, it waits for Ctrl+C from Splunk or for splunkd
 * to die. If either of these events occurs, it sends Ctrl+C to the JVM, waits for it
 * to exit, and exits itself. If the JVM exits, the shim exits immediately thereafter.
 */

#include "stdafx.h"
#include "shim.h"

// Global variable used to let the Ctrl+C handler propogate Ctrl+C to the JVM.
DWORD jvmPid = NULL;

int _tmain(int argc, _TCHAR* argv[])
{
    HANDLE processHandles[2];
    PTSTR jarPath, jvmOptions, jvmCommandLine;
    DWORD waitOutcome, exitCode;

    SetConsoleCtrlHandler((PHANDLER_ROUTINE)killJvm, TRUE);
    
    processHandles[0] = getSplunkdHandle();
    if (NULL == processHandles[0]) {
        // Couldn't get a handle to splunkd.
        printErrorMessage(GetLastError());
        return 1;
    }

    jarPath = getPathToJar();
    jvmOptions = readJvmOptions(jarPath);
    jvmCommandLine = assembleJvmCommand(jarPath, jvmOptions, argc, argv);

    STARTUPINFO si = {sizeof(si)};
    PROCESS_INFORMATION pi;
    if (!CreateProcess(NULL, jvmCommandLine, NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi)) {
        // Process creation failed.
        printErrorMessage(GetLastError(), jvmCommandLine);
        return 1;
    }

    CloseHandle(pi.hThread);
    processHandles[1] = pi.hProcess;
    jvmPid = pi.dwProcessId;
    waitOutcome = WaitForMultipleObjects(2, processHandles, FALSE, INFINITE);

    if (waitOutcome == WAIT_OBJECT_0) {
        // Splunkd has died
        GenerateConsoleCtrlEvent(CTRL_C_EVENT, 0);
        return 0;
    } else if (waitOutcome == WAIT_OBJECT_0 + 1) {
        // JVM has died
        if (!GetExitCodeProcess(processHandles[1], &exitCode)) {
            printErrorMessage(GetLastError());
            return 1;
        }
        CloseHandle(processHandles[0]); // Close splunkd handle
        CloseHandle(processHandles[1]); // Close JVM handle
        return exitCode;
    } else {
        // There was some other error.
        printErrorMessage(GetLastError());
    }

    return 0;
}

/*
 * A PHANDLER_ROUTINE to handle receiving console control events such as 
 * Ctrl+C. Upon receiving a console control event, it sends the same event
 * to the JVM (which it finds via the PID stored in the global jvmPid
 * variable).
 *
 * The handler does not further shutdown, since as soon as the JVM dies,
 * the standard logic in _tmain will handle exiting this program as well.
 */
BOOL killJvm(DWORD interruptCode) {
    if (jvmPid != NULL) {
        GenerateConsoleCtrlEvent(interruptCode, jvmPid);
        return TRUE;
    } else {
        return FALSE;
    }
}

/*
 * Print an error message to stderr containing the human readable error message
 * corresponding to the GetLastError's return value.
 */
void printErrorMessage(DWORD errorCode, ...) {
    DWORD nBytesWritten;
    LPTSTR buffer;

    va_list args = NULL;
    va_start(args, errorCode);

    FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ALLOCATE_BUFFER, 
        NULL, errorCode, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), (LPTSTR)&buffer, 0, &args);

    fwprintf(stderr, TEXT("ERROR %s\r\n"), buffer);
}

/*
 * Returns the contents of a file with the same base name as the Jar referred to in jarPath, 
 * but with the suffix .vmopts (so for path\to\myinput.jar, reads path\to\myinputs.vmptops).
 * 
 * This function is meant to read the options for the JVM (i.e., -Xms512M) so they can be
 * added to the command line. If there is an error that prevents reading the file (the jar path
 * does not end in .jar, or it cannot be read), readJvmOptions returns NULL and sets an error
 * code retrievable with GetLastError. If the file does not exist, readJvmOptions returns an
 * empty string.
 */
PTSTR readJvmOptions(PTSTR jarPath) {
    DWORD jarPathLen = _tcslen(jarPath);
    PTSTR vmoptsPath = (PTSTR)malloc(sizeof(TCHAR) * (jarPathLen + 4));
    PTSTR suffixPtr = vmoptsPath;

    _tcscpy_s(vmoptsPath, jarPathLen+1, jarPath);

    suffixPtr = _tcsrchr(vmoptsPath, CHAR('.'));
    if (suffixPtr == 0) {
        SetLastError(ERROR_INVALID_DATA);
        return NULL;
    }

    _tcscpy_s(suffixPtr, 8, TEXT(".vmopts"));

    OFSTRUCT fileInformation;
    HANDLE vmoptsHandle = CreateFile(vmoptsPath, GENERIC_READ, FILE_SHARE_READ, 
        NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);

    if (INVALID_HANDLE_VALUE == vmoptsHandle) {
        if (ERROR_FILE_NOT_FOUND == GetLastError()) {
            return TEXT("");
        } else {
            return NULL;
        }
    }

    DWORD fileSize = GetFileSize(vmoptsHandle, NULL);
    if (INVALID_FILE_SIZE == fileSize) {
        return NULL;
    }

    DWORD nRead;
    char* buffer = (char*)malloc((fileSize+1) * sizeof(char)); // +1 to give space for a NULL character.
    if (!ReadFile(vmoptsHandle, buffer, fileSize, &nRead, NULL)) {
        return NULL;
    }
    buffer[nRead] = NULL; // Ensure options are null terminated.

#ifdef _UNICODE
    // Calculate how much space is needed.
    DWORD nWchars = MultiByteToWideChar(CP_UTF8, MB_ERR_INVALID_CHARS, buffer, nRead, NULL, 0);
    wchar_t *vmopts = (wchar_t*)malloc(sizeof(wchar_t) * (nWchars+1));
    if (!MultiByteToWideChar(CP_UTF8, MB_ERR_INVALID_CHARS, buffer, nRead, vmopts, nWchars)) {
        return NULL;
    }
    vmopts[nWchars] = NULL;
    return vmopts;
#else
    return buffer;
#endif
}

/*
 * Construct the full command to run the jar. This will return a new buffer containing
 *
 *     java [jvmOptions] -jar "[jarPath]" [argv[0]] [argv[1]] ...
 */
PTSTR assembleJvmCommand(PTSTR jarPath, PTSTR jvmOptions, int argc, _TCHAR* argv[]) {
    PTSTR buffer, index;
    size_t len;
    int i;

    len = 0;
    for (i = 1; i < argc; i++) {
        len += _tcslen(argv[i]) + 1; // The +1 accounts for a space to separate the arguments
    }

    // 13 = number of characters for java and -jar sections; +1 at the end is for the null terminator.
    buffer = (PTSTR)malloc((13 + _tcslen(jarPath) + _tcslen(jvmOptions) + len + 1) * sizeof(TCHAR));
    index = buffer;

    _tcscpy_s(index, 6, TEXT("java "));
    index += 5;
    len = _tcslen(jvmOptions);
    _tcscpy_s(index, len+1, jvmOptions);
    index += len;
    _tcscpy_s(index, 8, TEXT(" -jar \""));
    index += 7;
    len = _tcslen(jarPath);
    _tcscpy_s(index, len+1, jarPath);
    index += len;
    _tcscpy_s(index, 2, TEXT("\""));
    index += 1;

    for (i = 1; i < argc; i++) {
        _tcscpy_s(index, 2, TEXT(" "));
        index += 1;
        len = _tcslen(argv[i]);
        _tcscpy_s(index, len+1, argv[i]);
        index += len;
    }

    return buffer;
}


/*
 * Returns a HANDLE referring to splunkd's process, or NULL if it could
 * not find such a handle. Use GetLastError to find the error in that case.
 *
 * When splunkd starts a modular input script, it sets its pid as the value of
 * an environment variable SPLUNKD_PROCESSID.
 */
HANDLE getSplunkdHandle() {
    const TCHAR* SPLUNKD_HANDLE_ENVVAR = TEXT("SPLUNKD_PROCESSID");

    size_t bufferSize;
    TCHAR* pidBuffer;
    TCHAR* stopString;

    DWORD splunkdPid;

    // Find how large a buffer we need.
    _tgetenv_s(&bufferSize, NULL, 0, SPLUNKD_HANDLE_ENVVAR);
    if (bufferSize == 0) {
        SetLastError(ERROR_ENVVAR_NOT_FOUND);
        return NULL;
    }

    pidBuffer = (PTSTR)malloc(bufferSize * sizeof(TCHAR));
    _tgetenv_s(&bufferSize, pidBuffer, bufferSize, SPLUNKD_HANDLE_ENVVAR);
    splunkdPid = _tcstoul(pidBuffer, &stopString, 10);

    if (*stopString != NULL || splunkdPid == 0) {
        // We haven't parsed the whole environment variable as a number.
        SetLastError(ERROR_BAD_ENVIRONMENT);
        return NULL;
    }
    
    return OpenProcess(SYNCHRONIZE, FALSE, splunkdPid);
}

/*
 * Return a PTSTR with the path to the Java jar, or NULL if no path could be constructed.
 *
 * The jar is taken to be at ../../jars/{name}.jar from the location of the current executable,
 * if the name of the current executable is {name}.exe.
 */
PTSTR getPathToJar() {
    PTSTR thisPath, endPtr, baseName;
    PCTSTR jarPathFragment = TEXT("\\jars\\");
    const DWORD jarPathFragmentLen = 7;
    PCTSTR jarSuffix = TEXT(".jar");
    const DWORD jarSuffixLen = 5;
    const size_t N = 1024;
    DWORD baseNameLen;

    thisPath = (PTSTR)malloc(N*sizeof(TCHAR));
    if (N == GetModuleFileName(NULL, thisPath, N) && ERROR_INSUFFICIENT_BUFFER == GetLastError()) {
        return NULL;
    }
    
    // The following code removes the last two path segments before the executable name in the buffer,
    // puts a \jars\ on the path, then shifts the base name of the executable over and adds .jar to it.
    // Graphically, with 0 representing null terminators, the steps are:
    //
    // 1. $SPLUNK_HOME$\etc\apps\myapp\windows_x86_64\bin\myinput.exe0
    // 2. $SPLUNK_HOME$\etc\apps\myapp\jars\0             myinput.exe0
    // 3. $SPLUNK_HOME$\etc\apps\myapp\jars\myinput0
    // 4. $SPLUNK_HOME$\etc\apps\myapp\jars\myinput.jar0
    
    // Find 'myinput.exe', just after the last \.
    endPtr = _tcsrchr(thisPath, '\\');
    baseName = endPtr+1; // This is 'myinput.exe'
    *endPtr = NULL; // NULL terminate the string before this point so we can strrchar to get the next \.
    endPtr = _tcsrchr(baseName, '.'); // Find .exe, and NULL terminate the base name.
    *endPtr = NULL;

    // Take two directory levels off this path.
    endPtr = _tcsrchr(thisPath, '\\');
    *endPtr = NULL;
    endPtr = _tcsrchr(thisPath, '\\');
    // Instead of NULL terminating, we copy \jars\ over top and advance to the end of it.
    _tcscpy_s(endPtr, jarPathFragmentLen, jarPathFragment);
    endPtr += jarPathFragmentLen - 1;
    // Now shift the base name over.
    baseNameLen = _tcslen(baseName) + 1;
    // Advance endPtr and add .jar to it.
    _tcscpy_s(endPtr, baseNameLen, baseName);
    endPtr += baseNameLen - 1;
    _tcscpy_s(endPtr, jarSuffixLen, jarSuffix);

    return thisPath;
}