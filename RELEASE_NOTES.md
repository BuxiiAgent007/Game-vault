# GameVault 2.0.0 — Final PoE Release Notes

## Updates since the prototype

- Completed the GameVault Android application in Kotlin with Jetpack Compose.
- Added Firebase Authentication for email/password registration and login.
- Added Google Single Sign-On for the PoE requirement.
- Added Firebase Firestore as the online NoSQL database for user profiles, settings, collection data and badges.
- Added Firebase Storage blob storage for profile images. A selected profile image is uploaded to Firebase Storage and the download URL is stored in Firestore.
- Added RAWG REST API integration for game discovery, search, details, screenshots, genres, platforms and release dates.
- Added Room SQLite local storage for offline collection changes and cached game data.
- Added WorkManager synchronisation when network connectivity returns, with timestamp-based conflict resolution.
- Added Firebase Cloud Messaging notification handling and device-token storage.
- Added English, isiZulu and Setswana resource files.
- Added collection gamification with badges, ranks and statistics.
- Added settings for theme, language and notification preferences.
- Added input validation and user-friendly error messages for authentication forms.
- Added logging around authentication, storage uploads and notification handling.
- Added automated unit tests for Room converters and offline conflict resolution.
- Added GitHub Actions for automated testing and Android builds.
- Updated Gradle to a stable Gradle 8.9 wrapper and aligned Android/Kotlin/KSP versions to avoid the `debugRuntimeClasspathCopy` configuration error.
- Updated the application version to 2.0.0 for the final PoE build.

## Innovative features demonstrated in this version

1. Gamification through milestone badges and player ranks.
2. Personalised recommendations based on the user's collection history.
3. Offline-first collection editing with automatic synchronisation.
4. Release and badge notification infrastructure using FCM.
5. Multi-language support including two South African languages.
6. Profile image blob storage using Firebase Storage.
