#!/bin/bash

#Script to execute ERPMain process 

if [ ! -z "$JAVA_OPTS" ]; then
	JAVA_OPTS="$JAVA_OPTS"
else
	JAVA_OPTS="-Xmx512m"
fi

if [ ! -z "JAVA_HOME" ]; then
	JAVA_CMD=$JAVA_HOME/bin/java
fi

if [ -z "$JAVA_CMD" ] || [ ! -x "$JAVA_CMD" ]; then
	JAVA_CMD="which java"
fi

if [ -z "$JAVA_CMD" ] || [ ! -x "$JAVA_CMD" ]; then
	echo "Unable to find java in JAVA_HOME or PATH. Please ensure JAVA_HOME is set" >&2
fi

CLASS_NAME=$1
CLASS_PATH=$SPLUNK_HOME/bin/jars/SplunkMR-s6.0-h1.0.jar:$bin/* 

$JAVA_CMD $JAVA_OPTS -cp $CLASS_PATH $CLASS_NAME

