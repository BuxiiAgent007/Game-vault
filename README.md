# GameVault — OPSC6312 Final PoE

GameVault is a Kotlin Android game-discovery and personal-library application based on the Game Vault planning and design documents. The Part 2 description defines registration, login, home, calendar, settings, Vault, profile and search screens [1]. The application uses the RAWG Video Games Database API for game catalogue data [2] and Firebase services for authentication, NoSQL storage, blob storage and push notifications [3].


## Technology stack

- Kotlin
- Android Studio
- Jetpack Compose + Material 3
- MVVM architecture
- Hilt dependency injection
- Retrofit + Gson + OkHttp
- RAWG REST API
- Firebase Authentication
- Google SSO
- Firebase Firestore (NoSQL)
- Firebase Storage (blob storage)
- Firebase Cloud Messaging [3] (FCM)
- Room SQLite database
- WorkManager offline synchronisation
- Coil image loading
- GitHub Actions

## Final PoE features

### Authentication
- Email/password registration
- Email/password login
- Password reset
- Google Single Sign-On
- Firebase Authentication manages credentials; passwords are not stored as plaintext in the GameVault Firestore database.

### NoSQL database
Firestore stores online user data such as:

- User profile
- Settings
- Collection records
- Badges
- Device notification tokens

The app writes and reads these documents through Firebase SDK calls.

### Blob storage
Profile images are uploaded to Firebase Storage under:

`users/{userId}/profile/avatar.jpg`

The resulting download URL is stored in the user's Firestore document.

### REST API
GameVault connects to the public RAWG REST API for:

- Game search
- Game details
- Genres
- Platforms
- Screenshots
- Ratings
- Release dates
- Store information

This satisfies the final PoE requirement to connect the application to an internet REST API.

### Offline mode and synchronisation
Room is used as the local source of truth for collection changes. Users can change collection status, progress, ratings and reviews while offline. WorkManager detects network connectivity and synchronises pending records when connectivity returns. Timestamp-based conflict resolution is used when local and remote records differ [4].

### Real-time notifications
Firebase Cloud Messaging client handling is included for Android notifications. The app creates a notification channel, handles received messages and stores device tokens under the authenticated user's Firestore record [9]. A real release/badge alert still requires a trusted message sender, such as a configured Cloud Function or another secured server-side trigger; verify that trigger on a physical phone before claiming the notification requirement is complete.

### Multi-language support
The project contains:

- English
- isiZulu
- Setswana

Language can be changed from Settings.

### Gamification
The final version includes:

- Collection statistics
- Player ranks
- Milestone badges
- Finished-game tracking
- Review milestones
- Personalised recommendations based on collection history

## Main screens

1. Register
2. Login
3. Home / Discover
4. Search and filters
5. Game details
6. Vault / Collection
7. Release Calendar
8. Profile and badges
9. Settings

The Part 2 description specifically identifies the register, login, home, calendar, settings, Vault, profile and search screens.

## Firebase setup

Create/connect a Firebase project and add an Android application with package name:

`com.gamevault.app`

Enable:

- Authentication: Email/Password
- Authentication: Google
- Firestore Database
- Storage
- Cloud Messaging

Place the Firebase `google-services.json` file in:

`app/google-services.json`

Do not commit a real Firebase configuration if your institutional submission rules prohibit it. Review the configuration and Firebase security rules before publication.

## RAWG API setup

The transfer ZIP includes a `local.properties` file populated with the RAWG key recovered from the original GameVault project you supplied. Android Studio reads this file from the project root and the Gradle build injects the value into `BuildConfig`; it is not hard-coded in Kotlin source. If you use a fresh checkout or share the source through GitHub, create your own `local.properties` from `local.properties.example` and add your RAWG key. The real `local.properties` is excluded by `.gitignore` and must never be committed to GitHub.

RAWG integration source files are in `app/src/main/java/com/gamevault/app/data/remote/` (`RawgApi.kt` and `dto/RawgDtos.kt`), with Retrofit configuration in `data/di/AppModule.kt` and API calls in `data/repository/GameRepository.kt` (verify the exact paths in the project tree before relying on them).

## Android Studio / Gradle

The project uses:

- Android Gradle Plugin 8.7.3
- Kotlin 2.0.21
- KSP 2.0.21-1.0.28
- Gradle 8.9
- Java 17

Use the included Gradle wrapper:

```text
./gradlew test
./gradlew build
./gradlew assembleDebug
```

The wrapper is intentionally pinned to Gradle 8.9. Do not switch the project to the Gradle 9 milestone version, because that was the source of the `debugRuntimeClasspathCopy` configuration error encountered during development.

## Automated testing

The repository contains unit tests for:

- Room enum converters
- Offline conflict resolution

GitHub Actions runs the unit tests, lint task and Android debug build on pushes and pull requests to the configured branches [5], following the assessment guidance [6].

Workflow file:

`.github/workflows/build.yml`

## GitHub submission

The final assessment requires the complete Kotlin source code to be submitted through GitHub rather than as a ZIP file. The project should therefore be pushed to the group's GitHub repository and the final commit should be tagged as the final PoE.

Recommended commands:

```bash
git add .
git commit -m "Final PoE - GameVault 2.0.0"
git tag -a final-poe -m "GameVault Final PoE"
git push origin master
git push origin final-poe
```

If the repository uses `main` instead of `master`, replace the branch name accordingly.

## Demonstration video checklist

The final video should be recorded on a physical Android phone and include voice-over. Demonstrate:

1. Registration
2. Login
3. Password protection through Firebase Authentication
4. Google SSO
5. Settings changes
6. RAWG API game search/details
7. Firestore data being written/read
8. Firebase Storage profile image upload
9. Offline collection change
10. Reconnection and synchronisation
11. Push notification
12. English/isiZulu/Setswana language selection
13. Gamification/badges/profile statistics
14. Invalid input handling
15. GitHub Actions test/build result
16. Play Console publication-preparation evidence

The Part 2 document contains the existing GitHub repository and YouTube demonstration links. These should be updated/replaced with the final Part 3 links after the final build is recorded.

## Release notes

See `RELEASE_NOTES.md` for the complete list of changes from the prototype to the final PoE build.

## Play Store preparation

See `docs/PLAY_STORE_CHECKLIST.md` for signing, AAB/APK and Play Console evidence requirements.

## Technical referencing

This README uses IEEE-style numbered citations consistently. Each numbered citation corresponds to one complete entry in `SOURCES.md`; the list is ordered by first citation. This follows the OPSC6312 technical referencing rubric, which requires a consistent style, correct matching between in-text citations and the reference list, and appropriate integration of referenced material [7].

## Rubric alignment and evidence

See `RUBRIC_ALIGNMENT.md` for a requirement-by-requirement mapping, evidence to capture, and items that still require student configuration or real-device verification. The source package is a development hand-off, not proof that cloud services, a physical-phone build, or Play Console publication have already been verified.

## AI usage statement

See `AI_USAGE.md` for the short disclosure of how AI assistance was used during development [8].

## Team / project context

The supplied Part 2 document lists the project roles as User Interface, User Experience, Backend Developers and Tester.
