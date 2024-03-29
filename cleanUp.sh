#!/bin/bash
set -e
echo Cleaning Android Project : $1
cd $1
SIZE=$(du -h --apparent-size --max-depth=0)
echo Initial Size : $SIZE
echo Removing .gradle...
rm -rf .gradle
echo Removing Gradle...
rm -rf gradle
echo Removing .idea...
rm -rf .idea
echo Removing build...
rm -rf build
echo Removing app/build...
rm -rf app/build
echo Removing .gitignore...
find . -type f -name '.gitignore' -delete
echo Removing gradle.properties...
find . -type f -name 'gradle.properties' -delete
echo Removing gradlew...
find . -type f -name 'gradlew' -delete
find . -type f -name 'gradlew.bat' -delete
echo Removing iml files...
find . -type f -name '*.iml' -delete
echo Removing gradle.properties...
find . -type f -name 'local.properties' -delete
SIZE=$(du -h --apparent-size --max-depth=0)
echo Final Size : $SIZE
echo Done
