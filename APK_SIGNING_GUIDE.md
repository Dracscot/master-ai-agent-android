# APK signing and release guide

This guide explains how to produce a signed release APK for the Android app.

## 1. Generate a keystore

```bash
keytool -genkeypair -v \
  -keystore master-ai-agent-release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias masterai
```

Store the keystore and password securely.

## 2. Configure signing in Gradle

Create `app/keystore.properties`:

```properties
storeFile=../master-ai-agent-release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=masterai
keyPassword=YOUR_KEY_PASSWORD
```

Then add the following to `app/build.gradle.kts`:

```kotlin
import java.util.Properties
import java.io.FileInputStream

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("app/keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

## 3. Generate the signed release APK

```bash
./gradlew assembleRelease
```

APK output:

```text
app/build/outputs/apk/release/app-release.apk
```

## 4. Verify the APK is signed

```bash
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```

## 5. Install on a device

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 6. Google Play release

- Create a Google Play Console account
- Create an app listing
- Upload the AAB or APK
- Complete content rating and privacy policy
- Publish to internal, alpha, beta, or production track

## Security best practices
- Never commit keystore files to source control
- Never hardcode the API key in the APK
- Use a secure backend proxy or app attestation
- Use Play App Signing for production distribution
