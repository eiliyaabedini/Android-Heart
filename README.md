This project support building using Buck.

Install Buck:

```sh
brew tap facebook/fb
brew install buck
```

Install watchman:
```sh
brew install watchman
```
Run the buck wrapper task
```sh
./gradlew :buckWrapper
```
This creates a buckw wrapper similar to the gradle wrapper
To install an apk on a device:
```sh
./buckw install --run appDebug
```
To build without installing:
```sh
./buckw build appDebug
```
If you encounter the following issue when trying to install the apk on the device - remove previously installed app manually:
```
java.lang.IllegalArgumentException: Failed to find manifest digest in META-INF/CERT.SF
```



For testing Deeplinks you can run following command:
```
adb shell am start -W -a android.intent.action.VIEW -d "linkHere" de.lizsoft
```


For running all tests run following command
```
./gradlew testDebugUnitTest

```


Publish Library:
```./gradlew clean build install bintrayUpload```


Prevent CI Builds:
After each commit and push to Gitlab, A CI build will start to run to make sure that commit did not break anything.
If the changes are minor and you want to skip the CI build, you can add ```[ci skip]``` in the beginning of your commit message.
 

TODO Health:
- [ ] Remove map utils and its ui from heart lib
- [ ] implement Remote config
 

TODO Travel Check:
- [ ] Flexibility for departure if it has transportation
- [ ] Flexibility for days of travel
- [ ] Flexibility for Food. Do you want to have at least breakfast, halfboard or all inclusive?

Setup:
- Add google-services.json to `app` directory 


Publish a new version into play store:
```
./gradlew --rerun-tasks assembleRelease --stacktrace
./gradlew --rerun-tasks publishReleaseApk --stacktrace
```



