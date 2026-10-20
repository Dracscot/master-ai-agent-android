# APK signing and release guide

This guide explains how to produce a signed release APK and App Bundle for the Android app.

## 1. Create a keystore

```bash
keytool -genkeypair -v \
  -keystore master-ai-agent-release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias masterai
```

## 2. Create app/keystore.properties

Create the file:

```properties
storeFile=../master-ai-agent-release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=masterai
keyPassword=YOUR_KEY_PASSWORD
```

Keep this file local and never commit it to source control.

## 3. Configure signing in Gradle

The project already includes the signing config in `app/build.gradle.kts`.

## 4. Build release APK

```bash
./gradlew assembleRelease
```

APK output:
```text
app/build/outputs/apk/release/app-release.apk
```

## 5. Build release AAB

```bash
./gradlew bundleRelease
```

AAB output:
```text
app/build/outputs/bundle/release/app-release.aab
```

## 6. Install to device

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 7. Play Store release flow

- Create a Google Play Console account
- Create an app listing
- Upload the AAB
- Fill in privacy policy and data safety
- Set up testing tracks (internal / alpha / beta)
- Submit for review
- Release to production

## 8. Security best practices
- Never commit keystore files or secrets
- Keep API keys in backend environment variables only
- Use Play App Signing for production
- Use encrypted storage for tokens and credentials
- Keep app permissions minimal and justified
