package com.example.nammashaaleinventory.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val USER_PREFERENCE_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCE_NAME
)

interface AppContainer {
    val inventoryRepository: InventoryRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val inventoryRepository: InventoryRepository by lazy {
        val database = AppDatabase.getDatabase(context)
        InventoryRepository(
            database.assetDao(),
            database.categoryDao(),
            database.userDao(),
            database.repairTicketDao()
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}
