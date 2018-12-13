#!/bin/bash

if [ $EUID -ne 0 ]; then
   echo "This script must be run as root"
   exit 1
fi

source config.sh

echo
echo "# INSTALL APT REQUIREMENTS"
apt-get install -y unzip git openjdk-8-jdk

echo
echo "# INSTALL GRADLE"
mkdir -p "/opt/gradle"
cd "/opt/gradle"
wget --no-clobber "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
unzip -n "gradle-$GRADLE_VERSION-bin.zip"
chmod -R +x "gradle-$GRADLE_VERSION/bin"

# ANDROID SDK *SHOULD* be installed here, but gradle wants to write changes to the sdk directory, instead of local user directory, which is stupid


echo
echo # SETUP BUILD USER - SNOWBUILDER - $SNOWBUILDER_HOME
if ! id -u snowbuilder &>/dev/null ; then
	useradd --home-dir "$SNOWBUILDER_HOME" --create-home --system snowbuilder

	cat <<EOF >> "$SNOWBUILDER_HOME/.profile"

export GRADLE_HOME=/opt/gradle/gradle-$GRADLE_VERSION
export ANDROID_HOME=\$HOME/android-sdk/$ANDROID_SDK_VERSION
export ANDROID_SDK_ROOT=\$HOME/android-sdk/$ANDROID_SDK_VERSION
export PATH=\$PATH:\$GRADLE_HOME/bin:\$ANDROID_SDK_ROOT/tools:\$ANDROID_SDK_ROOT/tools/bin:\$ANDROID_HOME/tools
EOF

	su - snowbuilder <<EOF
echo
echo "# INSTALL ANDROID SDK"
mkdir -p "\$HOME/android-sdk/"
cd "\$HOME/android-sdk/"
wget --no-clobber "https://dl.google.com/android/repository/$ANDROID_SDK_VERSION.zip"
mkdir -p "$ANDROID_SDK_VERSION"
unzip -n $ANDROID_SDK_VERSION.zip -d "$ANDROID_SDK_VERSION/"
chmod -R +x "$ANDROID_SDK_VERSION/tools/bin"

echo
echo "# YOLO!"
yes | sdkmanager --licenses

EOF
fi
