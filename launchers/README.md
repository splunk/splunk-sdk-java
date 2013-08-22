# Modular input launchers for the Splunk SDK for Java

Splunk cannot launch Java programs as modular inputs directly. Instead, we need to provide a few small programs to launch a JVM and run a program. Since modular inputs written in Java need to work across all Splunk variants, including the universal forwarder, the launchers cannot rely on Python or anything else in the underlying system. Instead, we provide a set of C programs compiled for each of Linux, MacOS X, and Windows.

The programs assume the following layout in an app: a jars/ directory containing a launchable jar containing the modular input, and a configuration file with a .ini suffix of the same base name that contains options for what JVM to launch and what options to pass to it. Then platform specific bin directories contain the launcher programs, named the base name of the target jar. For for a jar named myinput.jar, the layout would be

myapp/
  jars/
    myinput.jar
    myinput.ini
  linux_x86/
    myinput
  linux_x86_64/
    myinput
  darwin_x86_64/
    myinput
  windows_x86/
    myinput.exe
  windows_x86_64/
    myinput.exe

The configuration file should have the form

JAVA_HOME=/path/to/jre
JAVA_OPTS=options to pass to JVM

The keys are case insensitive, and leading whitespace and whitespace around the equals sign is ignored.

The POSIX launchers use the exec system call to replace the launcher with a JVM with the proper arguments. The Windows launcher starts the JVM with a call to CreateProcess, forwards exit events, and monitors the JVM it launches for termination.

Testing story:

http://stackoverflow.com/questions/65820/unit-testing-c-code

There are a lot of options.



Windows:

Use GetModuleFileName(NULL) to get current name and path of this executable.

For Linux,

  char *resolved_path = realpath("/proc/self/exe", NULL);
  printf("%s\n", resolved_path);
  free(resolved_path);

/proc/pid/exe on Linux and /proc/pid/file on BSD are symlinks to the executable. Call readlink to resolve the symlink to get the path.

On OS X, there are two ways:
http://stackoverflow.com/questions/799679/programatically-retrieving-the-absolute-path-of-an-os-x-command-line-app/1024933#1024933
libproc.h has proc_pidpath
_NSGetExecutablePath in dyld

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <libproc.h>

int main (int argc, char* argv[])
{
    int ret;
    pid_t pid; 
    char pathbuf[PROC_PIDPATHINFO_MAXSIZE];

    pid = getpid();
    ret = proc_pidpath (pid, pathbuf, sizeof(pathbuf));
    if ( ret <= 0 ) {
        fprintf(stderr, "PID %d: proc_pidpath ();\n", pid);
        fprintf(stderr, "    %s\n", strerror(errno));
    } else {
        printf("proc %d: %s\n", pid, pathbuf);
    }

    return 0;
}

proc_pidpath looks like the way to go. I'll test it.

To exec the program:

  char* args[] = {"java", "-jar", "braindead.jar", NULL};
  execvp("java", args);
