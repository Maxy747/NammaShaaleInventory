package com.example.nammashaaleinventory.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammashaaleinventory.data.AssetEntity
import com.example.nammashaaleinventory.data.CategoryEntity
import com.example.nammashaaleinventory.data.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AssetUiState(
    val id: Int = 0,
    val name: String = "",
    val category: String = "Tablet",
    val condition: String = "Working",
    val qrCodeHash: String = "",
    val actionEnabled: Boolean = false,
    val errorMessage: String? = null,
    val categories: List<CategoryEntity> = emptyList()
)

class AssetDetailViewModel(private val repository: InventoryRepository) : ViewModel() {

    var uiState by mutableStateOf(AssetUiState())
        private set

    init {
        repository.getAllCategories()
            .onEach { categories ->
                uiState = uiState.copy(categories = categories)
            }
            .launchIn(viewModelScope)
    }

    fun updateUiState(newAssetUiState: AssetUiState) {
        uiState = newAssetUiState.copy(
            actionEnabled = newAssetUiState.isValid(),
            errorMessage = null
        )
    }

    suspend fun saveAsset(): Boolean {
        if (uiState.isValid()) {
            if (!repository.isQrCodeUnique(uiState.qrCodeHash, uiState.id)) {
                uiState = uiState.copy(errorMessage = "QR Code already exists!")
                return false
            }
            
            val asset = uiState.toAssetEntity()
            if (asset.id == 0) {
                repository.insertAsset(asset)
            } else {
                repository.updateAsset(asset)
            }
            return true
        }
        return false
    }

    suspend fun deleteAsset() {
        repository.deleteAsset(uiState.toAssetEntity())
    }

    fun loadAsset(assetId: Int) {
        viewModelScope.launch {
            val asset = repository.getAssetByIdStream(assetId).firstOrNull()
            if (asset != null) {
                uiState = asset.toAssetUiState(actionEnabled = true, categories = uiState.categories)
            }
        }
    }

    fun loadAssetByQr(qrCode: String) {
        viewModelScope.launch {
            val asset = repository.getAssetByQrCode(qrCode)
            if (asset != null) {
                uiState = asset.toAssetUiState(actionEnabled = true, categories = uiState.categories)
            } else {
                uiState = uiState.copy(qrCodeHash = qrCode, actionEnabled = false)
            }
        }
    }

    suspend fun addNewCategory(categoryName: String): Boolean {
        val trimmedName = categoryName.trim()
        if (trimmedName.isBlank()) return false
        
        val existing = repository.getCategoryByName(trimmedName)
        if (existing != null) {
            uiState = uiState.copy(errorMessage = "Category already exists!")
            return false
        }
        
        repository.insertCategory(CategoryEntity(name = trimmedName))
        uiState = uiState.copy(category = trimmedName) // Auto-select new category
        return true
    }
}

fun AssetUiState.isValid(): Boolean {
    return name.isNotBlank() && category.isNotBlank() && condition.isNotBlank()
}

fun AssetUiState.toAssetEntity(): AssetEntity = AssetEntity(
    id = id,
    name = name,
    category = category,
    condition = condition,
    qrCodeHash = qrCodeHash,
    lastAuditDate = System.currentTimeMillis()
)

fun AssetEntity.toAssetUiState(actionEnabled: Boolean = false, categories: List<CategoryEntity> = emptyList()): AssetUiState = AssetUiState(
    id = id,
    name = name,
    category = category,
    condition = condition,
    qrCodeHash = qrCodeHash,
    actionEnabled = actionEnabled,
    categories = categories
)
