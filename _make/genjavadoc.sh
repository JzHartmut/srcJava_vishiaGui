
##--------------------------------------------------------------------------------------------
## Environment variables set from zbnfjax:
## JAVA_JDK: Directory where bin/javac is found. This java version will taken for compilation
## The java-copiler may be located at a user-specified position.
## Set the environment variable JAVA_HOME, where bin/javac will be found.
if test "$JAVA_JDK" = "";  then export JAVA_JDK="/usr/share/JDK"; fi


export DST="..\..\javadocGral"
export DST_priv="..\..\javadocGral_priv"

rm -f $DST
rm -f $DST_priv

mkdir $DST
mkdir $DST_priv

set SRC=""
set SRC="$SRC  ../org/vishia/guiBzr/*.java"
set SRC="$SRC  ../org/vishia/guiInspc/*.java"
set SRC="$SRC  ../org/vishia/guiViewCfg/*.java"
set SRC="$SRC  ../org/vishia/gral/*.java"
set SRC="$SRC  ../org/vishia/gral/area9/*.java"
set SRC="$SRC  ../org/vishia/gral/awt/*.java"
set SRC="$SRC  ../org/vishia/gral/cfg/*.java"
set SRC="$SRC  ../org/vishia/gral/base/*.java"
set SRC="$SRC  ../org/vishia/gral/ifc/*.java"
set SRC="$SRC  ../org/vishia/gral/widget/*.java"
set SRC="$SRC  ../org/vishia/gral/swt/*.java"
set SRC="$SRC  ../org/vishia/commander/*.java"
set SRC="$SRC  ../org/vishia/commander/target/*.java"
        set SRC="$SRC  ../org/vishia/windows/*.java"
export SRC

echo generate docu: $SRC
::this batch should be accessable in the PATH and should be set the JAVA_HOME environment variable:
::call setANT_HOME.bat
echo on

echo generate docu: $SRC
echo on

$JAVA_JDK/bin/javadoc -d $DST -linksource -notimestamp $SRC   1>$DST\javadoc.rpt 2>$DST\javadoc.err
$JAVA_JDK/bin/javadoc -d $DST -private -linksource -notimestamp $SRC   1>$DST_priv\javadoc.rpt 2>$DST_priv\javadoc.err

mkdir $DST\img
copy ../img/* $DST/img/*

mkdir $DST_priv/img
copy ../img/* $DST_priv/img/*

copy stylesheet_javadoc.css $DST/stylesheet.css
copy stylesheet_javadoc.css $DST_priv/stylesheet.css

pc/*.java"
set SRC="$SRC  ../org/vishia/guiViewCfg/*.java"
set SRC="$SRC  ../org/vishia/gral/*.java"
set SRC="$SRC  ../org/vishia/gral/area9/*.java"
set SRC="$SRC  ../org/vishia/gral/awt/*.java"
set SRC="$SRC  ../org/vishia/gral/cfg/*.java"
set SRC="$SRC  ../org/vishia/gral/base/*.java"
set SRC="$SRC  ../org/vishia/gral/ifc/*.java"
set SRC="$SRC  ../org/vishia/gral/widget/*.java"
set SRC="$SRC  ../org/vishia/gral/swt/*.java"
set SRC="$SRC  ../org/vishia/commander/*.java"
set SRC="$SRC  ../org/vishia/commander/target/*.java"
        