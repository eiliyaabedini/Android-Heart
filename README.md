# Android Heart

[ ![Download](https://api.bintray.com/packages/lizsoft/heart/heart/images/download.svg) ](https://bintray.com/lizsoft/heart/heart/_latestVersion)

With Android Heart you will have everything that you need for architecture, testing, tools, etc.. in one place.

<br>

**Download**
Main Library

    implementation "de.lizsoft.heart:heart:1.1.2"
<br>
DeepLink

    implementation "de.lizsoft.heart:heart-deeplink:1.1.2"
<br>
Push notification

    implementation "de.lizsoft.heart:heart-push-notification:1.1.2"
<br>
Firebase Authentication

    implementation "de.lizsoft.heart:heart-authentication:1.1.2"


<br>

**Documentation**
Please check our [WIKI](https://github.com/omegasoft7/Android-Heart/wiki)

<br>

**Travel App**
As an example to show how you can work with this library there is a travel check App include in this repository as well. check `App` and `travel-check` modules.
Travel check app will simply check and filter best travel package offers and will notify user in case that it can find a travel package that fits user expectations. For now we are only fetching data from `lastminute.de`. We will add more providers soon.
Any contributions in this project is more than welcome :)
[https://play.google.com/store/apps/details?id=de.lizsoft.app.travelcheck](https://play.google.com/store/apps/details?id=de.lizsoft.app.travelcheck)


<br>

**Compile project via BUCK**
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
<br>
<br>
  
**Test DeepLinks**
For testing Deeplinks you can run following command:  
```  
adb shell am start -W -a android.intent.action.VIEW -d "linkHere" de.lizsoft  
```  
<br>
<br>
  
**Run Unit Tests**
For running all tests run following command  
```  
./gradlew testDebugUnitTest  
  
```  
  
  
**Publish Library**
```./gradlew clean build install bintrayUpload```  
  
<br>
<br>

**Prevent CI Builds**
After each commit and push to Github, A CI build will start to run to make sure that commit did not break anything.
If the changes are minor and you want to skip the CI build, you can add ```[ci skip]``` in the beginning of your commit message.  
   
<br>
<br>

  **TODOs**
  
TODO Health:
- [ ] implement Remote config  
   
<br>

TODO Travel Check:  
- [ ] Flexibility for departure if it has transportation  
- [ ] Flexibility for days of travel  
- [ ] Flexibility for Food. Do you want to have at least breakfast, halfboard or all inclusive?  
  
<br>
<br>

**Setup/Compile**
-) Add google-services.json to `app` directory   
  
<br>
  
**Publish a new version into play store** 
```  
./gradlew --rerun-tasks bundleRelease --stacktrace
./gradlew --rerun-tasks publishReleaseBundle --stacktrace
```