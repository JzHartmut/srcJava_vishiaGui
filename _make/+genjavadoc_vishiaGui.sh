#export DSTDIR=D:/vishia/Java/
export DSTDIR=$TMP/_Javadoc/
mkdir $DSTDIR

if ! test -d $DSTDIR; then export DSTDIR=../../; fi
echo %DSTDIR%
export DST=docuSrcJava_vishiaGui
##export DST_priv=docuSrcJavaPriv_vishiaGui

export SRC="-subpackages org.vishia"
export SRCPATH=..
export CLASSPATH=xxxxx
#export LINKPATH=
#export CLASSPATH=xxxxx
export LINKPATH="-link ../docuSrcJava_vishiaBase;../docuSrcJava_vishiaRun"

if test -d ../../srcJava_vishiaBase; then export vishiaBase="../../srcJava_vishiaBase"
else export vishiaBase="../../../../../../cmpnJava_vishiaBase/src/main/java/srcJava_vishiaBase"
fi

$vishiaBase/_make/-genjavadocbase.sh


