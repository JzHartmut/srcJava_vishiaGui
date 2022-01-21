echo =========================================================================
echo execute  $0
echo " ... generates the vishiaBase.jar from srcJava_vishiaBase core sources"

#Do not change the version on repeated build, and check the checksum and content of jar.
#If it is equal, it is a reproduces build. The $VERSIONSTAMP is important 
#  because it determines the timestamp and hence the checksum in the jar file. 

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
export TIMEinJAR="2021-12-19+00:00"

## Determine a dedicated vishiaBase-yyyy-mm-dd.jar or deactivate it to use the current vishiaBase.jar:
export VERSION_VISHIABASE="XX2021-07-01"

# SWT for Windows-64 it is a copy of the used jar, see bom
# comment or uncomment for alternative swt.jar
#export JAR_SWT=""  ##left empty if unversioned should be used
export JAR_SWT="org.eclipse.swt.win32.win32.x86_64_3.110.0.v20190305-0602.jar"
#export JAR_SWT="org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar"   ##32 bit SWT
#export JAR_SWT=LINUX-TODO


# It should have anytime the stamp of the newest file, independing of the VERSIONSTAMP
export SRCZIPFILE="vishiaGui-$VERSIONSTAMP-source.zip"

# Select the location and the proper vishiaBase
if test -f ../../../../../../deploy/vishiaBase-$VERSION_VISHIABASE.jar
then export JAR_vishiaBase="../../../../../../deploy/vishiaBase-VERSION_VISHIABASE.jar"
elif test -f ../../deploy/vishiaBase-$VERSION_VISHIABASE.jar
then export JAR_vishiaBase="../../deploy/vishiaBase-$VERSION_VISHIABASE.jar"
elif test -f ../../jars/vishiaBase.jar
then export JAR_vishiaBase="../../jars/vishiaBase.jar"
elif test -f ../../../../../../libstd/vishiaBase.jar
then export JAR_vishiaBase="../../../../../../libstd/vishiaBase.jar"
elif test -f ../../../../../../libs/vishiaBase.jar
then export JAR_vishiaBase="../../../../../../libs/vishiaBase.jar"
else
  echo vishiaBase.jar not found, abort
fi
echo JAR_vishisBase=$JAR_vishiaBase

# Select the location and the proper SWT
if test -f ../../../../../../deploy/$JAR_SWT
then export JAR_vishiaBase="../../../../../../deploy/$JAR_SWT"
elif test -f ../../deploy/$JAR_SWT
then export JAR_vishiaBase="../../deploy/$JAR_SWT"
elif test -f ../../jars/org.eclipse.swt.win32_x86_64.jar
then export JARPATH_SWT="../../jars/org.eclipse.swt.win32_x86_64.jar"
elif test -f ../../jars/org.eclipse.swt.win32_x86.jar
then export JARPATH_SWT="../../jars/org.eclipse.swt.win32_x86.jar"
elif test -f ../../jars/org.eclipse.swt.gtk.linux.x86_64.jar
then export JARPATH_SWT="../../jars/org.eclipse.swt.gtk.linux.x86_64.jar"
elif test -f ../../../../../../libstd/org.eclipse.swt.win32_x86_64.jar
then export JARPATH_SWT="../../../../../../libstd/org.eclipse.swt.win32_x86_64.jar"
elif test -f ../../../../../../libstd/org.eclipse.swt.win32_x86.jar
then export JARPATH_SWT="../../../../../../libstd/org.eclipse.swt.win32_x86.jar"
elif test -f ../../../../../../libstd/org.eclipse.swt.gtk.linux.x86_64.jar
then export JARPATH_SWT="../../../../../../libstd/org.eclipse.swt.gtk.linux.x86_64.jar"
elif test -f ../../../../../../libs/org.eclipse.swt.win32_x86_64.jar
then export JARPATH_SWT="../../../../../../libs/org.eclipse.swt.win32_x86_64.jar"
elif test -f ../../../../../../libs/org.eclipse.swt.win32_x86.jar
then export JARPATH_SWT="../../../../../../libs/org.eclipse.swt.win32_x86.jar"
elif test -f ../../../../../../libs/org.eclipse.swt.gtk.linux.x86_64.jar
then export JARPATH_SWT="../../../../../../libs/org.eclipse.swt.gtk.linux.x86_64.jar"
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
export SRC_ALL=".."
if test -d ../../srcJava_vishiaRun; then export SRC_ALL2="../../srcJava_vishiaRun"
else export SRC_ALL2="../../../../../../cmpnJava_vishiaRun/src/main/java/srcJava_vishiaRun"
fi
export SRCPATH="$SRC_ALL$sepPath$SRC_ALL2"
#either both source trees are face to face, or the cmpn are so
export SRCPATH="$SRC_ALL"

# Resourcefiles for files in the jar
export RESOURCEFILES="..:**/*.zbnf ..:**/*.xml ..:**/*.png ..:**/*.txt"


# located from this workingdir as currdir for shell execution:
export MANIFEST=vishiaGui.manifest

#$BUILD_TMP is the main build output directory. 
#possible to give $BUILD_TMP from outside. On argumentless call determine in tmp.
if test "$BUILD_TMP" = ""; then export BUILD_TMP="/tmp/BuildJava_vishiaGui"; fi

#to store temporary class files:
export TMPJAVAC=$BUILD_TMP/javac/

if ! test -d $BUILD_TMP/deploy; then mkdir --parent $BUILD_TMP/deploy; fi
export DSTNAME="vishiaGui"


#now run the common script:
chmod 777 ./-makejar-coreScript.sh
./-makejar-coreScript.sh


# Deploy the result
if test ! -f $BUILD_TMP/deploy/$DSTNAME-$VERSIONSTAMP.jar; then   ##compilation not successfull
  echo "?????? compiling ERROR, abort ????????????????????????" 
  exit 255
else                                                       ##compilation not successfull
  ##
  ## copy the useable version to a existing tools directory:
  if test -d ../../../../../../tools; then ##beside cmpnJava... should be existing
    export CURRENT_JARS_PATH="../../../../../../tools" 
  else
    export CURRENT_JARS_PATH="../../jars" 
    if ! test -d $CURRENT_JARS_PATH; then mkdir $CURRENT_JARS_PATH; fi
  fi  
  if test -v CURRENT_JARS_PATH; then
    echo test and correct the bom file: JZtxtcmd corrBom.jzTc $CURRENT_JARS_PATH $BUILD_TMP/deploy vishiaBase $VERSIONSTAMP
    java -cp $CURRENT_JARS_PATH/vishiaBase.jar org.vishia.jztxtcmd.JZtxtcmd corrBom.jzTc $CURRENT_JARS_PATH $BUILD_TMP/deploy vishiaGui $VERSIONSTAMP
    echo ========================================================================== $?
    if test ! -f $CURRENT_JARS_PATH/vishiaGui*_old.jar; then
      echo "BOM not changed, unchanged MD5"
    else
      cp $BUILD_TMP/deploy/$DSTNAME-$VERSIONSTAMP.jar $CURRENT_JARS_PATH/vishiaGui.jar    
      echo create BOM file $CURRENT_JARS_PATH/bomVishiaJava.new.txt
      ls -l $CURRENT_JARS_PATH
      ##
      ## copy to the deploy directory.
      if test -d ../../../../../../deploy; then
        cp $BUILD_TMP/deploy/$DSTNAME-$VERSIONSTAMP* ../../../../../../deploy
      fi
      if test -d ../../deploy; then
        cp $BUILD_TMP/deploy/$DSTNAME-$VERSIONSTAMP* ../../deploy
      fi  
    fi  
    echo ======= success ==========================================================
  fi  
fi  




