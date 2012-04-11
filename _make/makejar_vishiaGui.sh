#!/bin/bash

## The TMP_JAVAC is a directory, which contains only this compiling results. It will be clean in the batch processing.
export TMP_JAVAC="../../../tmp_javac"

## Output jar-file with path and filename relative from current dir:
export OUTDIR_JAVAC="../../exe"
export JAR_JAVAC="vishiaGUI.jar"

## Manifest-file for jar building relativ path from current dir:
export MANIFEST_JAVAC="vishiaGUI.manifest"
## Input for javac, only choice of primary sources, relativ path from current (make)-directory:
INPUT_JAVAC=""
INPUT_JAVAC="$INPUT_JAVAC ../org/vishia/gral/area9/*.java"
INPUT_JAVAC="$INPUT_JAVAC ../org/vishia/gral/base/*.java"
INPUT_JAVAC="$INPUT_JAVAC ../org/vishia/gral/cfg/*.java"
INPUT_JAVAC="$INPUT_JAVAC ../org/vishia/gral/ifc/*.java"
INPUT_JAVAC="$INPUT_JAVAC ../org/vishia/gral/swt/*.java"
INPUT_JAVAC="$INPUT_JAVAC ../org/vishia/gral/widget/*.java"
INPUT_JAVAC="$INPUT_JAVAC ../org/vishia/gral/awt/*.java"
INPUT_JAVAC="$INPUT_JAVAC ../org/vishia/gral/example/*.java"
INPUT_JAVAC="$INPUT_JAVAC ../org/vishia/guiBzr/*.java"
INPUT_JAVAC="$INPUT_JAVAC ../org/vishia/commander/*.java"
export INPUT_JAVAC

## Sets the CLASSPATH variable for compilation (used jar-libraries). Do not leaf empty also it aren't needed because it is used in command line.
##This component depends on the following component:
export CLASSPATH_JAVAC="/usr/share/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_3.6.2.v3659b.jar:/usr/share/eclipse/plugins/org.eclipse.swt_3.6.2.v3659c.jar:../../exe/vishiaBase.jar:../../exe/zbnf.jar:../../exe/vishiaRun.jar"

## Sets the src-path for further necessary sources. It is always .. because the make directory is parallel to the java sources of this component.
export SRCPATH_JAVAC=".."

##call of javac and jar with preparing directories etc.
../../srcJava_vishiaBase/_make/+javacjarbase.sh

