package com.example.nammashaaleinventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammashaaleinventory.data.AssetEntity
import com.example.nammashaaleinventory.data.CategoryEntity
import com.example.nammashaaleinventory.data.InventoryRepository
import com.example.nammashaaleinventory.data.RepairTicketEntity
import com.example.nammashaaleinventory.data.UserPreferencesRepository
import com.example.nammashaaleinventory.data.UserSession
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MaintenanceUiState(
    val tickets: List<RepairTicketEntity> = emptyList(),
    val assets: List<AssetEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val selectedStatus: String = "All",
    val selectedPriority: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val userSession: UserSession = UserSession()
)

class MaintenanceViewModel(
    private val repository: InventoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _userSession = userPreferencesRepository.userSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSession())

    private val _selectedStatus = MutableStateFlow("All")
    private val _selectedPriority = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<MaintenanceUiState> = combine(
        combine(_userSession, _selectedStatus, _selectedPriority, _searchQuery) { session, status, priority, query ->
            DataPart1(session, status, priority, query)
        },
        combine(repository.getAllAssets(), repository.getAllCategories()) { assets, categories ->
            DataPart2(assets, categories)
        },
        _userSession.flatMapLatest { session ->
            if (session.role == "Admin") {
                repository.getAllTickets()
            } else {
                repository.getTicketsByUser(session.userId)
            }
        }
    ) { part1, part2, allTickets ->
        val filteredTickets = allTickets.filter { ticket ->
            val matchesStatus = part1.status == "All" || ticket.status == part1.status
            val matchesPriority = part1.priority == "All" || ticket.priority == part1.priority
            val matchesQuery = part1.query.isBlank() || 
                ticket.issueDescription.contains(part1.query, ignoreCase = true) ||
                ticket.assignedTo.contains(part1.query, ignoreCase = true) ||
                ticket.ticketId.toString().contains(part1.query) ||
                (ticket.assetId?.toString()?.contains(part1.query) == true) ||
                ticket.customAssetName?.contains(part1.query, ignoreCase = true) == true
            
            matchesStatus && matchesPriority && matchesQuery
        }
        
        MaintenanceUiState(
            tickets = filteredTickets,
            assets = part2.assets,
            categories = part2.categories,
            selectedStatus = part1.status,
            selectedPriority = part1.priority,
            searchQuery = part1.query,
            userSession = part1.session,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MaintenanceUiState(isLoading = true)
    )

    private data class DataPart1(val session: UserSession, val status: String, val priority: String, val query: String)
    private data class DataPart2(val assets: List<AssetEntity>, val categories: List<CategoryEntity>)

    fun onStatusFilterChange(newStatus: String) {
        _selectedStatus.value = newStatus
    }

    fun onPriorityFilterChange(newPriority: String) {
        _selectedPriority.value = newPriority
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun raiseTicket(
        assetId: Int?,
        description: String,
        priority: String,
        dueDate: Long?,
        customName: String? = null,
        customCategory: String? = null,
        isUnregistered: Boolean = false
    ) {
        viewModelScope.launch {
            val session = _userSession.value
            val ticket = RepairTicketEntity(
                assetId = assetId,
                issueDescription = description,
                priority = priority,
                assignedTo = "Unassigned",
                status = "Pending",
                raisedByUserId = session.userId,
                dueDate = dueDate,
                customAssetName = customName,
                customAssetCategory = customCategory,
                isUnregisteredAsset = isUnregistered
            )
            repository.insertTicket(ticket)
        }
    }

    fun updateTicketStatus(ticket: RepairTicketEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateTicket(ticket.copy(status = newStatus))
        }
    }
    
    fun deleteTicket(ticketId: Int) {
        viewModelScope.launch {
            repository.deleteTicket(ticketId)
        }
    }
}
