@echo off
echo This file contains the bazaar version of the depending components of this source. >_bzrOtherCmpnVersions.txt
echo That component sources should be gotten and placed in the given path. >>_bzrOtherCmpnVersions.txt

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

set A=srcJava_Zbnf
echo ->>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt
echo ==Component at ..\%A%== >>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt
echo ->>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt
cd ..\%A%
call bzr.bat version-info >>..\srcJava_vishiaGui\_bzrOtherCmpnVersions.txt

cd ..\srcJava_vishiaGui
type _bzrOtherCmpnVersions.txt
pause
