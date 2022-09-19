REM jzcmd should be a batch file able to found in the system's path.
REM for jzcmd.bat see .../zbnfjax/batch_template/jzcmd.bat.
REM to use this tool the download www.vishia.org/ZBNF/download/.../zbnfjax.zip is necessary.
##java -cp ../../
call jztxtcmd.bat genXMI_vishiaGui.bat --log=D:/tmp/tmpXml/vishiaGui2Xmi.log --loglevel=333
pause
exit /B

==JZcmd==

##!checkJZcmd=<:><&$TMP>/tmpJZcmd_CHECK_<&scriptfile>.xml<.>;

currdir=<:><&scriptdir><.>;
include ../../java_vishiaBase/makeScripts/Java2Xmi.jzcmd;  ##contains the program to create XMI

Filepath xmldir = T:/tmp/tmpXml;


Fileset src =
( org/vishia/gral/ifc/*.java
, org/vishia/gral/area9/*.java
, org/vishia/gral/base/*.java
, org/vishia/gral/cfg/*.java
, org/vishia/gral/swt/*.java
, org/vishia/gral/widget/*.java
, org/vishia/guiInspc/*.java
, org/vishia/guiViewCfg/*.java
, org/vishia/commander/*.java
, ../srcJava_vishiaRun:org/vishia/inspcPC/mng/*.java
, ../srcJava_vishiaRun:org/vishia/inspcPC/accTarget/*.java
, ../srcJava_vishiaBase/org/vishia/util/*.java
, ../srcJava_vishiaBase/org/vishia/byteData/*.java
);



main(){
  
  //zmake $xmldir/*.xml := parseJava2Xml(..&src);

  zmake ../rpy/vishiaGui.xmi := genXMI(..:&src, tmpxml=xmldir);

}

