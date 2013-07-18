#ifndef __SHIM_H__
#define __SHIM_H__

#include <Windows.h>

HANDLE getSplunkdHandle();
PTSTR getPathToJar();
PTSTR readJvmOptions(PTSTR pathToJar);
PTSTR assembleJvmCommand(PTSTR pathToJar, PTSTR jvmOptions, int argc, _TCHAR* argv[]);
void printErrorMessage(DWORD errorCode, ...);
BOOL killJvm(DWORD interruptCode);

#endif