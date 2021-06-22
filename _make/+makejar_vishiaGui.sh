echo script +makejar_vishiaGui.sh
echo script $0

#Do not change the version on repeated build, and check the checksum and content of jar.
#If it is equal, it is a reproduces build. The $VERSION is important 
#  because it determines the timestamp and hence the checksum in the jar file. 

#Set the version newly here to the current date if the sources are changed in jar and checksum.
#If the relevant sources are not change in functionality, may be changed in comment, 
#  it is not necessary the change this VERSION because the generated content is the same.
export VERSION="2021-06-21"

#It should have anytime the stamp of the newest file, independing of the VERSION
export SRCZIPFILE="vishiaGui-$VERSION-source.zip"

#Note: Select the proper vishiaBase, ../libs/.. for manually build
##export JAR_vishiaBase=../libs/vishiaBase-2020-07-16.jar
export JAR_vishiaBase=../../../../../../cmpnJava_vishiaBase/deploy/vishiaBase-2021-06-21.jar

#It is also the tool for zip and jar
export JAR_zipjar=$JAR_vishiaBase

# SWT for Windows-64 it is a copy of the used jar, see bom
export JAR_SWT=org.eclipse.swt.win32.win32.x86_64.jar

if test "$OS" = "Windows_NT"; then export sepPath=";"; else export sepPath=":"; fi
#The CLASSPATH is used for reference jars for compilation which should be present on running too.
##export CLASSPATH=../libs/"$JAR_SWT$sepPath$JAR_vishiaBase"
export CLASSPATH=../../../../../libs/"$JAR_SWT$sepPath$JAR_vishiaBase"

#determine the sources:
# Note: include sources of vishiaRun are part of the source.zip
export SRC_ALL=".."
export SRC_ALL2=""
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
chmod 777 makejar.sh
./-makejar-coreScript.sh


if test -f $DEPLOY-$VERSION.jar -a -d D:/vishia/Java/libStd; then
  cp $DEPLOY-$VERSION.jar D:/vishia/Java/libStd/vishiaGui.jar
  ls -l D:/vishia/Java/libStd
fi  

