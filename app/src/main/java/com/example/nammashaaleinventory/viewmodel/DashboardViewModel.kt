package com.example.nammashaaleinventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammashaaleinventory.data.AssetEntity
import com.example.nammashaaleinventory.data.InventoryRepository
import com.example.nammashaaleinventory.data.UserSession
import kotlinx.coroutines.flow.*

data class DashboardUiState(
    val totalAssets: Int = 0,
    val brokenAssets: Int = 0,
    val repairAssets: Int = 0,
    val workingAssets: Int = 0,
    val recentAssets: List<AssetEntity> = emptyList(),
    val categoryDistribution: Map<String, Int> = emptyMap(),
    val openTickets: Int = 0,
    val userSession: UserSession = UserSession()
)

class DashboardViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _userSession = MutableStateFlow(UserSession())

    val uiState: StateFlow<DashboardUiState> = combine(
        combine(
            repository.getAllAssets(),
            repository.getTotalAssetsCount(),
            repository.getBrokenAssetsCount()
        ) { allAssets, total, broken -> Triple(allAssets, total, broken) },
        combine(
            repository.getRepairAssetsCount(),
            repository.getPendingTicketsCount(),
            _userSession
        ) { repair, pending, session -> Triple(repair, pending, session) }
    ) { part1, part2 ->
        val (allAssets, total, broken) = part1
        val (repair, pendingTickets, session) = part2

        val working = total - broken - repair
        val categoryDist = allAssets.groupBy { it.category }.mapValues { it.value.size }
        val recent = allAssets.take(5)
        
        DashboardUiState(
            totalAssets = total,
            brokenAssets = broken,
            repairAssets = repair,
            workingAssets = working,
            recentAssets = recent,
            categoryDistribution = categoryDist,
            openTickets = pendingTickets,
            userSession = session
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )

    fun updateUserSession(session: UserSession) {
        _userSession.value = session
    }

    val allAssets: StateFlow<List<AssetEntity>> = repository.getAllAssets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
