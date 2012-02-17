#!/bin/bash

## The TMP_JAVAC is a directory, which contains only this compiling results. It will be clean in the batch processing.
export TMP_JAVAC="../../../tmp_javac"

## The java-copiler may be located at a user-specified position.
## Set the environment variable JAVA_HOME, where bin/javac will be found.
if test "$JAVA_JDK" = "";  then export JAVA_JDK="/usr/share/JDK"; fi
#set PATH="$JAVA_JDK_HOME\bin:$PATH"

## Output jar-file with path and filename relative from current dir:
export OUTPUTFILE_JAVAC="../../exe/Fcmd.jar"

## Manifest-file for jar building relativ path from current dir:
export MANIFEST_JAVAC="Fcmd.manifest"

## Input for javac, only choice of primary sources, relativ path from current (make)-directory:
export INPUT_JAVAC="../org/vishia/commander/Fcmd.java"

## Sets the CLASSPATH variable for compilation (used jar-libraries). Do not leaf empty also it aren't needed because it is used in command line.
##This component depends on the following component:
export CLASSPATH_JAVAC="/usr/share/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_3.6.2.v3659b.jar:/usr/share/eclipse/plugins/org.eclipse.swt_3.6.2.v3659c.jar"

## Sets the src-path for further necessary sources. It is always .. because the make directory is parallel to the java sources of this component.
export SRCPATH_JAVAC="..:../../srcJava_Zbnf:../../srcJava_vishiaRun"

##call of javac and jar with preparing directories etc.
./javacjar.sh

