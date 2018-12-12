#!/bin/bash
clear
cat <<EOF
##############################################
# CLUELESS NOTE
#
# ADD THIS:

    lintOptions {
        abortOnError false
    }

# TO   android { }   IN   snowblossom-android/app/build.gradle
# TO IGNORE LINT ERRORS
##############################################
EOF


su - snowbuilder <<EOF
#printenv
echo

if [ -d snowblossom-android ]; then
	cd "snowblossom-android"
	git pull
else
	git clone https://github.com/snowblossomcoin/snowblossom-android.git
	cd "snowblossom-android"
fi

gradle build

EOF
