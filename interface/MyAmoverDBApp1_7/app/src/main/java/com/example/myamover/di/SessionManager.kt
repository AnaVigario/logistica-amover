package com.example.myamover.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

// Interface que define a gestão da sessão do utilizador.
// Utiliza o Jetpack DataStore (substituto do SharedPreferences) para guardar dados simples.
interface SessionManager {
    // Referência para o DataStore que vai armazenar as preferências (neste caso, token do utilizador).
    val dataStore: DataStore<Preferences>


    companion object {
        // Chave usada para identificar o token no DataStore.
        val USER_TOKEN = stringPreferencesKey("user_token")
    }

    // Guarda o token do utilizador no DataStore.
    // É uma função suspend, logo tem de ser chamada dentro de corrotinas.
    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[USER_TOKEN] = token
        }
    }

    // Limpa todas as preferências guardadas no DataStore (incluindo o token).
    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
