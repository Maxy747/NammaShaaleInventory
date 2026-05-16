package com.example.nammashaaleinventory.ui.asset

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nammashaaleinventory.viewmodel.AssetDetailViewModel
import com.example.nammashaaleinventory.viewmodel.AssetUiState
import com.example.nammashaaleinventory.viewmodel.isValid
import com.example.nammashaaleinventory.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailScreen(
    assetId: Int,
    qrCode: String?,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AssetDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(assetId, qrCode) {
        if (assetId > 0) {
            viewModel.loadAsset(assetId)
        } else if (!qrCode.isNullOrEmpty()) {
            viewModel.loadAssetByQr(qrCode)
        }
    }

    LaunchedEffect(viewModel.uiState.errorMessage) {
        viewModel.uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Asset") },
            text = { Text("Are you sure you want to remove this asset? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.deleteAsset()
                            showDeleteDialog = false
                            navigateBack()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddCategoryDialog) {
        var newCategoryName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add New Category") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            if (viewModel.addNewCategory(newCategoryName)) {
                                showAddCategoryDialog = false
                                snackbarHostState.showSnackbar("Category added successfully")
                            }
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (assetId > 0) "Edit Asset" else "Add Asset") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (assetId > 0) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        AssetEntryBody(
            assetUiState = viewModel.uiState,
            onAssetValueChange = viewModel::updateUiState,
            onAddCategoryClick = { showAddCategoryDialog = true },
            onSaveClick = {
                coroutineScope.launch {
                    if (viewModel.saveAsset()) {
                        navigateBack()
                    }
                }
            },
            modifier = modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetEntryBody(
    assetUiState: AssetUiState,
    onAssetValueChange: (AssetUiState) -> Unit,
    onAddCategoryClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val conditions = listOf("Working", "Broken", "Repair")

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        OutlinedTextField(
            value = assetUiState.name,
            onValueChange = { onAssetValueChange(assetUiState.copy(name = it)) },
            label = { Text("Asset Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = assetUiState.name.isBlank() && assetUiState.actionEnabled
        )
        
        // Category Dropdown
        var categoryExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = assetUiState.category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                assetUiState.categories.forEach { category ->
                    DropdownMenuItem(
                        text = { 
                            Row {
                                Text(category.name)
                                if (!category.isDefault) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("(Custom)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        },
                        onClick = {
                            onAssetValueChange(assetUiState.copy(category = category.name))
                            categoryExpanded = false
                        }
                    )
                }
                Divider()
                DropdownMenuItem(
                    text = { 
                        Row {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add New Category")
                        }
                    },
                    onClick = {
                        categoryExpanded = false
                        onAddCategoryClick()
                    }
                )
            }
        }

        // Condition Dropdown
        var conditionExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = conditionExpanded,
            onExpandedChange = { conditionExpanded = !conditionExpanded }
        ) {
            OutlinedTextField(
                value = assetUiState.condition,
                onValueChange = {},
                readOnly = true,
                label = { Text("Condition") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = conditionExpanded,
                onDismissRequest = { conditionExpanded = false }
            ) {
                conditions.forEach { condition ->
                    DropdownMenuItem(
                        text = { Text(condition) },
                        onClick = {
                            onAssetValueChange(assetUiState.copy(condition = condition))
                            conditionExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = assetUiState.qrCodeHash,
            onValueChange = { onAssetValueChange(assetUiState.copy(qrCodeHash = it)) },
            label = { Text("QR Code Hash") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSaveClick,
            enabled = assetUiState.isValid(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Asset")
        }
    }
}
