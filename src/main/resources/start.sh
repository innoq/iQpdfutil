#!/usr/bin/env bash

##############################################################################
##
##  iq-pdfutil
##
##  https://github.com/innoq/iQpdfutil
##
##############################################################################

PRG="$0"

CURRENT_DIR="`pwd`"

cd "`dirname $PRG`"
APP_DIR="`pwd -P`"
cd $CURRENT_DIR

LIB="$APP_DIR/lib"
CP=$LIB_DIR/itextpdf-5.0.6.jar:$LIB_DIR/bcmail-jdk14-1.38.jar:$LIB_DIR/bcprov-jdk14-1.38.jar:$LIB_DIR/bctsp-jdk14-1.38.jar

java -classpath "$CP" -jar $APP_DIR/iq-pdfutil-0.1.0-SNAPSHOT.jar $*
