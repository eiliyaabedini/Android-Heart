#!/usr/bin/env bash
set -e
./gradlew clean build install
./gradlew bintrayUpload