echo off
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

::set SRCPATH=..;..\..\srcJava_vishiaBase;..\..\srcJava_Zbnf;..\..\srcJava_vishiaRun
set SRCPATH=..;..\..\..\ZBNF\srcJava_Zbnf;..\..\srcJava_vishiaRun
set CLASSPATH=..\..\jar\org.eclipse.swt.win32.win32.x86_64.jar

echo set linkpath
set LINKPATH=
set LINKPATH=%LINKPATH% -link ..\docuSrcJava_Zbnf
set LINKPATH=%LINKPATH% -link ..\docuSrcJava_vishiaRun

..\..\..\ZBNF\srcJava_Zbnf\_make\+genjavadocbase.bat
