package com.example.nammashaaleinventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammashaaleinventory.data.AssetEntity
import com.example.nammashaaleinventory.data.CategoryEntity
import com.example.nammashaaleinventory.data.InventoryRepository
import kotlinx.coroutines.flow.*

data class AssetListUiState(
    val assets: List<AssetEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedCondition: String = "All",
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = false
)

class AssetListViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")
    private val _selectedCondition = MutableStateFlow("All")

    val uiState: StateFlow<AssetListUiState> = combine(
        repository.getAllAssets(),
        _searchQuery,
        _selectedCategory,
        _selectedCondition,
        repository.getAllCategories()
    ) { assets, query, category, condition, categories ->
        val filteredAssets = assets.filter { asset ->
            val matchesQuery = asset.name.contains(query, ignoreCase = true) ||
                    asset.category.contains(query, ignoreCase = true) ||
                    asset.qrCodeHash.contains(query, ignoreCase = true)
            
            val matchesCategory = category == "All" || asset.category == category
            val matchesCondition = condition == "All" || asset.condition == condition
            
            matchesQuery && matchesCategory && matchesCondition
        }
        AssetListUiState(
            assets = filteredAssets,
            searchQuery = query,
            selectedCategory = category,
            selectedCondition = condition,
            categories = categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AssetListUiState(isLoading = true)
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategoryChange(newCategory: String) {
        _selectedCategory.value = newCategory
    }

    fun onConditionChange(newCondition: String) {
        _selectedCondition.value = newCondition
    }
}
