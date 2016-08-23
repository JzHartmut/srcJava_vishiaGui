@echo off
echo This file contains the bazaar version of the depending components of this source. >_bzrOtherCmpnVersions.txt
echo That component sources should be gotten and placed in the given path. >>_bzrOtherCmpnVersions.txt

rem This is the path to all archives for this computer, correct it if necessary:
if "%BZR_ARCHIVEPATH%" == "" set BZR_ARCHIVEPATH=D:/Bzr


set A=srcJava_vishiaBase
echo ->>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt
echo ==Component at ..\%A%== >>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt
echo ->>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt
cd ..\%A%
call bzr.bat version-info >>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt

set A=srcJava_vishiaRun
echo ->>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt
echo ==Component at ..\%A%== >>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt
echo ->>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt
cd ..\%A%
call bzr.bat version-info >>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt
cd ..\srcJava_vishiaGui

set A=srcJava_Zbnf
set DIR=%CD%
if exist ..\%A%\.bzr set OTHDIR=..\%A%
if not exist ..\%A% set OTHDIR=%BZR_ARCHIVEPATH%\%A%
if not exist %OTHDIR%\.bzr goto :errorOthCmpn
echo Component: %OTHDIR%\.bzr
echo OwnDir: %DIR%
pause
echo ->>%DIR%\_bzrOtherCmpnVersions.txt
echo ==Component at == %OTHDIR%==>>%DIR%\_bzrOtherCmpnVersions.txt
echo ->>%DIR%\_bzrOtherCmpnVersions.txt
cd /D %OTHDIR%
call bzr.bat version-info >>%DIR%\_bzrOtherCmpnVersions.txt
cd /D %DIR%
goto :ok
:errorOthCmpn
echo Component not found: %OTHDIR%\.bzr
pause
:ok

cd ..\srcJava_vishiaGui
type _bzrOtherCmpnVersions.txt
pause
