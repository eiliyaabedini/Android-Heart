#!/usr/bin/env bash
set -e

#https://github.com/gdrive-org/gdrive
#brew install gdrive
./gradlew assembleDebug

#Delete all the files n directory
gdrive list -q " '19FA2NsF1BpWB5ODzx4KL5RDWE_GG9LL-' in parents" --no-header --max 0 | cut -d" " -f1 - | xargs -L 1 gdrive delete -r

#upload the new apk
gdrive upload --parent 19FA2NsF1BpWB5ODzx4KL5RDWE_GG9LL- app/build/outputs/apk/debug/app-debug.apk