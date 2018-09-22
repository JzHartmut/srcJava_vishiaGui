echo off
::cd ..\..\srcJava_Zbnf\_make
::call makejar_zbnf.bat
::cd ..\..\srcJava_vishiaGui\_make
::pause

set DST_Download=..\..\Download\exe

REM The TMP_JAVAC is a directory, which contains only this compiling results. It will be clean in the batch processing.
::set TMP_JAVAC=..\..\..\..\vishia.tmp\tmp_javac
set TMP_JAVAC=T:\vishia.tmp\tmp_javac

REM Output dir and jar-file with path and filename relative from current dir:
REM The output dir is exe usually but zbnfjax if this file is compiled in the ZBNF download preparation.
set OUTDIR_JAVAC=..\..\exe
set JAR_JAVAC=vishiaGral.jar

REM Manifest-file for jar building relativ path from current dir:
set MANIFEST_JAVAC=vishiaGui.manifest

REM Input for javac, only choice of primary sources, relativ path from current (make)-directory:
set INPUT_JAVAC=
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/commander/Fcmd.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gitGui/*.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/*.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/cfg/*.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/ifc/*.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/area9/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/test/*.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/guiInspc/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/guiViewCfg/ViewCfg.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/simSelector/*.java

set COPYJAR=..\org\vishia\gral\cfg\*.zbnf %TMP_JAVAC%\bin\org\vishia\gral\cfg\*

REM Sets the CLASSPATH variable for compilation (used jar-libraries). do not leaf empty also it aren't needed:
REM This component based on the ZBNF and the vishiaRun.

set SWTJAR=..\..\..\Java\Download\swt\windows\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
if exist %SWTJAR% goto :swtOk
set SWTJAR=%FCMD_DST%\windows\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
if exist %SWTJAR% goto :swtOk
::set SWTJAR=d:\Programs\XML_Tools\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
::if exist %SWTJAR% goto :swtOk
::set SWTJAR=d:\Progs\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
::if exist %SWTJAR% goto :swtOk
::set SWTJAR=c:\Progs\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
::if exist %SWTJAR% goto :swtOk
::set SWTJAR=d:\Programme\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
::if exist %SWTJAR% goto :swtOk
echo SWT library not found
pause
exit
:swtOk
REM The viahiaGui depends on zbnf.jar.
REM SRCvishiaBase used for compile batch script only.
set SRCvishiaBase=..\..\..\ZBNF\srcJava_Zbnf
set ZBNFJAR=..\..\..\ZBNF\zbnfjax\zbnf.jar

if exist %ZBNFJAR% goto :ZbnfOk
set SRCvishiaBase=..\..\srcJava_vishiaBase
set ZBNFJAR=..\..\exe\zbnf.jar
:ZbnfOk
set CLASSPATH_JAVAC=%SWTJAR%;%ZBNFJAR%
echo classpath: %CLASSPATH_JAVAC%
pause
echo on

REM Sets the src-path for further necessary sources:
::set SRCPATH_JAVAC=..;../../srcJava_Zbnf;../../srcJava_vishiaRun
set SRCPATH_JAVAC=..;../../srcJava_vishiaRun

call %SRCvishiaBase%\_make\+javacjarbase.bat

if exist %DST_Download% copy %OUTDIR_JAVAC%\%JAR_JAVAC% %DST_Download%\%JAR_JAVAC% 
pause

