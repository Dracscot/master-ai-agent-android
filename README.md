# master-ai-agent-android

This repository contains a production-ready Android AI agent scaffold with a Master Agent architecture, child-agent creation flow, secure backend proxy, encrypted local token storage, voice input, and Play-ready signing guidance.

## What is included
- Jetpack Compose UI
- Master agent orchestration
- Child-agent creation flow
- Secure Node.js backend proxy for LLM API calls
- Encrypted token storage with `EncryptedSharedPreferences`
- Conversation memory persistence with DataStore
- Safety rules for tool execution and prompts
- Voice input using Android SpeechRecognizer
- APK release and signing instructions

## Project structure

```text
master-ai-agent-android/
├── app/
│   ├── build.gradle.kts
│   ├── keystore.properties.example
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/masterai/
│       │   │   ├── AgentManager.kt
│       │   │   ├── AgentSafety.kt
│       │   │   ├── AgentViewModel.kt
│       │   │   ├── AgentViewModelFactory.kt
│       │   │   ├── ConversationStore.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── ProxyAiClient.kt
│       │   │   ├── SecureTokenStore.kt
│       │   │   ├── ToolRegistry.kt
│       │   │   └── VoiceInput.kt
│       │   └── res/values/
│       │       ├── strings.xml
│       │       └── themes.xml
├── backend/
│   └── proxy/
│       ├── .env.example
│       ├── package.json
│       └── server.js
├── .gitignore
├── APK_SIGNING_GUIDE.md
├── README.md
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── app/keystore.properties.example
```

## Quick start

### 1. Install Android Studio and JDK 17

### 2. Start the secure proxy

```bash
cd backend/proxy
npm install
cp .env.example .env
# set OPENAI_API_KEY in .env
node server.js
```

### 3. Open the project in Android Studio

### 4. Build debug APK

```bash
./gradlew assembleDebug
```

### 5. Install on device

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Production recommendations
- Never embed production API keys in the APK.
- Use secure backend proxy and encrypted storage.
- Use app signing and Play App Signing for release.
- Add privacy policy and Play data-safety form before upload.
- Use safety checks before allowing tool execution.
- Store conversation memory locally with DataStore.

## Agent creation flow

Examples supported by the app:
- "Create a researcher agent"
- "Create a coding agent"
- "Create a QA agent"

## APK signing
See `APK_SIGNING_GUIDE.md` for complete release steps.
