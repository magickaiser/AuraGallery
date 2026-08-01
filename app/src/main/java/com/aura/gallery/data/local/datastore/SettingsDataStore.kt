package com.aura.gallery.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Manages app preferences using DataStore.
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val GRID_COLUMNS = stringPreferencesKey("grid_columns")
        val SORT_ORDER = stringPreferencesKey("sort_order")
    }

    /**
     * Theme mode: "system", "light", "dark"
     */
    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE] ?: "system"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode
        }
    }

    /**
     * Grid columns: "3", "4", "5"
     */
    val gridColumns: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.GRID_COLUMNS] ?: "3"
    }

    suspend fun setGridColumns(columns: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GRID_COLUMNS] = columns
        }
    }

    /**
     * Sort order: "date_desc", "date_asc", "name"
     */
    val sortOrder: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.SORT_ORDER] ?: "date_desc"
    }

    suspend fun setSortOrder(order: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SORT_ORDER] = order
        }
    }
}
