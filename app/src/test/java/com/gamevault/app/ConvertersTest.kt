package com.gamevault.app

import com.gamevault.app.data.local.Converters
import com.gamevault.app.data.model.AppLanguage
import com.gamevault.app.data.model.AuthProvider
import com.gamevault.app.data.model.CollectionStatus
import com.gamevault.app.data.model.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `collection status round-trips through Room`() {
        CollectionStatus.entries.forEach { status ->
            assertEquals(status, converters.stringToStatus(converters.statusToString(status)))
        }
    }

    @Test
    fun `language round-trips through Room`() {
        AppLanguage.entries.forEach { lang ->
            assertEquals(lang, converters.stringToLanguage(converters.languageToString(lang)))
        }
    }

    @Test
    fun `theme round-trips through Room`() {
        ThemeMode.entries.forEach { theme ->
            assertEquals(theme, converters.stringToTheme(converters.themeToString(theme)))
        }
    }

    @Test
    fun `auth provider round-trips through Room`() {
        AuthProvider.entries.forEach { provider ->
            assertEquals(provider, converters.stringToProvider(converters.providerToString(provider)))
        }
    }
}
