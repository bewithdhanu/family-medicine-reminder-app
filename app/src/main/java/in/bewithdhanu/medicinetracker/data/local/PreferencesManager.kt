package `in`.bewithdhanu.medicinetracker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore for storing user preferences
 * - Language selection (Telugu/English)
 * - Theme preference (auto handled by system)
 */
class PreferencesManager(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "medicine_tracker_preferences"
        )
        
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        
        const val LANGUAGE_TELUGU = "te"
        const val LANGUAGE_ENGLISH = "en"
    }
    
    /**
     * Get current language preference
     * Default: Telugu (te)
     */
    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: LANGUAGE_TELUGU
    }
    
    /**
     * Set language preference
     */
    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
    
    /**
     * Get current language synchronously (for initial load)
     */
    suspend fun getCurrentLanguage(): String {
        var language = LANGUAGE_TELUGU
        context.dataStore.data.map { preferences ->
            language = preferences[LANGUAGE_KEY] ?: LANGUAGE_TELUGU
        }
        return language
    }
}

