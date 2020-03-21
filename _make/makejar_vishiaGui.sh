#this file is the user-adapt-able frame for makejar_vishiaGui.sh
#edit some settings if there are different form default.
export TMPJAVAC=/tmp/javac_vishiaGui/build/javac
#export JAVAC_HOME=c:/Programs/Java/jdk1.8.0_211
export JAVAC_HOME=c:/Programs/Java/jdk1.8.0_241
#export JAVAC_HOME=/usr/share/JDK/jdk1.8.0_241




#It determines only the file names, the TIME is inside makejar.sh
export VERSION=2020-03-21

#Output files
export DEPLOY=../vishiaGui-

#copy the depending files locally in the libs directory!
#or change this setting
export JAR_vishiaBase=../libs/vishiaBase-2020-03-20.jar
# SWT for Windows-64
export JAR_SWT=../libs/org.eclipse.swt.win32.win32.x86_64_3.110.0.v20190305-0602.jar

sepPath=":"
if test "$OS" = "Windows_NT"; then sepPath=";"; fi
export CLASSPATH="$JAR_SWT$sepPath$JAR_vishiaBase"
# located from this workingdir as currdir for shell execution.
# Note: include sources of vishiaRun are part of the source.zip
export SRCPATH=".."
export RESOURCEFILES="$SRCPATH:**/*.zbnf $SRCPATH:**/*.xml"
export MANIFEST=vishiaGui.manifest
# FILE1SRC=../org/vishia/jztxtcmd/JZtxtcmd.java
export SRC_ALL=..

#now run the common script:
chmod 777 makejar.sh
./makejar.sh

