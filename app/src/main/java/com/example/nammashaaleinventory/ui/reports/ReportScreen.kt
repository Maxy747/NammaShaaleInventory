package com.example.nammashaaleinventory.ui.reports

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nammashaaleinventory.ui.AppViewModelProvider
import com.example.nammashaaleinventory.utils.ReportUtils
import com.example.nammashaaleinventory.viewmodel.DashboardViewModel
import com.example.nammashaaleinventory.viewmodel.MaintenanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    dashboardViewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory),
    maintenanceViewModel: MaintenanceViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val allAssets by dashboardViewModel.allAssets.collectAsState()
    val ticketState by maintenanceViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var pendingContentToSave by remember { mutableStateOf<String?>(null) }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            pendingContentToSave?.let { content ->
                ReportUtils.writeContentToUri(context, it, content)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Infrastructure Reports") },
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
            Text("Governance Reports", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            ReportCard(
                title = "Inventory Audit Report",
                description = "Full list of school assets with current status and QR tags.",
                onShare = { 
                    val content = ReportUtils.generateInventoryCsvContent(allAssets)
                    ReportUtils.shareCsvReport(context, content, "Inventory Audit Report")
                },
                onSave = {
                    pendingContentToSave = ReportUtils.generateInventoryCsvContent(allAssets)
                    saveLauncher.launch("inventory_audit_report_${System.currentTimeMillis()}.csv")
                }
            )

            ReportCard(
                title = "Broken Assets Analysis",
                description = "Detailed list of items requiring urgent replacement or disposal.",
                onShare = { 
                    val brokenAssets = allAssets.filter { it.condition == "Broken" }
                    val content = ReportUtils.generateInventoryCsvContent(brokenAssets)
                    ReportUtils.shareCsvReport(context, content, "Broken Assets Analysis")
                },
                onSave = {
                    val brokenAssets = allAssets.filter { it.condition == "Broken" }
                    pendingContentToSave = ReportUtils.generateInventoryCsvContent(brokenAssets)
                    saveLauncher.launch("broken_assets_report_${System.currentTimeMillis()}.csv")
                }
            )

            ReportCard(
                title = "Repair Workflow Summary",
                description = "Summary of open tickets, overdue repairs, and technician assignments.",
                onShare = { 
                    val content = ReportUtils.generateTicketsCsvContent(ticketState.tickets)
                    ReportUtils.shareCsvReport(context, content, "Repair Workflow Summary")
                },
                onSave = {
                    pendingContentToSave = ReportUtils.generateTicketsCsvContent(ticketState.tickets)
                    saveLauncher.launch("repair_tickets_report_${System.currentTimeMillis()}.csv")
                }
            )
        }
    }
}

@Composable
fun ReportCard(
    title: String, 
    description: String, 
    onShare: () -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save", fontSize = 12.sp)
                }

                Button(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share", fontSize = 12.sp)
                }
            }
        }
    }
}
