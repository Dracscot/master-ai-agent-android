# master-ai-agent-android

This repository contains a deployable Android AI agent starter with a Master Agent pattern and the ability to create child agents on command.

## Features
- Jetpack Compose Android app
- Master Agent + child agent creation flow
- Tool registry with calculator and system status
- OpenAI-compatible LLM integration
- Secure backend proxy example for API key handling
- APK signing and production release guide

## Repository structure

```text
master-ai-agent-android/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/masterai/
│       │   │   ├── AgentManager.kt
│       │   │   ├── AgentViewModel.kt
│       │   │   ├── LLMClient.kt
│       │   │   ├── MainActivity.kt
│       │   │   └── ToolRegistry.kt
│       │   └── res/values/
│       │       ├── strings.xml
│       │       └── themes.xml
├── backend/
│   └── proxy/
│       ├── .env.example
│       ├── package.json
│       └── server.js
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── APK_SIGNING_GUIDE.md
└── README.md
```

## Quick start

1. Install Android Studio and JDK 17.
2. Open the project in Android Studio.
3. Make sure the Android SDK 35 platform is installed.
4. Update the API key in `app/src/main/java/com/example/masterai/AgentViewModel.kt` or use the secure backend proxy.
5. Build the debug APK:

```bash
./gradlew assembleDebug
```

6. Install on an Android device:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Secure backend proxy

For production, do not embed the LLM API key in the Android app. Use the included proxy:

```bash
cd backend/proxy
npm install
cp .env.example .env
# edit .env with your OpenAI API key
node server.js
```

Then point the Android app to your backend endpoint instead of directly calling OpenAI.

## Agent creation flow

Examples of commands the app can handle:

- "Create a researcher agent"
- "Create a coding agent"
- "Create a QA agent"

This logic is implemented in `AgentViewModel.kt`.

## Production APK build and signing

See `APK_SIGNING_GUIDE.md` for the complete flow.

## Important security note

The app should not ship with a production API token stored in source code. Use a secure backend or a secret manager.
