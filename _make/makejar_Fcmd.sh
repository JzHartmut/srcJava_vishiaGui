#!/bin/bash

## The TMP_JAVAC is a directory, which contains only this compiling results. It will be clean in the batch processing.
export TMP_JAVAC="../../../tmp_javac"

## Output jar-file with path and filename relative from current dir:
export OUTDIR_JAVAC="../../exe"
export JAR_JAVAC="Fcmd.jar"

## Manifest-file for jar building relativ path from current dir:
export MANIFEST_JAVAC="Fcmd.manifest"


## Input for javac, only choice of primary sources, relativ path from current (make)-directory:
export INPUT_JAVAC="../org/vishia/commander/Fcmd.java"

## Sets the CLASSPATH variable for compilation (used jar-libraries). Do not leaf empty also it aren't needed because it is used in command line.
##This component depends on the following component:
if test -d /usr/share/eclipse/plugins; then export SWTJAR="/usr/share/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_3.6.2.v3659b.jar:/usr/share/eclipse/plugins/org.eclipse.swt_3.6.2.v3659c.jar"; fi
if test -d /d/Progs/Eclipse3_5/plugins; then export SWTJAR="/d/Progs/Eclipse3_5/plugins/org.eclipse.swt.win32.win32.x86_3.5.1.v3555a.jar"; fi

export CLASSPATH_JAVAC="$SWTJAR"

## Sets the src-path for further necessary sources. It is always .. because the make directory is parallel to the java sources of this component.
export SRCPATH_JAVAC="..:../../srcJava_vishiaBase:../../srcJava_Zbnf:../../srcJava_vishiaRun"

##call of javac and jar with preparing directories etc.
../../srcJava_vishiaBase/_make/+javacjarbase.sh

