# Google Play Store Preparation Checklist

## Application
- [x] Application ID: `com.gamevault.app`
- [x] App name: GameVault
- [x] Version name: 2.0.0
- [x] Version code: 2
- [x] Launcher icon included
- [x] Adaptive launcher icon included
- [x] Release notes prepared
- [x] Internet permission and secure HTTPS-only traffic configured

## Signing
1. Generate a private release keystore locally. Never commit the keystore or passwords to GitHub.
2. Create `keystore.properties` locally from `keystore.properties.example`.
3. Configure the release signing values in `app/build.gradle.kts`.
4. Build a signed release APK/AAB from Android Studio.
5. Keep a secure backup of the keystore.

## Play Console evidence
Capture screenshots of:
- App dashboard
- App bundle upload
- Internal testing/release screen
- Store listing
- Content declarations
- App access/testing information

The assessment asks for evidence of publication preparation, so these screenshots should be included in the final evidence folder/portfolio.
