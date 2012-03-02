
##--------------------------------------------------------------------------------------------
## Environment variables set from zbnfjax:
## JAVA_JDK: Directory where bin/javac is found. This java version will taken for compilation
## The java-copiler may be located at a user-specified position.
## Set the environment variable JAVA_HOME, where bin/javac will be found.
if test "$JAVA_JDK" = "";  then export JAVA_JDK="/usr/share/JDK"; fi


export DST="../../docuSrcJava_vishiaGUI"
export DST_priv="../../docuSrcJava_vishiaGUI_priv"

echo set SRC
export SRC="-subpackages org.vishia.commander:org.vishia.gral"
export SRC="$SRC ../org/vishia/guiBzr/*.java"
export SRC="$SRC ../org/vishia/guiInspc/*.java"
export SRC="$SRC ../org/vishia/guiViewCfg/*.java"
export SRC="$SRC ../org/vishia/windows/*.java"

#echo generate docu: $SRC
echo set SRCpATH
export SRCPATH="..:../../srcJava_vishiaBase:../../srcJava_vishiaRun:../../srcJava_Zbnf"

echo set linkpath
export LINKPATH=""
export LINKPATH="$LINKPATH -link ../docuSrcJava_Zbnf"
export LINKPATH="$LINKPATH -link ../docuSrcJava_vishiaBase"
export LINKPATH="$LINKPATH -link ../docuSrcJava_vishiaRun"

../../srcJava_vishiaBase/_make/+genjavadocbase.sh
