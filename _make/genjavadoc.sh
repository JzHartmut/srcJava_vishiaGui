
export DST="../../docuSrcJava_vishiaGui"
export DST_priv="../../docuSrcJava_vishiaGui_priv"

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
