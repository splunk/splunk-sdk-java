/*
 * Copyright 2013 Splunk, Inc.
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

#include "stdafx.h"
#include "shim.h"

// Global variable used to let the Ctrl+C handler propogate Ctrl+C to the JVM.
DWORD jvmPid = NULL;

int _tmain(int argc, _TCHAR* argv[])
{
    HANDLE processHandles[2] = {NULL, NULL};
    HANDLE &splunkdHandle = processHandles[0];
    HANDLE &jvmHandle = processHandles[1];
    PTSTR jarPath = NULL, jvmOptions = NULL, jvmCommandLine = NULL;
    DWORD waitOutcome, exitCode;

    DWORD returnCode = 0;

    STARTUPINFO si;
    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);

    PROCESS_INFORMATION pi;
    ZeroMemory(&pi, sizeof(pi));

    SetConsoleCtrlHandler((PHANDLER_ROUTINE)killJvm, TRUE);
    splunkdHandle = getSplunkdHandle();
    
    if (NULL == splunkdHandle) {
        // Couldn't get a handle to splunkd.
        printErrorMessage(GetLastError());

        returnCode = 1;
        goto CLEAN_UP_AND_EXIT;
    }

    jarPath = getPathToJar();
    jvmOptions = readJvmOptions(jarPath);
    jvmCommandLine = assembleJvmCommand(jarPath, jvmOptions, argc, argv);

    if (!CreateProcess(NULL, jvmCommandLine, NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi)) {
        // Process creation failed.
        printErrorMessage(GetLastError(), jvmCommandLine);

        returnCode = 1;
        goto CLEAN_UP_AND_EXIT;
    }

    CloseHandle(pi.hThread); // CreateProcess gives us a handle to the initial thread of the new process, which we don't need.
    jvmHandle = pi.hProcess;
    jvmPid = pi.dwProcessId;
    waitOutcome = WaitForMultipleObjects(2, processHandles, FALSE, INFINITE);

    if (waitOutcome == WAIT_OBJECT_0) {
        // Splunkd has died
        GenerateConsoleCtrlEvent(CTRL_C_EVENT, 0);

        goto CLEAN_UP_AND_EXIT;
    } else if (waitOutcome == WAIT_OBJECT_0 + 1) {
        // JVM has died
        if (!GetExitCodeProcess(jvmHandle, &returnCode)) {
            printErrorMessage(GetLastError());
            returnCode = 1;
        }
        goto CLEAN_UP_AND_EXIT;
    } else {
        // There was some other error
        printErrorMessage(GetLastError());

        returnCode = 1;
        goto CLEAN_UP_AND_EXIT;
    }

CLEAN_UP_AND_EXIT:
    if (NULL != jvmCommandLine) LocalFree(jvmCommandLine);
    if (NULL != jvmOptions)     LocalFree(jvmOptions);
    if (NULL != jarPath)        LocalFree(jarPath);

    if (NULL != splunkdHandle) CloseHandle(splunkdHandle);
    if (NULL != jvmHandle)     CloseHandle(jvmHandle);

    return returnCode;
}


BOOL killJvm(DWORD interruptCode) {
    if (jvmPid != NULL) {
        GenerateConsoleCtrlEvent(interruptCode, jvmPid);
        return TRUE;
    } else {
        return FALSE;
    }
}


void printErrorMessage(DWORD errorCode, ...) {
    LPTSTR buffer;

    va_list args = NULL;
    va_start(args, errorCode);

    FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ALLOCATE_BUFFER, 
        NULL, errorCode, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), (LPTSTR)&buffer, 0, &args);

    fwprintf(stderr, TEXT("ERROR %s\r\n"), buffer);

    LocalFree(buffer);
}


PTSTR readJvmOptions(PTSTR jarPath) {
    DWORD jarPathLen = _tcslen(jarPath);
    // vmoptsPath is the same as jarPath, but ending with .vmopts instead of .jar. We allocate
    // 3 additional TCHARs for the additional length of .vmopts, and 1 more TCHAR for a NULL
    // terminator, so jarPathLen+4.
    PTSTR vmoptsPath = (PTSTR)malloc(sizeof(TCHAR) * (jarPathLen + 4));
    PTSTR suffixPtr = vmoptsPath;

    _tcscpy_s(vmoptsPath, jarPathLen+1, jarPath);

    suffixPtr = _tcsrchr(vmoptsPath, CHAR('.'));
    if (suffixPtr == 0) {
        SetLastError(ERROR_INVALID_DATA);
        return NULL;
    }

    _tcscpy_s(suffixPtr, 8, TEXT(".vmopts"));

    HANDLE vmoptsHandle = CreateFile(vmoptsPath, GENERIC_READ, FILE_SHARE_READ, 
        NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);

    if (INVALID_HANDLE_VALUE == vmoptsHandle) {
        if (ERROR_FILE_NOT_FOUND == GetLastError() || ERROR_PATH_NOT_FOUND == GetLastError()) {
            // We can't return a literal because we will try to deallocate it later.
            free(vmoptsPath);
            vmoptsPath = (PTSTR)malloc(sizeof(TCHAR));
            _tcscpy_s(vmoptsPath, 1, TEXT(""));
            return vmoptsPath;
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

#define APPEND_TCS(literal) { \
    _tcscpy_s(index, _tcslen(literal)+1, literal); \
    index += _tcslen(literal); \
}

    APPEND_TCS(TEXT("java "));
    APPEND_TCS(jvmOptions);
    APPEND_TCS(TEXT(" -jar \""));
    APPEND_TCS(jarPath);
    APPEND_TCS(TEXT("\""));

    for (i = 1; i < argc; i++) {
        APPEND_TCS(TEXT(" "));
        APPEND_TCS(argv[i]);
    }

#undef APPEND_TCS

    return buffer;
}


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
    if (_tgetenv_s(&bufferSize, pidBuffer, bufferSize, SPLUNKD_HANDLE_ENVVAR)) {
        return NULL; // There was an error in getting the environment variable.
    }
    splunkdPid = _tcstoul(pidBuffer, &stopString, 10);

    if (*stopString != NULL || splunkdPid == 0) {
        // We haven't parsed the whole environment variable as a number.
        SetLastError(ERROR_BAD_ENVIRONMENT);
        return NULL;
    }

    free(pidBuffer);
    
    return OpenProcess(SYNCHRONIZE, FALSE, splunkdPid);
}


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

    if (baseName-endPtr < _tcslen(TEXT("\\jars\\"))) {
        return NULL; // Not enough space to copy the path fragment in without clobbering the jar name.
    }

    // Instead of NULL terminating, we copy \jars\ over top and advance to the end of it.
    _tcscpy_s(endPtr, jarPathFragmentLen, jarPathFragment);
    endPtr += jarPathFragmentLen - 1;
    // Now shift the base name over.
    baseNameLen = _tcslen(baseName) + 1;
    // Advance endPtr and add .jar to it.
    memmove_s(endPtr, baseNameLen*sizeof(TCHAR), baseName, baseNameLen*sizeof(TCHAR));
    endPtr += baseNameLen - 1;
    _tcscpy_s(endPtr, jarSuffixLen, jarSuffix);

    return thisPath;
}