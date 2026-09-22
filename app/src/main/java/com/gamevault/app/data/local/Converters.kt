package com.gamevault.app.data.local

import androidx.room.TypeConverter
import com.gamevault.app.data.model.AppLanguage
import com.gamevault.app.data.model.AuthProvider
import com.gamevault.app.data.model.CollectionStatus
import com.gamevault.app.data.model.ThemeMode

class Converters {

    @TypeConverter
    fun statusToString(v: CollectionStatus): String = v.name

    @TypeConverter
    fun stringToStatus(v: String): CollectionStatus = enumValueOf(v)

    @TypeConverter
    fun providerToString(v: AuthProvider): String = v.name

    @TypeConverter
    fun stringToProvider(v: String): AuthProvider = enumValueOf(v)

    @TypeConverter
    fun languageToString(v: AppLanguage): String = v.name

    @TypeConverter
    fun stringToLanguage(v: String): AppLanguage = enumValueOf(v)

    @TypeConverter
    fun themeToString(v: ThemeMode): String = v.name

    @TypeConverter
    fun stringToTheme(v: String): ThemeMode = enumValueOf(v)
}
