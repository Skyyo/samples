package com.skyyo.samples.application.persistance

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.skyyo.samples.features.forceTheme.THEME_AUTO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
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

    suspend fun getAppTheme(): String {
        return dataStore.data.first()[PreferencesKeys.APP_THEME] ?: THEME_AUTO
    }

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("appTheme")
    }
}
