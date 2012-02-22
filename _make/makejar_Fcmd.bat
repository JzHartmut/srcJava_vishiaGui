echo off
REM The java-copiler may be located at a user-specified position.
REM Set the environment variable JAVA_HOME, where bin/javac will be found.
if "%JAVA_HOME%" == "" goto :searchJava
if not exist %JAVA_HOME% goto :searchJava
goto :okJavaHome
:searchJava
  if exist D:\Progs\JAVA\jdk1.6.0_21 set JAVA_HOME=D:\Progs\JAVA\jdk1.6.0_21
  if exist D:\Programs\JAVA\jdk1.6.0_21 set JAVA_HOME=D:\Programs\JAVA\jdk1.6.0_21
  if exist C:\Programs\JAVA\jdk1.6.0_21 set JAVA_HOME=C:\Programs\JAVA\jdk1.6.0_21
  if exist D:\Progs\JAVA\jdk1.6.0_21 set JAVA_HOME=D:\Progs\JAVA\jdk1.6.0_21
:okJavaHome
echo Java-JDK found at %JAVA_HOME%
::set PATH=%JAVA_HOME%\bin;%PATH%


REM The TMP_JAVAC is a directory, which contains only this compiling results. It will be clean in the batch processing.
set TMP_JAVAC=..\..\..\tmp_javac

REM Output jar-file with path and filename relative from current dir. It is beside the srcJava-directory:
set OUTPUTFILE_JAVAC=..\..\exe\Fcmd.jar
if not exist ..\..\exe mkdir ..\..\exe

REM Manifest-file for jar building relativ path from current dir:
set MANIFEST_JAVAC=Fcmd.manifest

REM Input for javac, only choice of primary sources, relativ path from current (make)-directory:
set INPUT_JAVAC=
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/guiViewCfg/ViewCfg.java
set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/commander/Fcmd.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/cfg/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/gral/area9/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/guiBzr/*.java
::set INPUT_JAVAC=%INPUT_JAVAC% ../org/vishia/guiInspc/*.java

REM Sets the CLASSPATH variable for compilation (used jar-libraries).
REM This component based on the ZBNF and the vishiaRun.

set SWTJAR=d:\Progs\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
if exist %SWTJAR% goto :swtOk
set SWTJAR=Z:\V\vishia\Fcmd\sf\Fcmd\exe\windows\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
if exist %SWTJAR% goto :swtOk
:swtOk
::set CLASSPATH_JAVAC=d:\Progs\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
::set CLASSPATH_JAVAC=d:\Progs\Eclipse3_5\plugins\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
::set CLASSPATH_JAVAC=Z:\V\vishia\Fcmd\sf\Fcmd\exe\windows\org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar
set CLASSPATH_JAVAC=%SWTJAR%
::;../../zbnfjax/zbnf.jar;../../exe/vishiaRun.jar

REM Sets the src-path for this component, maybe for further necessary sources:
set SRCPATH_JAVAC=..;../../srcJava_Zbnf;../../srcJava_vishiaRun

REM Call java-compilation and jar with given input environment. This following commands are the same for all java-compilations.
echo on
if exist %TMP_JAVAC% rmdir /S /Q %TMP_JAVAC%
mkdir %TMP_JAVAC%
mkdir %TMP_JAVAC%\bin
%JAVA_HOME%\bin\javac.exe -deprecation -d %TMP_JAVAC%/bin -sourcepath %SRCPATH_JAVAC% -classpath %CLASSPATH_JAVAC% %INPUT_JAVAC% 1>>%TMP_JAVAC%\javac_ok.txt 2>%TMP_JAVAC%\error.txt
echo on
if errorlevel 1 goto :error
echo copiling successfull, generate jar:

set ENTRYDIR=%CD%
cd %TMP_JAVAC%\bin
echo jar -c
%JAVA_HOME%\bin\jar.exe -cvfm %ENTRYDIR%/%OUTPUTFILE_JAVAC% %ENTRYDIR%/%MANIFEST_JAVAC% *  >>../error.txt
if errorlevel 1 goto :error
cd %ENTRYDIR%

pause
goto :ende

:error
  type %TMP_JAVAC%\error.txt
  pause
  goto :ende

:ende
