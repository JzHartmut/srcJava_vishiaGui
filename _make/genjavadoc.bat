
set DSTDIR=..\..\
set DST=docuSrcJava_vishiaGui
set DST_priv=docuSrcJavaPriv_vishiaGui

echo set SRC
::set SRC=-subpackages org.vishia.gral org.vishia.commander org\vishia\guiInspc
set SRC=-subpackages org.vishia
::set SRC=%SRC% ..\org\vishia\guiBzr\*.java
::set SRC=%SRC% ..\org\vishia\guiInspc\*.java
::set SRC=%SRC% ..\org\vishia\guiViewCfg\*.java
::set SRC=%SRC% ..\org\vishia\windows\*.java

set SRCPATH=..;..\..\srcJava_vishiaBase;..\..\srcJava_vishiaRun;..\..\srcJava_Zbnf

echo set linkpath
set LINKPATH=
set LINKPATH=%LINKPATH% -link ..\docuSrcJava_Zbnf
set LINKPATH=%LINKPATH% -link ..\docuSrcJava_vishiaBase
set LINKPATH=%LINKPATH% -link ..\docuSrcJava_vishiaRun

..\..\srcJava_vishiaBase\_make\+genjavadocbase.bat
