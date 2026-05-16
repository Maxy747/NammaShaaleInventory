package com.example.nammashaaleinventory

import android.app.Application
import com.example.nammashaaleinventory.data.AppContainer
import com.example.nammashaaleinventory.data.AppDataContainer

class InventoryApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Force delete database for demo to trigger seeding
        // deleteDatabase("inventory_database")
        container = AppDataContainer(this)
    }
}
