# GameVault — OPSC6312 Final Portfolio of Evidence

Game discovery and personal library Android app built on the RAWG Video Games Database API, with Firebase for user data, offline sync, gamification, and multi-language support.

**Repository Link:** [https://github.com/BuxiiAgent007/Game-vault](https://github.com/BuxiiAgent007/Game-vault)

## 🛠️ Technology Stack
- **Language & UI:** Kotlin + Jetpack Compose, Material 3 (Dark purple/teal theme)
- **Architecture:** MVVM, Hilt (Dependency Injection)
- **Networking:** Retrofit + OkHttp + Gson (RAWG API + custom REST API)
- **Local Storage:** Room (local cache + offline-first collection)
- **Background Tasks:** WorkManager (background sync, last-write-wins conflict resolution)
- **Backend & Cloud:** Firebase (Auth: Email + Google SSO, Firestore, FCM, Storage)
- **Utilities:** Coil (image loading), 3 languages (English / isiZulu / Setswana)

## ✨ Features (Mapped to Design Doc)
- **Authentication:** Email/password registration + login, forgot password, Google SSO
- **Settings:** Theme, language, notification toggles, logout
- **REST APIs:** RAWG (catalogue) + custom API layer (Firestore-backed)
- **Offline Mode:** Room-first writes, WorkManager sync when back online
- **Notifications:** Real-time notifications via FCM (service included; server triggers need a Cloud Function)
- **Multi-language:** values/values-zu/values-tn
- **Gamification:** Milestone badges + rank titles + stats dashboard
- **Discovery:** Trending, search with debounce + genre/year filters, release calendar, game detail with hero image/screenshots, personalized recommendations

## 🚀 Setup Instructions (IMPORTANT)
1. Open the folder in Android Studio (Hedgehog+). Let Gradle sync.
2. (project -> add Android app -> package `com.gamevault.app`). Enable Auth (Email + Google) and create a Firestore database in test mode.
3. Create `local.properties` next to `settings.gradle.kts`: 
   `RAWG_API_KEY=<your key from https://rawg.io/api>`
4. Run on an emulator or physical device.

## ⚠️ Known Gaps / Next Steps
- **Custom REST API:** The custom REST API (Cloud Functions) is optional. The `SyncWorker` falls back to Firestore directly. Deploy functions or leave as-is.
- **Release-reminder notifications:** Need a scheduled Cloud Function that checks RAWG and sends FCM pushes (server-side, outside this app code).
- **Translations:** isiZulu/Setswana translations cover core UI strings; review by a speaker recommended.
- **Google Sign-In:** Needs the real SHA-1 + OAuth client from your Firebase project.

---

## 🎥 Demonstration Video
[INSERT YOUR YOUTUBE LINK HERE]

*(Note: The video demonstrates the app running on a physical device, showing all features including offline sync, multi-language support, and push notifications).*

## ✅ Automated Testing & CI/CD Evidence
This project uses **GitHub Actions** for Continuous Integration. On every push to the `master` branch, the workflow automatically:
1. Sets up Java 17
2. Runs Gradle Unit Tests (`ConflictResolverTest` and `ConvertersTest`)
3. Builds the Debug APK

**Test Results:** 7 Tests passed, 0 failures (100% Success Rate).
**Build Status:** Passing (Green Checkmark ✅)

*(You can view the live test runs in the "Actions" tab of this repository).*
