# OPSC6312 Part 3 Rubric Alignment and Evidence Tracker

## Scope of the supplied rubric

The uploaded PDF is the **IIE Technical Referencing Rubric**. It assesses (1) consistency of the chosen referencing style and placement of in-text citations, and (2) congruency between in-text citations and the reference list, correct reference ordering, and integration of paraphrases/quotes. It is not a full feature-mark allocation rubric for the app. This document therefore maps the provided Part 3 instructions and the available referencing rubric without inventing marks or weighting.

## A. Technical referencing rubric

| Rubric criterion | What this project does | Final check required |
|---|---|---|
| Consistent referencing style | README uses numbered IEEE-style citations; `SOURCES.md` is the matching numbered reference list. | Keep the same style in any report/video script and do not mix Harvard author-date citations into the IEEE list. |
| Citation placement | Technical statements in the README have citations placed near the claims they support. | Add citations to any new externally sourced claims before submission. |
| Citation/reference congruency | Each numbered citation in README maps to a reference entry in `SOURCES.md`. | Check every citation after making edits; remove unused references or cite them in the text. |
| Reference ordering and completeness | References are numbered in order of first citation and include title/organisation, URL and access date for online sources. | Confirm access dates and source details against the pages actually consulted. |
| Integration and paraphrasing | README explains how each technology supports the application instead of relying on long quotations. | Use your own explanation in the written report and cite paraphrased technical information. |

## B. Part 3 functional requirements checklist

| Requirement in the brief | Project component/evidence | Status to verify before submission |
|---|---|---|
| Register and login with protected passwords | Firebase Authentication; `AuthRepository.kt` | Configure Email/Password provider and test on a physical phone. Firebase manages credentials; do not claim that the app itself stores an encrypted password. |
| Google SSO (PoE) | Google Sign-In integration in authentication flow | Configure the correct Firebase Android app, SHA fingerprints and Google provider; test an actual Google login. |
| Settings | Settings screen/view model and saved preferences | Change each setting, restart the app, and confirm the value persists. |
| REST API | RAWG API integration using `RAWG_API_KEY` in `local.properties` | Obtain a valid RAWG key, configure it locally, and test search/details on device. Never commit a private key. |
| NoSQL data write/read | Firebase Firestore repositories | Enable Firestore and rules; show both a write from the app and the corresponding read in Firebase Console. |
| Blob storage | Firebase Storage profile-image repository | Enable Storage, verify upload and download URL, and test rules using a non-owner account. |
| Offline mode | Room database and collection repository | Switch off connectivity, modify collection data, force-close/reopen if supported, and verify local persistence. |
| Sync after reconnect | `SyncWorker.kt`, WorkManager and Firestore fallback | Reconnect and verify the pending change appears remotely. Check retry/error logs. The optional custom API is not deployed by this source package unless you deploy and configure it. |
| Push notifications | FCM service and token registration | FCM receiving/token handling alone does not generate release alerts. Configure a trusted server/Cloud Function or Firebase Console test message and demonstrate receipt on a physical phone. |
| At least two South African languages | `values-zu/strings.xml`, `values-tn/strings.xml` | Test all required visible strings; add translations for any strings still hard-coded in Kotlin. |
| Innovative features from Part 1 | Badges, ranks, recommendations and statistics | Demonstrate each claimed feature with real user data and ensure calculations are not just static placeholders. |
| Invalid input handling | Authentication form validation and error states | Test blank fields, malformed email, weak password, incorrect credentials, network loss and repeated submission. Confirm no crash. |
| App icon and final image assets | Launcher resources and Play Store icon | Check adaptive icon on a physical device and provide screenshots. |
| Comments and logging | Kotlin comments and Android `Log` calls | Ensure logs contain no passwords, tokens, or unnecessary personal information. Capture relevant Logcat evidence. |
| Automated testing | JUnit unit tests and `.github/workflows/build.yml` | Run `./gradlew test lint assembleDebug` locally or inspect a successful GitHub Actions run. Fix failures before tagging final. |
| GitHub version control | Repository README, commits and `final-poe` tag | Push the complete source tree; verify repository visibility/access, latest commit, workflow result and tag. The assignment says not to submit a ZIP as the final deliverable. |
| README and release notes | `README.md`, `RELEASE_NOTES.md` | Update repository/video links and ensure release notes only claim features actually tested. |
| AI disclosure (maximum 500 words) | `AI_USAGE.md` | Personalise the statement to accurately describe the AI use in your own work. |
| Physical-phone demo with voice-over | Record the app and cloud consoles | Record only after features work; show Authentication, Firestore, Storage, API response, offline sync, notification, tests and GitHub evidence. |
| Play Store preparation | `docs/PLAY_STORE_CHECKLIST.md` | Generate a signed release artifact using your own protected signing key; capture screenshots and Play Console evidence. Do not claim publication unless it occurred. |

## C. Build verification limitation

The source package includes Gradle wrapper 8.9 and project build configuration, but this environment could not download the Gradle distribution because network name resolution for `services.gradle.org` was unavailable. Therefore a successful compile/test result has **not** been verified here. Run the commands below on a machine with Android SDK and internet access and resolve any errors before final submission:

```bash
./gradlew test lint assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat test lint assembleDebug
```

## D. Final submission evidence checklist

- [ ] Source code pushed to GitHub (not submitted only as a ZIP).
- [ ] README and release notes match the actual tested implementation.
- [ ] All Firebase providers/services and security rules configured.
- [ ] Valid RAWG API key configured outside version control.
- [ ] Successful Gradle build, unit tests and lint result captured.
- [ ] App tested on a physical Android phone.
- [ ] Demonstration video includes voice-over and the online console data requested by the brief.
- [ ] Final commit tagged `final-poe` and tag pushed to GitHub.
- [ ] Signed APK/AAB and Play Console evidence prepared where applicable.
- [ ] AI usage statement checked for accuracy and kept under 500 words.
