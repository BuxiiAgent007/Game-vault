package com.gamevault.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.gamevault.app.data.model.AppLanguage
import com.gamevault.app.data.model.ThemeMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class AppSettings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.EN,
    val notifRelease: Boolean = true,
    val notifBadges: Boolean = true
)

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gamevault_settings", Context.MODE_PRIVATE)

    fun getSettings(): AppSettings = AppSettings(
        theme = prefs.getString(KEY_THEME, "SYSTEM")?.let { ThemeMode.valueOf(it) }
            ?: ThemeMode.SYSTEM,
        language = prefs.getString(KEY_LANGUAGE, "EN")?.let { AppLanguage.valueOf(it) }
            ?: AppLanguage.EN,
        notifRelease = prefs.getBoolean(KEY_NOTIF_RELEASE, true),
        notifBadges = prefs.getBoolean(KEY_NOTIF_BADGES, true)
    )

    /** Writes locally instantly (works offline), then mirrors to Firestore when online. */
    suspend fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putString(KEY_THEME, settings.theme.name)
            .putString(KEY_LANGUAGE, settings.language.name)
            .putBoolean(KEY_NOTIF_RELEASE, settings.notifRelease)
            .putBoolean(KEY_NOTIF_BADGES, settings.notifBadges)
            .apply()
        val uid = auth.currentUser?.uid ?: return
        runCatching {
            firestore.collection("users").document(uid)
                .update(
                    mapOf(
                        "theme" to settings.theme.name,
                        "language" to settings.language.name,
                        "notifRelease" to settings.notifRelease,
                        "notifBadge" to settings.notifBadges
                    )
                )
                .await()
        }
    }

    private companion object {
        const val KEY_THEME = "theme"
        const val KEY_LANGUAGE = "language"
        const val KEY_NOTIF_RELEASE = "notif_release"
        const val KEY_NOTIF_BADGES = "notif_badges"
    }
}
