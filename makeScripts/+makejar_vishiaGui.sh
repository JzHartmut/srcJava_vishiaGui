echo ====== start script ===============================================================
echo execute  $0
## Set the current dir 3 level before the script, it sees the src/srcDir/makeScripts:
cd $(dirname $0)/../../..
echo currdir $PWD

## Determines the name of some files marked with the date.
## If it not set, the current date will be used. This is usual proper, to prevent confusion.
export VERSIONSTAMP=""

## Determines the timestamp of the files in the jar. The timestamp determines the MD5 check code. 
## Do not change the timestamp on repeated build, and check the checksum and content of jar.
## If it is equal, it is a reproduces build. The $TIMEinJAR is important 
##  because it determines the timestamp and hence the checksum in the jar file. 
## Using another timestamp on equal result files forces another MD5.
## Hence let this unchanged in comparison to a pre-version 
## if it is assumed that the sources are unchanged.
## Only then a comparison of MD5 is possible. 
## The comparison byte by byte inside the jar (zip) file is always possible.
export TIMEinJAR="2023-10-12+00:00"

## This directory contains some basic scripts. Should be exists
export MAKEBASEDIR="src/srcJava_vishiaBase/makeScripts"

## Determine the name of some files and directories with the component's name:
export DSTNAME="vishiaGui"

## Determines the sources for this component to create a jar
SRCDIRNAME="src/srcJava_vishiaGui"
export SRC_ALL="$SRCDIRNAME/java"            ## use all sources from here
export SRC_ALL2="src/srcJava_vishiaRun/java" ## use all sources also from here
export SRCPATH="";                           ## search path for depending sources if FILE1SRC is given
export FILE1SRC=""                           ## use a specific source file (with depending ones)

# Determines search path for compiled sources (in jar) for this component. 
# Select the location and the proper vishiaBase
if test -f ../deploy/vishiaBase-$VERSION_VISHIABASE.jar
  then export JAR_vishiaBase="../deploy/vishiaBase-$VERSION_VISHIABASE.jar"
elif test -f tools/vishiaBase.jar; then export JAR_vishiaBase="tools/vishiaBase.jar"
elif test -f ../tools/vishiaBase.jar; then export JAR_vishiaBase="../tools/vishiaBase.jar"
elif test -f jars/vishiaBase.jar; then export JAR_vishiaBase="jars/vishiaBase.jar"
elif test -f libs/vishiaBase.jar; then export JAR_vishiaBase="libs/vishiaBase.jar"
else
  echo vishiaBase.jar not found, abort
  exit 5
fi
echo JAR_vishisBase=$JAR_vishiaBase


# Select the location and the proper SWT
pwd
JAR_SWT="org.eclipse.swt.win32.x86_64.jar"
if test -f ../deploy/$JAR_SWT; then export JARPATH_SWT"../deploy/$JAR_SWT"
elif test -f tools/$JAR_SWT; then export JARPATH_SWT="tools/$JAR_SWT"
elif test -f ../tools/$JAR_SWT; then export JARPATH_SWT="../tools/$JAR_SWT"
elif test -f jars/$JAR_SWT; then export JARPATH_SWT="jars/$JAR_SWT"
elif test -f libs/$JAR_SWT; then export JARPATH_SWT="libs/$JAR_SWT"
else
echo not found [../deploy|../tools|tools|jars|libs]/$JAR_SWT
JAR_SWT="org.eclipse.swt.linux.x86_64.jar"
if test -f ../deploy/$JAR_SWT; then export JARPATH_SWT"../deploy/$JAR_SWT"
elif test -f tools/$JAR_SWT; then export JARPATH_SWT="tools/$JAR_SWT"
elif test -f jars/$JAR_SWT; then export JARPATH_SWT="jars/$JAR_SWT"
elif test -f libs/$JAR_SWT; then export JARPATH_SWT="libs/$JAR_SWT"
else
  echo not found [../deploy|../tools|tools|jars|libs]/$JAR_SWT
  exit 5
fi; fi
echo JARPATH_SWT=$JARPATH_SWT


if test "$OS" = "Windows_NT"; then export sepPath=";"; else export sepPath=":"; fi
export CLASSPATH="-cp $JARPATH_SWT$sepPath$JAR_vishiaBase"

## Determines the manifest file for the jar
export MANIFEST="$SRCDIRNAME/makeScripts/$DSTNAME.manifest"

# Determines resource files to store in the jar
export RESOURCEFILES="$SRC_ALL:**/*.zbnf $SRC_ALL:**/*.txt $SRC_ALL:**/*.xml $SRC_ALL:**/*.png"

## add paths to the source.zip, should be a relative path from current dir 
export SRCADD_ZIP=".:$SRCDIRNAME/makeScripts/*"  

#now run the common script:
chmod 777 $MAKEBASEDIR/-makejar-coreScript.sh
$MAKEBASEDIR/-makejar-coreScript.sh

