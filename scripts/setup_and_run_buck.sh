#!/usr/bin/env bash

#call it from the root folder
bash scripts/clean_buck.sh

cp _buckconfig .buckconfig

./gradlew :buckWrapper

bash scripts/run_buck.sh