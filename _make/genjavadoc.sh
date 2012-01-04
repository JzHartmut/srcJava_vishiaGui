
##--------------------------------------------------------------------------------------------
## Environment variables set from zbnfjax:
## JAVA_JDK: Directory where bin/javac is found. This java version will taken for compilation
## The java-copiler may be located at a user-specified position.
## Set the environment variable JAVA_HOME, where bin/javac will be found.
if test "$JAVA_JDK" = "";  then export JAVA_JDK="/usr/share/JDK"; fi


export DST="../../javadocGral"
export DST_priv="../../javadocGral_priv"

rm -f -r $DST
rm -f -r $DST_priv

mkdir $DST
mkdir $DST_priv

export SRC=""
export SRC="$SRC  ../org/vishia/guiBzr/*.java"
export SRC="$SRC  ../org/vishia/guiInspc/*.java"
export SRC="$SRC  ../org/vishia/guiViewCfg/*.java"
export SRC="$SRC  ../org/vishia/gral/*.java"
export SRC="$SRC  ../org/vishia/gral/area9/*.java"
export SRC="$SRC  ../org/vishia/gral/awt/*.java"
export SRC="$SRC  ../org/vishia/gral/cfg/*.java"
export SRC="$SRC  ../org/vishia/gral/base/*.java"
export SRC="$SRC  ../org/vishia/gral/ifc/*.java"
export SRC="$SRC  ../org/vishia/gral/widget/*.java"
export SRC="$SRC  ../org/vishia/gral/swt/*.java"
export SRC="$SRC  ../org/vishia/commander/*.java"
export SRC="$SRC  ../org/vishia/commander/target/*.java"
export SRC="$SRC  ../org/vishia/windows/*.java"

echo generate docu: $SRC

$JAVA_JDK/bin/javadoc -d $DST -linksource -notimestamp $SRC   1>$DST/javadoc.rpt 2>$DST/javadoc.err
$JAVA_JDK/bin/javadoc -d $DST_priv -private -linksource -notimestamp $SRC   1>$DST_priv/javadoc.rpt 2>$DST_priv/javadoc.err

mkdir $DST/img
cp -r ../img $DST

mkdir $DST_priv/img
cp -r ../img $DST_priv

cp stylesheet_javadoc.css $DST/stylesheet.css
cp stylesheet_javadoc.css $DST_priv/stylesheet.css

echo successfull generated $DST