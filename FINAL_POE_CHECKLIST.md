# GameVault Part 3 Final PoE Checklist

| Assessment requirement | GameVault implementation | Evidence to record |
|---|---|---|
| Register and login | Firebase Authentication email/password | Physical-phone video + Firebase Authentication console |
| Password protection | Firebase Authentication manages credentials; no plaintext password is written to Firestore | Video of registration/login + Firestore user document |
| SSO (PoE) | Google Sign-In + Firebase Authentication | Video of Google login |
| Settings | Theme, language and notification settings | Video of Settings screen |
| REST API | RAWG Video Games Database REST API | Video of search/game details + network/API evidence |
| NoSQL database | Firebase Firestore | Firebase console screenshot/video |
| NoSQL read | Firestore profile, settings, collection and badges are read by the app | Firebase console + app screen |
| Blob storage | Firebase Storage profile image upload | Profile upload video + Storage console |
| Offline mode | Room local collection and game cache | Disable network, change collection, show data still available |
| Synchronisation | WorkManager sync when network reconnects | Reconnect and show remote Firestore record updated |
| Real-time notifications | Firebase Cloud Messaging service and device token storage | FCM notification on physical phone |
| South African languages | isiZulu and Setswana | Video language change in Settings |
| Innovative features | Badges, ranks, recommendations, progress statistics | Profile screen video |
| Invalid inputs | Login/register field checks and friendly error messages | Video with invalid input |
| App icon/assets | Launcher icon and Play Store icon included | Phone launcher + Play Console evidence |
| Automated testing | JUnit tests + GitHub Actions | GitHub Actions run and test report |
| Version control | Git repository + final tag `final-poe` | GitHub commit/tag screenshot |
| README | Comprehensive technical/project documentation | GitHub README |
| Release notes | `RELEASE_NOTES.md` | GitHub file |
| AI disclosure | `AI_USAGE.md` | GitHub file |
| Publication preparation | Signing checklist and Play Console evidence checklist | Signed AAB/APK + Play Console screenshots |

## Physical-phone demonstration order

1. Launch GameVault and show the final app icon.
2. Register a new account.
3. Show the Firebase Authentication user record.
4. Log in.
5. Demonstrate Google SSO.
6. Search RAWG and open a game detail page.
7. Add a game to Vault and edit its status/progress.
8. Turn off mobile data/Wi-Fi and make another collection change.
9. Turn connectivity back on and wait for WorkManager synchronisation.
10. Show the corresponding Firestore collection record.
11. Open Profile and upload a profile image to Firebase Storage.
12. Show the uploaded blob in Firebase Storage.
13. Open Settings and change language/theme/notification preferences.
14. Demonstrate English, isiZulu and Setswana.
15. Trigger/show an FCM notification.
16. Show badges, rank and collection statistics.
17. Enter invalid login/registration data and show the app does not crash.
18. Show GitHub Actions passing.
19. Show the final GitHub tag `final-poe`.
20. Show Play Console publication-preparation evidence.
