echo on

REM The TMP_JAVAC is a directory, which contains only this compiling results. It will be clean in the batch processing.
set TMP_JAVAC=..\..\..\tmp_javac

REM Output dir and jar-file with path and filename relative from current dir:
REM The output dir is exe usually but zbnfjax if this file is compiled in the ZBNF download preparation.
set OUTDIR_JAVAC=..\..\exe
set JAR_JAVAC=Fcmd.jar


REM Manifest-file for jar building relativ path from current dir:
set MANIFEST_JAVAC=Fcmd.manifest

REM Input for javac, only choice of primary sources, relativ path from current (make)-directory:
set INPUT_JAVAC=
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/guiViewCfg/ViewCfg.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/commander/Fcmd.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/vcs/*.java
set INPUT_JAVAC=%INPUT_JAVAC% ../../srcJava_Zbnf/org/vishia/zcmd/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/cfg/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/area9/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/guiBzr/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/guiInspc/*.java

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
set SWTJAR=Z:\V\vishia\Fcmd\sf\Fcmd\exe\windows\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
if exist %SWTJAR% goto :swtOk
echo SWT library not found
pause
exit
:swtOk
::set CLASSPATH_JAVAC=d:\Progs\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
::set CLASSPATH_JAVAC=d:\Progs\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
::set CLASSPATH_JAVAC=Z:\V\vishia\Fcmd\sf\Fcmd\exe\windows\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
set CLASSPATH_JAVAC=%SWTJAR%
::;../../zbnfjax/zbnf.jar;../../exe/vishiaRun.jar

REM Sets the src-path for further necessary sources:
set SRCPATH_JAVAC=..;../../srcJava_vishiaBase;../../srcJava_Zbnf;../../srcJava_vishiaRun

call ..\..\srcJava_vishiaBase\_make\+javacjarbase.bat

set PATH_FCMD=D:\vishia\Fcmd\sf\Fcmd\exe
if exist %PATH_FCMD% copy %OUTDIR_JAVAC%\%JAR_JAVAC% %PATH_FCMD%\%JAR_JAVAC% 
