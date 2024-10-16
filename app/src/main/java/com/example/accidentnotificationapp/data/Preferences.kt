package com.example.accidentnotificationapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
	
	companion object {
		private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
		private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
		private val COOKIE_KEY = stringPreferencesKey("cookie")
	}
	
	suspend fun setLoginState(isLoggedIn: Boolean, email: String, cookie: String) {
		context.dataStore.edit { preferences ->
			preferences[IS_LOGGED_IN_KEY] = isLoggedIn
			preferences[USER_EMAIL_KEY] = email
			preferences[COOKIE_KEY] = cookie
		}
	}
	
	val isLoggedIn: Flow<Boolean> = context.dataStore.data
		.map { preferences ->
			preferences[IS_LOGGED_IN_KEY] ?: false
		}
	
	val userEmail: Flow<String?> = context.dataStore.data
		.map { preferences ->
			preferences[USER_EMAIL_KEY]
		}
	
	val cookie: Flow<String?> = context.dataStore.data
		.map { preferences ->
			preferences[COOKIE_KEY]
		}
	
	suspend fun logout() {
		context.dataStore.edit { preferences ->
			preferences.clear()
		}
	}
}
