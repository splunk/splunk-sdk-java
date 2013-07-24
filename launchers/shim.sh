# Shim to run a modular input written in Java. The modular input
# is assumed to be in the form of an executable jar. This shim
# is in ${PLATFORM}/bin/${INPUTNAME}.sh of the app the modular
# input is contained in, and the jar is assumed to be in
# jars/${INPUTNAME}.jar in the app.
#
# Extra arguments to the JVM (i.e., -Xms512M) can be put in
# a file jars/${INPUTNAME}.vmopts and will be interpolated
# into the command to run the JVM.
SCRIPT=$(readlink -f "$0")
BASENAME=$(basename "$SCRIPT" .sh)
JAR_DIR=$(dirname "$SCRIPT")/../../jars

if [ -f $JAR_DIR/$BASENAME.vmopts ]; then
    VMOPTS=`cat $JAR_DIR/$BASENAME.vmopts`
else
    VMOPTS=""
fi

exec java $VMOPTS -jar $JAR_DIR/$BASENAME.jar $@
