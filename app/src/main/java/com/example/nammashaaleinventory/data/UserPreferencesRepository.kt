package com.example.nammashaaleinventory.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

data class UserSession(
    val userId: Int = -1,
    val userName: String = "", // This is the 'fullName' for display
    val loginUsername: String = "", // This is the 'username' used to login
    val role: String = "",
    val department: String = "",
    val isLoggedIn: Boolean = false
)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val USER_ID = intPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val LOGIN_USERNAME = stringPreferencesKey("login_username")
        val ROLE = stringPreferencesKey("role")
        val DEPARTMENT = stringPreferencesKey("department")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val userSession: Flow<UserSession> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("UserPreferences", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserSession(
                userId = preferences[PreferencesKeys.USER_ID] ?: -1,
                userName = preferences[PreferencesKeys.USER_NAME] ?: "",
                loginUsername = preferences[PreferencesKeys.LOGIN_USERNAME] ?: "",
                role = preferences[PreferencesKeys.ROLE] ?: "",
                department = preferences[PreferencesKeys.DEPARTMENT] ?: "",
                isLoggedIn = preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
            )
        }

    suspend fun saveSession(userId: Int, userName: String, loginUsername: String, role: String, department: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.USER_NAME] = userName
            preferences[PreferencesKeys.LOGIN_USERNAME] = loginUsername
            preferences[PreferencesKeys.ROLE] = role
            preferences[PreferencesKeys.DEPARTMENT] = department
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
