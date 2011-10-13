@echo on
::rmdir /S /Q ..\javadoc

set DST=..\..\javadocGral
if exist %DST% rmdir /S /Q %DST%
if exist %DST%_priv rmdir /S /Q %DST%_priv

if not exist %DST% mkdir %DST%
if not exist %DST%_priv mkdir %DST%_priv
call setZBNFJAX_HOME.bat

set SRC=
set SRC=%SRC% ../org/vishia/guiBzr/*.java
set SRC=%SRC% ../org/vishia/guiInspc/*.java
set SRC=%SRC% ../org/vishia/guiViewCfg/*.java
set SRC=%SRC% ../org/vishia/gral/*.java
set SRC=%SRC% ../org/vishia/gral/cfg/*.java
set SRC=%SRC% ../org/vishia/gral/base/*.java
set SRC=%SRC% ../org/vishia/gral/ifc/*.java
set SRC=%SRC% ../org/vishia/gral/widget/*.java
set SRC=%SRC% ../org/vishia/gral/swt/*.java
set SRC=%SRC% ../org/vishia/gral/gridPanel/*.java
set SRC=%SRC% ../org/vishia/gral/area9/*.java
set SRC=%SRC% ../org/vishia/commander/*.java
set SRC=%SRC% ../org/vishia/mainGuiSwt/*.java
set SRC=%SRC% ../org/vishia/windows/*.java

echo generate docu: %SRC%
::this batch should be accessable in the PATH and should be set the JAVA_HOME environment variable:
::call setANT_HOME.bat
echo on

echo generate docu: %SRC%
echo on

%JAVA_HOME%\bin\javadoc.exe -d %DST% -linksource -notimestamp %SRC%  1>%DST%\javadoc.rpt 2>%DST%\javadoc.err
%JAVA_HOME%\bin\javadoc.exe -d %DST%_priv -private -linksource -notimestamp %SRC%  1>%DST%_priv\javadoc.rpt 2>%DST%_priv\javadoc.err

if not exist %DST%\img mkdir %DST%\img
copy ..\img\* %DST%\img\*

if not exist %DST%_priv\img mkdir %DST%_priv\img
copy ..\img\* %DST%_priv\img\*

copy stylesheet_javadoc.css %DST%\stylesheet.css
copy stylesheet_javadoc.css %DST%_priv\stylesheet.css
if "NOPAUSE" == "" pause

