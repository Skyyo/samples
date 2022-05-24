package com.skyyo.samples.application.persistance

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.skyyo.samples.features.forceTheme.THEME_AUTO
import com.skyyo.samples.utils.DEFAULT_LANGUAGE_CODE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


val Context.getDataStore: DataStore<Preferences> by preferencesDataStore(name = "templateDataStore")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.getDataStore

    suspend fun setAppTheme(appTheme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = appTheme
        }
    }

    suspend fun getAppTheme(): String =
        dataStore.data.first()[PreferencesKeys.APP_THEME] ?: THEME_AUTO

    suspend fun setLanguageCode(languageCode: String) = dataStore.edit { preferences ->
        preferences[PreferencesKeys.LANGUAGE_CODE] = languageCode
    }

    fun getLanguageCode() = dataStore.data.map {
        it[PreferencesKeys.LANGUAGE_CODE] ?: DEFAULT_LANGUAGE_CODE
    }

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("appTheme")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }
}
