#this file is the user-adapt-able frame for makejar_vishiaGui.sh
#edit some settings if there are different form default.
#export JAVAC_HOME=c:/Programs/Java/jdk1.8.0_211
export JAVAC_HOME=c:/Programs/Java/jdk1.8.0_241
#export JAVAC_HOME=/usr/share/JDK/jdk1.8.0_241

##build zipjar:
#Note: on changes firstly build with the same given date here.
#Then check the checksum. If it matches, this VERSION date is ok.
#For example if only a comment is changed in sources or nothing is changed,
#the repeated built should deliver a reproducible build.
#-----
#If the checksum and the content does not match, 
# then set the new date here from the last timestamp of the last commit or changed file.
export VERSION="2020-06-09"
##TODO: on next reproducible mismatch use Timestamp +00:00. 
##      If it is done for all, remove TIME to the core script.
##      =>Lesser complexity 
export TIME="$VERSION+00:00"    #check generated content!
export TMPJAVAC=$TMP/javac_vishiaGui/javac
export DEPLOY=vishiaGui

export JAR_vishiaBase=../libs/vishiaBase-2020-06-09.jar
export JAR_zipjar=$JAR_vishiaBase
# SWT for Windows-64
export JAR_SWT=../libs/org.eclipse.swt.win32.win32.x86_64_3.110.0.v20190305-0602.jar

sepPath=":"
if test "$OS" = "Windows_NT"; then sepPath=";"; fi
export CLASSPATH="$JAR_SWT$sepPath$JAR_vishiaBase"
# located from this workingdir as currdir for shell execution.
# Note: include sources of vishiaRun are part of the source.zip
export SRC_ALL=".."
#either both source trees are face to face, or the cmpn are so
if test -d ../../srcJava_vishiaRun; then export SRC_ALL2="../../srcJava_vishiaRun"
else export SRC_ALL2="../../../../../../cmpnJava_vishiaRun/src/main/java/srcJava_vishiaRun"
fi
export SRCPATH="$SRC_ALL$sepPath$SRC_ALL2"
export RESOURCEFILES="..:**/*.zbnf ..:**/*.xml"



# located from this workingdir as currdir for shell execution:

export MANIFEST=vishiaGui.manifest
# FILE1SRC=../org/vishia/jztxtcmd/JZtxtcmd.java



#now run the common script:
chmod 777 makejar.sh
./-makejar-coreScript.sh

