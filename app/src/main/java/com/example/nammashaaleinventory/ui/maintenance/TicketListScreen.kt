package com.example.nammashaaleinventory.ui.maintenance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nammashaaleinventory.data.AssetEntity
import com.example.nammashaaleinventory.data.RepairTicketEntity
import com.example.nammashaaleinventory.ui.AppViewModelProvider
import com.example.nammashaaleinventory.utils.DateUtils
import com.example.nammashaaleinventory.viewmodel.MaintenanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    navigateBack: () -> Unit,
    onRaiseTicket: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MaintenanceViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val statusFilters = listOf("All", "Pending", "In Progress", "Delayed", "Completed")
    val priorityFilters = listOf("All", "Low", "Medium", "High", "Critical")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.userSession.role == "Admin") "Maintenance Management" else "My Maintenance Tickets") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onRaiseTicket(0) }) {
                Icon(Icons.Default.Add, contentDescription = "Raise Ticket")
            }
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding).fillMaxSize()) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search ID, Asset, Technician...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Status Filters
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusFilters) { status ->
                    FilterChip(
                        selected = uiState.selectedStatus == status,
                        onClick = { viewModel.onStatusFilterChange(status) },
                        label = { Text(status) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Priority Filters
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(priorityFilters) { priority ->
                    FilterChip(
                        selected = uiState.selectedPriority == priority,
                        onClick = { viewModel.onPriorityFilterChange(priority) },
                        label = { Text(priority) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            if (uiState.tickets.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No tickets found", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tickets) { ticket ->
                        val asset = uiState.assets.find { it.id == ticket.assetId }
                        TicketItem(
                            ticket = ticket,
                            asset = asset,
                            isAdmin = uiState.userSession.role == "Admin",
                            onStatusUpdate = { status -> viewModel.updateTicketStatus(ticket, status) },
                            onDelete = { viewModel.deleteTicket(ticket.ticketId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketItem(
    ticket: RepairTicketEntity,
    asset: AssetEntity?,
    isAdmin: Boolean,
    onStatusUpdate: (String) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val assetName = if (ticket.isUnregisteredAsset) ticket.customAssetName ?: "Unknown Unregistered" else asset?.name ?: "Unknown Asset"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriorityBadge(ticket.priority)
                StatusBadge(ticket.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = assetName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Issue: ${ticket.issueDescription}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (ticket.isUnregisteredAsset) {
                Text(
                    text = "⚠️ Unregistered Asset (${ticket.customAssetCategory ?: "No Category"})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = "Asset ID: ${ticket.assetId} • ${asset?.category ?: "Unknown"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = ticket.assignedTo, style = MaterialTheme.typography.bodySmall)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Due: ${ticket.dueDate?.let { DateUtils.formatDate(it) } ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (isAdmin) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Manage Status")
                    }
                }
            }
        }
    }

    if (expanded) {
        AlertDialog(
            onDismissRequest = { expanded = false },
            title = { Text("Update Ticket Status") },
            text = {
                Column {
                    listOf("Pending", "In Progress", "Completed", "Delayed").forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    onStatusUpdate(status)
                                    expanded = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(status)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { expanded = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val color = when (priority) {
        "Critical" -> Color(0xFFD32F2F)
        "High" -> Color(0xFFF57C00)
        "Medium" -> Color(0xFFFBC02D)
        else -> Color(0xFF388E3C)
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = priority,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "Completed" -> Color(0xFF4CAF50)
        "In Progress" -> Color(0xFF2196F3)
        "Delayed" -> Color(0xFFF44336)
        else -> Color(0xFF757575)
    }
    Surface(
        color = color,
        shape = CircleShape
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
