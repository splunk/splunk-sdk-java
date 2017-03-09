# Shim to run a modular input written in Java. The modular input
# is assumed to be in the form of an executable jar. This shim
# is in ${PLATFORM}/bin/${INPUTNAME}.sh of the app the modular
# input is contained in, and the jar is assumed to be in
# jars/${INPUTNAME}.jar in the app.
#
# If ${PLATFORM}/bin/customized.java.path exists, this script will
# use java cmd defined in this file to start jvm, else default java
# will be used.
#
# Extra arguments to the JVM (i.e., -Xms512M) can be put in
# a file jars/${INPUTNAME}.vmopts and will be interpolated
# into the command to run the JVM.
SCRIPT="$0"

cd `dirname "$SCRIPT"`
SCRIPT=`basename "$SCRIPT"`

while [ -L "$SCRIPT" ]; do
    SCRIPT=`readlink "$SCRIPT"`
    cd `dirname "$SCRIPT"`
    SCRIPT=`basename "$SCRIPT"`
done

BASENAME=$(basename "$SCRIPT" .sh)
JAR_DIR=`pwd -P`/../../jars
CUSTOMIZED_JAVA_PATH_FILE=`pwd -P`/customized.java.path

if [ -f $CUSTOMIZED_JAVA_PATH_FILE ]; then
   JAVA_CMD=`cat $CUSTOMIZED_JAVA_PATH_FILE`
else
    JAVA_CMD="java"
fi

if [ -f $JAR_DIR/$BASENAME.vmopts ]; then
    VMOPTS=`cat $JAR_DIR/$BASENAME.vmopts`
else
    VMOPTS=""
fi

exec $JAVA_CMD $VMOPTS -jar $JAR_DIR/$BASENAME.jar $@

