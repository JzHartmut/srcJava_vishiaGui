@echo on
::rmdir /S /Q ..\javadoc
if not exist ..\..\javadoc mkdir ..\..\javadoc
if not exist ..\..\tmp mkdir ..\..\tmp
rem pause
call setZBNFJAX_HOME.bat

set docuSrc=
set docuSrc=%docuSrc% ../org/vishia/guiBzr/*.java
set docuSrc=%docuSrc% ../org/vishia/guiInspc/*.java
set docuSrc=%docuSrc% ../org/vishia/guiViewCfg/*.java
set docuSrc=%docuSrc% ../org/vishia/mainGui/*.java
set docuSrc=%docuSrc% ../org/vishia/mainGui/cfg/*.java
set docuSrc=%docuSrc% ../org/vishia/mainGuiSwt/*.java
set docuSrc=%docuSrc% ../org/vishia/windows/*.java

echo generate docu: %docuSrc%
::this batch should be accessable in the PATH and should be set the JAVA_HOME environment variable:
::call setANT_HOME.bat
::%JAVA_HOME%\bin\javadoc.exe -d ../javadoc -public -notimestamp %docuSrc%  1>..\tmp\javadoc.rpt 2>..\tmp\javadoc.err
echo on
%JAVA_HOME%\bin\javadoc.exe -d ../../javadoc -private -linksource -notimestamp %docuSrc%  1>..\..\tmp\javadoc.rpt 2>..\..\tmp\javadoc.err
copy stylesheet_javadoc.css ..\..\javadoc\stylesheet.css
pause

