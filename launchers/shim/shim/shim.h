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

/** 
 * shim - Windows launcher for Splunk modular inputs written in Java.
 *
 * shim is provided as both 32-bit and 64-bit binaries. If you are setting up a
 * modular input written in Java in your app, you should make it into an executable
 * jar, and put it in a jars/ directory in your app. The jar should be named the
 * with the same name as the modular input kind defined in Splunk. That is, if your
 * stanza in README/inputs.conf.spec that defines the modular input kind is called
 * 'myinput', then the jar should be in jars/myinput.jar. You can create a UTF-8 encoded
 * text file in jars/myinput.vmopts that contains options to pass to java when creating
 * the virtual machine (i.e., -Xms512M -agent something.jar).
 *
 * Then place the 32-bit binary of shim in windows_x86/bin/myinput.exe and the 64-bit
 * binary in windows_x86_64/bin/myinput.exe (changing 'myinput' to the name of your modular
 * input kind). Splunk will launch them and they in turn will look for the jar,
 * launch it, and handle all the control signals from Splunk.
 *
 * Once shim starts and executes the JVM, it waits for Ctrl+C from Splunk or for splunkd
 * to die. If either of these events occurs, it sends Ctrl+C to the JVM, waits for it
 * to exit, and exits itself. If the JVM exits, the shim exits immediately thereafter.
 */


#ifndef __SHIM_H__
#define __SHIM_H__

#include <Windows.h>

/**
 * Returns a HANDLE referring to splunkd's process, or NULL if it could
 * not find such a handle. Use GetLastError to find the error in that case.
 *
 * When splunkd starts a modular input script, it sets its pid as the value of
 * an environment variable SPLUNKD_PROCESSID.
 */
HANDLE getSplunkdHandle();


/**
 * Return a PTSTR with the path to the Java jar, or NULL if no path could be constructed.
 *
 * The jar is taken to be at ../../jars/{name}.jar from the location of the current executable,
 * if the name of the current executable is {name}.exe.
 */
PTSTR getPathToJar();


/**
 * Returns the contents of a file with the same base name as the Jar referred to in jarPath, 
 * but with the suffix .vmopts (so for path\to\myinput.jar, reads path\to\myinputs.vmptops).
 * 
 * This function is meant to read the options for the JVM (i.e., -Xms512M) so they can be
 * added to the command line. If there is an error that prevents reading the file (the jar path
 * does not end in .jar, or it cannot be read), readJvmOptions returns NULL and sets an error
 * code retrievable with GetLastError. If the file does not exist, readJvmOptions returns an
 * empty string.
 */
PTSTR readJvmOptions(PTSTR pathToJar);


/**
 * Construct the full command to run the jar. This will return a new buffer containing
 *
 *     java [jvmOptions] -jar "[jarPath]" [argv[0]] [argv[1]] ...
 */
PTSTR assembleJvmCommand(PTSTR pathToJar, PTSTR jvmOptions, int argc, _TCHAR* argv[]);


/**
 * Print an error message to stderr containing the human readable error message
 * corresponding to the GetLastError's return value.
 */
void printErrorMessage(DWORD errorCode, ...);


/**
 * A PHANDLER_ROUTINE to handle receiving console control events such as 
 * Ctrl+C. Upon receiving a console control event, it sends the same event
 * to the JVM (which it finds via the PID stored in the global jvmPid
 * variable).
 *
 * The handler does not further shutdown, since as soon as the JVM dies,
 * the standard logic in _tmain will handle exiting this program as well.
 */
BOOL killJvm(DWORD interruptCode);

#endif