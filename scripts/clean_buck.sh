#!/usr/bin/env bash

#call it from the root folder

find . -name "BUCK" | xargs rm
rm -rf .buckd/
rm -rf .okbuck/
rm -rf buck-out/
rm .buckconfig
rm .buckconfig.local
rm .watchmanconfig
rm buckw