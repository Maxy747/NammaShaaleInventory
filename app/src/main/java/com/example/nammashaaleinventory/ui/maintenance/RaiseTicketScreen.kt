package com.example.nammashaaleinventory.ui.maintenance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nammashaaleinventory.data.AssetEntity
import com.example.nammashaaleinventory.ui.AppViewModelProvider
import com.example.nammashaaleinventory.viewmodel.MaintenanceViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseTicketScreen(
    assetId: Int,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MaintenanceViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    val priorities = listOf("Low", "Medium", "High", "Critical")

    // Asset Selection State
    var selectedAsset by remember { mutableStateOf<AssetEntity?>(null) }
    var isOtherSelected by remember { mutableStateOf(false) }
    var customAssetName by remember { mutableStateOf("") }
    var customAssetCategory by remember { mutableStateOf("") }
    
    var assetDropdownExpanded by remember { mutableStateOf(false) }
    var assetSearchQuery by remember { mutableStateOf("") }

    // Initial load logic
    LaunchedEffect(assetId, uiState.assets) {
        if (assetId > 0 && uiState.assets.isNotEmpty()) {
            selectedAsset = uiState.assets.find { it.id == assetId }
        }
    }

    val filteredAssets = uiState.assets.filter {
        it.name.contains(assetSearchQuery, ignoreCase = true) ||
        it.id.toString().contains(assetSearchQuery) ||
        it.category.contains(assetSearchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raise Repair Ticket") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Select Asset", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            ExposedDropdownMenuBox(
                expanded = assetDropdownExpanded,
                onExpandedChange = { if (assetId == 0) assetDropdownExpanded = !assetDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = when {
                        isOtherSelected -> "Other / Asset Not Listed"
                        selectedAsset != null -> "${selectedAsset?.name} (ID: ${selectedAsset?.id})"
                        else -> "Select an asset..."
                    },
                    onValueChange = {},
                    readOnly = true,
                    enabled = assetId == 0,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assetDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = assetDropdownExpanded,
                    onDismissRequest = { assetDropdownExpanded = false }
                ) {
                    // Search box inside dropdown
                    OutlinedTextField(
                        value = assetSearchQuery,
                        onValueChange = { assetSearchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        placeholder = { Text("Search assets...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    filteredAssets.forEach { asset ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(asset.name, fontWeight = FontWeight.Bold)
                                    Text("ID: ${asset.id} • ${asset.category}", style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = {
                                selectedAsset = asset
                                isOtherSelected = false
                                assetDropdownExpanded = false
                            }
                        )
                    }

                    Divider()
                    
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Other / Asset Not Listed", color = MaterialTheme.colorScheme.primary)
                            }
                        },
                        onClick = {
                            selectedAsset = null
                            isOtherSelected = true
                            assetDropdownExpanded = false
                        }
                    )
                }
            }

            if (isOtherSelected) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Custom Asset Details", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = customAssetName,
                            onValueChange = { customAssetName = it },
                            label = { Text("Asset Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        var categoryExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = !categoryExpanded }
                        ) {
                            OutlinedTextField(
                                value = customAssetCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                uiState.categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.name) },
                                        onClick = {
                                            customAssetCategory = category.name
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Issue Description") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Text("Select Priority", fontWeight = FontWeight.Bold)
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(priorities.size) { index ->
                    val priority = priorities[index]
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = { Text(priority) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (description.isNotBlank() && (selectedAsset != null || (isOtherSelected && customAssetName.isNotBlank()))) {
                        val cal = Calendar.getInstance()
                        cal.add(Calendar.DAY_OF_YEAR, 7)
                        
                        viewModel.raiseTicket(
                            assetId = selectedAsset?.id,
                            description = description,
                            priority = selectedPriority,
                            dueDate = cal.timeInMillis,
                            customName = if (isOtherSelected) customAssetName else null,
                            customCategory = if (isOtherSelected) customAssetCategory else null,
                            isUnregistered = isOtherSelected
                        )
                        navigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(16.dp),
                enabled = description.isNotBlank() && (selectedAsset != null || (isOtherSelected && customAssetName.isNotBlank()))
            ) {
                Text("Submit Ticket", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
