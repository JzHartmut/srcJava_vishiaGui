echo on
::cd ..\..\srcJava_Zbnf\_make
::call makejar_zbnf.bat
::cd ..\..\srcJava_vishiaGui\_make
::pause

REM The TMP_JAVAC is a directory, which contains only this compiling results. It will be clean in the batch processing.
set TMP_JAVAC=..\..\..\tmp_javac

REM Output dir and jar-file with path and filename relative from current dir:
REM The output dir is exe usually but zbnfjax if this file is compiled in the ZBNF download preparation.
set OUTDIR_JAVAC=..\..\exe
set JAR_JAVAC=Fcmd.jar

REM to search swt library and copy jar-result:
set FCMD_DST=D:\vishia\Fcmd\sf\Fcmd\exe


REM Manifest-file for jar building relativ path from current dir:
set MANIFEST_JAVAC=Fcmd.manifest

REM Input for javac, only choice of primary sources, relativ path from current (make)-directory:
set INPUT_JAVAC=
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/commander/Fcmd.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gitGui/*.java

REM Sets the CLASSPATH variable for compilation (used jar-libraries). do not leaf empty also it aren't needed:
REM This component based on the ZBNF and the vishiaRun.

set SWTJAR=d:\Programs\XML_Tools\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
if exist %SWTJAR% goto :swtOk
set SWTJAR=d:\Progs\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
if exist %SWTJAR% goto :swtOk
set SWTJAR=c:\Progs\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
if exist %SWTJAR% goto :swtOk
set SWTJAR=d:\Programme\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
if exist %SWTJAR% goto :swtOk
set SWTJAR=%FCMD_DST%\windows\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
if exist %SWTJAR% goto :swtOk
echo SWT library not found
pause
exit
:swtOk
REM The viahiaGui depends on zbnf.jar.
REM SRCvishiaBase used for compile batch script only.
set SRCvishiaBase=..\..\..\ZBNF\srcJava_vishiaBase
set ZBNFJAR=..\..\..\ZBNF\zbnfjax\zbnf.jar

if exist %ZBNFJAR% goto :ZbnfOk
set SRCvishiaBase=..\..\srcJava_vishiaBase
set ZBNFJAR=..\..\exe\zbnf.jar
:ZbnfOk
set CLASSPATH_JAVAC=%SWTJAR%;%ZBNFJAR%
echo classpath: %CLASSPATH_JAVAC%
pause

REM Sets the src-path for further necessary sources:
::set SRCPATH_JAVAC=..;../../srcJava_vishiaBase;../../srcJava_Zbnf;../../srcJava_vishiaRun
set SRCPATH_JAVAC=..;../../srcJava_vishiaRun

call %SRCvishiaBase%\_make\+javacjarbase.bat

if exist %FCMD_DST% copy %OUTDIR_JAVAC%\%JAR_JAVAC% %FCMD_DST%\%JAR_JAVAC% 
pause

