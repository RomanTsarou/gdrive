
# gdrive
[![Release](https://jitpack.io/v/RomanTsarou/gdrive.svg)](https://jitpack.io/#RomanTsarou/gdrive)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

Easy login and upload files to your Google Drive

## Use

```kotlin
    fun testUpload() {
        //set path to your credentials.json file
        Drive.init("secret/credentials.json")

        //upload a file to Google Drive folder and get a link to uploaded file
        val url = Drive.uploadFile(
            driveFolderId = "132qka4osU6gWcss_aAgseTLgm0A7jABB",
            file = File("app/build/MyApp-1.0.1-release.apk")
        )
        println("Uploaded file: $url")
    }
```

## Setup

* First you need turn on the Drive API and get `credentials.json` file, see [Google Drive API Quickstart](https://developers.google.com/drive/api/v3/quickstart/java)
* Add the dependency in your `build.gradle` file:
  
```gradle  
repositories {  
    maven { url "https://jitpack.io" }  
}  
  
dependencies { 
	implementation 'com.github.RomanTsarou:gdrive:0.0.1'
}  
```