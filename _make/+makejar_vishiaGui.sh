echo script +makejar_vishiaGui.sh
echo script $0

#Do not change the version on repeated build, and check the checksum and content of jar.
#If it is equal, it is a reproduces build. The $VERSION is important 
#  because it determines the timestamp and hence the checksum in the jar file. 

#Set the version newly here to the current date if the sources are changed in jar and checksum.
#If the relevant sources are not change in functionality, may be changed in comment, 
#  it is not necessary the change this VERSION because the generated content is the same.
export VERSION="2021-07-01"
export VERSION_VISHIABASE="XX2021-07-01"

# SWT for Windows-64 it is a copy of the used jar, see bom
# comment or uncomment for alternative swt.jar
#export JAR_SWT=""  ##left empty if unversioned should be used
export JAR_SWT="org.eclipse.swt.win32.win32.x86_64_3.110.0.v20190305-0602.jar"
#export JAR_SWT="org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar"   ##32 bit SWT
#export JAR_SWT=LINUX-TODO


# It should have anytime the stamp of the newest file, independing of the VERSION
export SRCZIPFILE="vishiaGui-$VERSION-source.zip"

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
export RESOURCEFILES="..:**/*.zbnf ..:**/*.xml"


# located from this workingdir as currdir for shell execution:
export MANIFEST=vishiaGui.manifest

#$BUILD is the main build output directory. 
#possible to give $BUILD from outside. On argumentless call determine in tmp.
if test "$BUILD" = ""; then export BUILD="/tmp/BuildJava_vishiaGui"; fi

#to store temporary class files:
export TMPJAVAC=$BUILD/javac/

#DEPLOY is the directory where the results are written.
if ! test -d $BUILD/deploy; then mkdir --parent $BUILD/deploy; fi
export DEPLOY="$BUILD/deploy/vishiaGui"


#now run the common script:
chmod 777 ./-makejar-coreScript.sh
./-makejar-coreScript.sh


# Deploy the result
if test -f $DEPLOY-$VERSION.jar; then   ##compilation successfull
  ##
  ## copy to the deploy directory. 
  if test -d ../../../../../../deploy; then
    cp $DEPLOY-$VERSION* ../../../../../../deploy
  elif test -d ../../deploy; then
    cp $DEPLOY-$VERSION* ../../deploy
  fi  
  ##
  ## copy the useable version to a existing libstd directory:
  if test -d ../../../../../../libstd; then ##beside cmpnJava... should be existing
    export DEPLOYPATH="../../../../../../libStd" 
    ##TODO maybe correct the bomVishiaJava.txt via script.jzTc possible
  else
    export DEPLOYPATH="../../jars" 
    if ! test -d $DEPLOYPATH; then mkdir $DEPLOYPATH; fi
  fi  
  if test -v DEPLOYPATH; then
    cp $DEPLOY-$VERSION.jar $DEPLOYPATH/vishiaGui.jar    
    ls -l $DEPLOYPATH
    echo correct the bom file: JZtxtcmd corrBom.jzTc $DEPLOYPATH $BUILD/deploy vishiaBase $VERSION
    java -cp $DEPLOYPATH/vishiaBase.jar org.vishia.jztxtcmd.JZtxtcmd corrBom.jzTc $DEPLOYPATH $BUILD/deploy vishiaGui $VERSION
  fi  
fi  




