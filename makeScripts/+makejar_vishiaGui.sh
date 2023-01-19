echo =========================================================================
echo execute  $0
## Set the current dir 3 level before the script, it sees the src/srcDir/makeScripts
cd $(dirname $0)/../../..
echo currdir $PWD
export DSTNAME="vishiaGui"
echo " ... generates the $DSTNAME.jar from srcJava_$DSTNAME core sources"

#Do not change the version on repeated build, and check the checksum and content of jar.
#If it is equal, it is a reproduces build. The $VERSIONSTAMP is important 
#  because it determines the timestamp and hence the checksum in the jar file. 
export VERSIONSTAMP="2022-12-10"

## The VERSIONSTAMP can come form calling script, elsewhere it is set with the current date.
## This determines the names of the results, but not the content and not the MD5 check sum.
if test "$VERSIONSTAMP" = ""; then export VERSIONSTAMP=$(date -I); fi   ## writes current date
## Determines the timestamp of the files in the jar. The timestamp determines also
## the MD5 check code. 
## Do not change the version on repeated build, and check the checksum and content of jar.
## If it is equal, it is a reproduces build. The $VERSIONSTAMP is important 
##  because it determines the timestamp and hence the checksum in the jar file. 
## Using another timestamp on equal result files forces another MD5.
## Hence let this unchanged in comparison to a pre-version 
## if it is assumed that the sources are unchanged.
## Only then a comparison of MD5 is possible. 
## The comparison byte by byte inside the jar (zip) file is always possible.
## Use this timestamp for file in jars, influences the MD5 check:
export TIMEinJAR="$VERSIONSTAMP+00:00"

## Determine a dedicated vishiaBase-yyyy-mm-dd.jar or deactivate it to use the current vishiaBase.jar:
export VERSION_VISHIABASE="XX2021-07-01"

# SWT for Windows-64 it is a copy of the used jar, see bom
# comment or uncomment for alternative swt.jar
#export JAR_SWT=""  ##left empty if unversioned should be used
#export JAR_SWT="org.eclipse.swt.win32.win32.x86_64_3.110.0.v20190305-0602.jar"
export JAR_SWT="org.eclipse.swt.win32_x86_64.jar"
#export JAR_SWT="org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar"   ##32 bit SWT
#export JAR_SWT=LINUX-TODO


# It should have anytime the stamp of the newest file, independing of the VERSIONSTAMP
export SRCZIPFILE="vishiaGui-$VERSIONSTAMP-source.zip"

export SRCDIRNAME="src/srcJava_vishiaGui"  ##must proper to the own location
export MAKEBASEDIR="src/srcJava_vishiaBase/makeScripts"     ##must proper in the own location

# Select the location and the proper vishiaBase
if test -f ../deploy/vishiaBase-$VERSION_VISHIABASE.jar
then export JAR_vishiaBase="../deploy/vishiaBase-VERSION_VISHIABASE.jar"
elif test -f tools/vishiaBase.jar
then export JAR_vishiaBase="tools/vishiaBase.jar"
elif test -f jars/vishiaBase.jar
then export JAR_vishiaBase="jars/vishiaBase.jar"
elif test -f libs/vishiaBase.jar
then export JAR_vishiaBase="libs/vishiaBase.jar"
else
  echo vishiaBase.jar not found, abort
fi
echo JAR_vishisBase=$JAR_vishiaBase

# Select the location and the proper SWT
if test -f ../deploy/$JAR_SWT
then export JARPATH_SWT"../deploy/$JAR_SWT"
elif test -f tools/$JAR_SWT
then export JARPATH_SWT="tools/$JAR_SWT"
elif test -f jars/$JAR_SWT
then export JARPATH_SWT="jars/$JAR_SWT"
elif test -f libs/$JAR_SWT
then export JARPATH_SWT="libs/$JAR_SWT"
##elif test -f ../../jars/org.eclipse.swt.win32_x86_64.jar
##then export JARPATH_SWT="../../jars/org.eclipse.swt.win32_x86_64.jar"
##elif test -f ../../jars/org.eclipse.swt.win32_x86.jar
##then export JARPATH_SWT="../../jars/org.eclipse.swt.win32_x86.jar"
##elif test -f ../../jars/org.eclipse.swt.gtk.linux.x86_64.jar
##then export JARPATH_SWT="../../jars/org.eclipse.swt.gtk.linux.x86_64.jar"
##elif test -f ../../../tools/org.eclipse.swt.win32_x86_64.jar
##then export JARPATH_SWT="../../../../../../tools/org.eclipse.swt.win32_x86_64.jar"
##elif test -f ../../../tools/org.eclipse.swt.win32_x86.jar
##then export JARPATH_SWT="../../../../../../tools/org.eclipse.swt.win32_x86.jar"
##elif test -f ../../../tools/org.eclipse.swt.gtk.linux.x86_64.jar
##then export JARPATH_SWT="../../../../../../tools/org.eclipse.swt.gtk.linux.x86_64.jar"
##elif test -f ../../../libs/org.eclipse.swt.win32_x86_64.jar
##then export JARPATH_SWT="../../../../../../libs/org.eclipse.swt.win32_x86_64.jar"
##elif test -f ../../../libs/org.eclipse.swt.win32_x86.jar
##then export JARPATH_SWT="../../../../../../libs/org.eclipse.swt.win32_x86.jar"
##elif test -f ../../../libs/org.eclipse.swt.gtk.linux.x86_64.jar
##then export JARPATH_SWT="../../../../../../libs/org.eclipse.swt.gtk.linux.x86_64.jar"
else
  echo swt.jar not found, abort
fi
echo JARPATH_SWT=$JARPATH_SWT

if test "$OS" = "Windows_NT"; then export sepPath=";"; else export sepPath=":"; fi
#The CLASSPATH is used for reference jars for compilation which should be present on running too.
##Note here libs is not really used only for enhancements
export CLASSPATH="$JARPATH_SWT$sepPath$JAR_vishiaBase"

#It is also the tool for zip and jar used inside the core script
export JAR_zipjar=$JAR_vishiaBase

#determine the sources:
# Note: include sources of vishiaRun are part of the source.zip
export MANIFEST=$SRCDIRNAME/makeScripts/$DSTNAME.manifest

##This selects the files to compile
export SRC_MAKE="$SRCDIRNAME/makeScripts" 
export SRC_ALL="$SRCDIRNAME/java/vishiaGui"
export SRC_ALL2="src/srcJava_vishiaRun/Java"
export SRCPATH="$SRC_ALL$sepPath$SRC_ALL2"
#either both source trees are face to face, or the cmpn are so
##export SRCPATH="$SRC_ALL"

# Resourcefiles for files in the jar
export RESOURCEFILES="$SRCDIRNAME/java/vishiaGui:**/*.zbnf $SRCDIRNAME/java/vishiaGui:**/*.txt $SRCDIRNAME/java/vishiaGui:**/*.xml $SRCDIRNAME/java/vishiaGui:**/*.png"

#now run the common script:
# The DEPLOYSCRIPT will be executed after generation in the coreScript if given and found.
export DEPLOYSCRIPT="$MAKEBASEDIR/-deployJar.sh"
chmod 777 $MAKEBASEDIR/-makejar-coreScript.sh
chmod 777 $DEPLOYSCRIPT
$MAKEBASEDIR/-makejar-coreScript.sh


