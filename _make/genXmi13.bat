echo on
set TMP_ZBNFJAX=..\..\tmp_Xmi_vishiaGui
::call .\setZBNFJAX_HOME_Sbox.bat

set INPUT=genXmi13.bat
call zbnfjax zmakeGen %INPUT% "-tmp=%TMP_ZBNFJAX%" "-zbnf=xsl/ZmakeStd.zbnf" "-genCtrl=zmake/Java2XMI.zmake" "-o=_genXMI.bat" "--report=%TMP_ZBNFJAX%/%INPUT%.rpt"
call _genXMI.bat
pause
exit /B


ZMAKE_RULES:

$inputJava=fileset
( 
, org/vishia/gral/ifc/*.java
, org/vishia/gral/gridPanel/*.java
, org/vishia/gral/area9/*.java
, org/vishia/gral/base/*.java
, org/vishia/gral/cfg/*.java
, org/vishia/gral/swt/*.java
, org/vishia/gral/widget/*.java
, org/vishia/mainGuiSwt/*.java
, org/vishia/commander/*.java
);

rpy/gral.xmi:= genXMI($inputJava); 
