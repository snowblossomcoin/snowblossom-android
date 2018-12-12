#!/bin/bash

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root"
   exit 1
fi

source config.sh

if id -u snowbuilder &>/dev/null ; then
	pkill -u snowbuilder
	sleep 3s
	deluser snowbuilder
fi
rm -fR "$SNOWBUILDER_HOME" "/opt/android-sdk" "/opt/gradle"
