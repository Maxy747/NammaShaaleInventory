package com.example.nammashaaleinventory.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nammashaaleinventory.InventoryApplication
import com.example.nammashaaleinventory.viewmodel.AssetDetailViewModel
import com.example.nammashaaleinventory.viewmodel.AssetListViewModel
import com.example.nammashaaleinventory.viewmodel.DashboardViewModel
import com.example.nammashaaleinventory.viewmodel.LoginViewModel
import com.example.nammashaaleinventory.viewmodel.MaintenanceViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DashboardViewModel(
                inventoryApplication().container.inventoryRepository
            )
        }
        initializer {
            AssetDetailViewModel(
                inventoryApplication().container.inventoryRepository
            )
        }
        initializer {
            AssetListViewModel(
                inventoryApplication().container.inventoryRepository
            )
        }
        initializer {
            LoginViewModel(
                inventoryApplication().container.inventoryRepository,
                inventoryApplication().container.userPreferencesRepository
            )
        }
        initializer {
            MaintenanceViewModel(
                inventoryApplication().container.inventoryRepository,
                inventoryApplication().container.userPreferencesRepository
            )
        }
    }
}

fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)
