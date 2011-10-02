@echo on
::rmdir /S /Q ..\javadoc
if not exist ..\..\javadocGUI mkdir ..\..\javadocGui
if not exist ..\..\javadocGUIPriv mkdir ..\..\javadocGuiPriv
if not exist ..\..\tmp mkdir ..\..\tmp
rem pause
call setZBNFJAX_HOME.bat

set docuSrc=
set docuSrc=%docuSrc% ../org/vishia/guiBzr/*.java
set docuSrc=%docuSrc% ../org/vishia/guiInspc/*.java
set docuSrc=%docuSrc% ../org/vishia/guiViewCfg/*.java
set docuSrc=%docuSrc% ../org/vishia/gral/*.java
set docuSrc=%docuSrc% ../org/vishia/gral/cfg/*.java
set docuSrc=%docuSrc% ../org/vishia/gral/base/*.java
set docuSrc=%docuSrc% ../org/vishia/gral/ifc/*.java
set docuSrc=%docuSrc% ../org/vishia/gral/widget/*.java
set docuSrc=%docuSrc% ../org/vishia/gral/swt/*.java
set docuSrc=%docuSrc% ../org/vishia/gral/gridPanel/*.java
set docuSrc=%docuSrc% ../org/vishia/gral/area9/*.java
set docuSrc=%docuSrc% ../org/vishia/commander/*.java
set docuSrc=%docuSrc% ../org/vishia/mainGuiSwt/*.java
set docuSrc=%docuSrc% ../org/vishia/windows/*.java

echo generate docu: %docuSrc%
::this batch should be accessable in the PATH and should be set the JAVA_HOME environment variable:
::call setANT_HOME.bat
::%JAVA_HOME%\bin\javadoc.exe -d ../javadoc -public -notimestamp %docuSrc%  1>..\tmp\javadoc.rpt 2>..\tmp\javadoc.err
echo on

%JAVA_HOME%\bin\javadoc.exe -d ../../javadocGUI -linksource -notimestamp %docuSrc%  1>..\..\javadocGUI\javadoc.rpt 2>..\..\javadocGUI\javadoc.err
%JAVA_HOME%\bin\javadoc.exe -d ../../javadocGUIPriv -private -linksource -notimestamp %docuSrc%  1>..\..\javadocGUIPriv\javadoc.rpt 2>..\..\javadocGUIPriv\javadoc.err
copy stylesheet_javadoc.css ..\..\javadocGUI\stylesheet.css
copy stylesheet_javadoc.css ..\..\javadocGUIPriv\stylesheet.css
pause

