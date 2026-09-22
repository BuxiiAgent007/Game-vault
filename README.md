# GameVault (OPSC6312 Portfolio of Evidence)

Game discovery + personal library Android app built on the RAWG Video Games Database API,
with Firebase for user data, offline sync, gamification and multi-language support.

## Stack
- Kotlin + Jetpack Compose, MVVM, Hilt (DI), Material 3 (dark purple/teal theme)
- Retrofit + OkHttp + Gson (RAWG API + custom REST API)
- Room (local cache + offline-first collection)
- WorkManager (background sync, last-write-wins conflict resolution)
- Firebase: Auth (email + Google SSO), Firestore, FCM, Storage
- Coil (image loading), 3 languages (English / isiZulu / Setswana)

## Features (mapped to design doc)
1. Email/password registration + login, forgot password, Google SSO
2. Settings: theme, language, notification toggles, logout
3. REST APIs: RAWG (catalogue) + custom API layer (Firestore-backed)
4. Offline mode: Room-first writes, WorkManager sync when back online
5. Real-time notifications via FCM (service included; server triggers need a Cloud Function)
6. Multi-language: values/values-zu/values-tn
7. Gamification: milestone badges + rank titles + stats dashboard
8. Discovery: trending, search with debounce + genre/year filters, release calendar,
   game detail with hero image/screenshots, personalized recommendations

## Setup (IMPORTANT)
1. Open the folder in Android Studio (Hedgehog+). Let Gradle sync.
2. 
   (project -> add Android app -> package com.gamevault.app). Enable Auth (Email + Google)
   and create a Firestore database in test mode.
3. Create local.properties next to settings.gradle.kts:
   RAWG_API_KEY=<your key from https://rawg.io/api>
4. Run on an emulator or device.

## Known gaps / next steps for Part 2 polishing
- The custom REST API (Cloud Functions) is optional: the SyncWorker falls back to
  Firestore directly. Deploy functions or leave as-is.
- Release-reminder notifications need a scheduled Cloud Function that checks RAWG
  and sends FCM pushes (server-side, outside this app code).
- isiZulu/Setswana translations cover core UI strings; review by a speaker recommended.
- Google Sign-In needs the real SHA-1 + OAuth client from your Firebase project.
