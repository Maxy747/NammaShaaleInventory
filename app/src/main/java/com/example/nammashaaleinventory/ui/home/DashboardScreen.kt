package com.example.nammashaaleinventory.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nammashaaleinventory.ui.AppViewModelProvider
import com.example.nammashaaleinventory.ui.components.AppWatermark
import com.example.nammashaaleinventory.ui.components.ConditionBadge
import com.example.nammashaaleinventory.utils.DateUtils
import com.example.nammashaaleinventory.viewmodel.DashboardViewModel
import com.example.nammashaaleinventory.viewmodel.DashboardUiState
import com.example.nammashaaleinventory.data.UserSession
import com.example.nammashaaleinventory.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navigateToAssetList: (String?) -> Unit,
    navigateToScanner: () -> Unit,
    navigateToAssetDetail: (Int) -> Unit,
    onLogout: () -> Unit,
    navigateToTickets: () -> Unit,
    navigateToRaiseTicket: (Int) -> Unit,
    navigateToReports: () -> Unit,
    navigateToManageUsers: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory),
    loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val userSession by loginViewModel.userSession.collectAsState()
    val scrollState = rememberScrollState()

    // Sync session to dashboard viewmodel
    LaunchedEffect(userSession) {
        viewModel.updateUserSession(userSession)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Namma-Shaale Inventory", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Logged in as ${userSession.userName} (${userSession.role})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navigateToAssetList(null) }) {
                        Icon(Icons.Default.Inventory, contentDescription = "All Assets")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = navigateToScanner,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR", modifier = Modifier.size(32.dp))
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            AppWatermark()
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Welcome Section
                Text(
                    text = "Welcome back, ${userSession.userName.split(" ").firstOrNull() ?: "User"}!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Summary Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Total",
                        count = uiState.totalAssets.toString(),
                        icon = Icons.Default.Inventory2,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = { navigateToAssetList(null) },
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Tickets",
                        count = uiState.openTickets.toString(),
                        icon = Icons.Default.ConfirmationNumber,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = navigateToTickets,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Broken",
                        count = uiState.brokenAssets.toString(),
                        icon = Icons.Default.RunningWithErrors,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        onClick = { navigateToAssetList("Broken") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Health Score Section
                SchoolHealthCard(uiState)

                // Asset Condition Pie Chart (Admin Only)
                if (userSession.role == "Admin") {
                    AnalyticsSection(uiState)
                }

                // Analytics / Distribution (Admin Only)
                if (userSession.role == "Admin") {
                    CategoryDistributionSection(uiState.categoryDistribution)
                }

                // Quick Actions
                QuickActionsSection(
                    role = userSession.role,
                    onAddAsset = { navigateToAssetDetail(0) },
                    onViewTickets = navigateToTickets,
                    onRaiseTicket = { navigateToRaiseTicket(0) },
                    onViewReports = navigateToReports,
                    onManageUsers = navigateToManageUsers
                )

                // Recent Assets
                RecentAssetsSection(
                    recentAssets = uiState.recentAssets,
                    onViewAllClick = { navigateToAssetList(null) },
                    onAssetClick = navigateToAssetDetail
                )
                
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    count: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = count, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text(text = title, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "View →", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun SchoolHealthCard(uiState: DashboardUiState) {
    val healthScore = if (uiState.totalAssets > 0) {
        (uiState.workingAssets.toFloat() / uiState.totalAssets * 100).toInt()
    } else 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                healthScore >= 90 -> Color(0xFFE8F5E9)
                healthScore >= 75 -> Color(0xFFFFF3E0)
                else -> Color(0xFFFFEBEE)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = healthScore / 100f,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 8.dp,
                    color = when {
                        healthScore >= 90 -> Color(0xFF4CAF50)
                        healthScore >= 75 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    },
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
                Text(
                    text = "$healthScore%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Column {
                Text(
                    text = "School Health Score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when {
                        healthScore >= 90 -> "Excellent Maintenance"
                        healthScore >= 75 -> "Needs Attention"
                        else -> "Critical Infrastructure Alert"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    role: String,
    onAddAsset: () -> Unit,
    onViewTickets: () -> Unit,
    onRaiseTicket: () -> Unit,
    onViewReports: () -> Unit,
    onManageUsers: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Institutional Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (role == "Admin") {
                ActionButton(
                    text = "Add Asset",
                    icon = Icons.Default.Add,
                    onClick = onAddAsset,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Reports",
                    icon = Icons.Default.BarChart,
                    onClick = onViewReports,
                    modifier = Modifier.weight(1f)
                )
            } else {
                ActionButton(
                    text = "Report Issue",
                    icon = Icons.Default.ReportProblem,
                    onClick = onRaiseTicket,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Audit Logs",
                    icon = Icons.Default.History,
                    onClick = onViewTickets,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (role == "Admin") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton(
                    text = "Maintenance",
                    icon = Icons.Default.ConfirmationNumber,
                    onClick = onViewTickets,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Users",
                    icon = Icons.Default.Group,
                    onClick = onManageUsers,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}

@Composable
fun CategoryDistributionSection(dist: Map<String, Int>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Infrastructure Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (dist.isEmpty()) {
                    Text("No categories yet", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                } else {
                    val maxCount = dist.values.maxOrNull() ?: 1
                    dist.forEach { (category, count) ->
                        CategoryBar(category, count, maxCount)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryBar(name: String, count: Int, maxCount: Int) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = name, style = MaterialTheme.typography.labelMedium)
            Text(text = count.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = count.toFloat() / maxCount,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun RecentAssetsSection(
    recentAssets: List<com.example.nammashaaleinventory.data.AssetEntity>,
    onViewAllClick: () -> Unit,
    onAssetClick: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Audits", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onViewAllClick) {
                Text("View All")
            }
        }
        
        if (recentAssets.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No assets audited yet", color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            recentAssets.forEach { asset ->
                RecentAssetItem(asset = asset, onClick = { onAssetClick(asset.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RecentAssetItem(asset: com.example.nammashaaleinventory.data.AssetEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = asset.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = DateUtils.formatDate(asset.lastAuditDate), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
            ConditionBadge(condition = asset.condition)
        }
    }
}

@Composable
fun AnalyticsSection(uiState: DashboardUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                AssetPieChart(
                    working = uiState.workingAssets.toFloat(),
                    broken = uiState.brokenAssets.toFloat(),
                    repair = uiState.repairAssets.toFloat(),
                    total = uiState.totalAssets.toFloat()
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val percentage = if (uiState.totalAssets > 0) 
                        (uiState.workingAssets.toFloat() / uiState.totalAssets * 100).toInt() 
                    else 100
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Healthy", style = MaterialTheme.typography.labelSmall)
                }
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LegendItem(color = Color(0xFF4CAF50), label = "Working")
                LegendItem(color = MaterialTheme.colorScheme.error, label = "Broken")
                LegendItem(color = Color(0xFFFF9800), label = "Repair")
            }
        }
    }
}

@Composable
fun AssetPieChart(working: Float, broken: Float, repair: Float, total: Float) {
    val sweepWorking = if (total > 0) (working / total) * 360f else 360f
    val sweepBroken = if (total > 0) (broken / total) * 360f else 0f
    val sweepRepair = if (total > 0) (repair / total) * 360f else 0f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 12.dp.toPx()
        
        // Working
        drawArc(
            color = Color(0xFF4CAF50),
            startAngle = -90f,
            sweepAngle = sweepWorking,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Broken
        drawArc(
            color = Color(0xFFE57373),
            startAngle = -90f + sweepWorking,
            sweepAngle = sweepBroken,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Repair
        drawArc(
            color = Color(0xFFFFB74D),
            startAngle = -90f + sweepWorking + sweepBroken,
            sweepAngle = sweepRepair,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}
