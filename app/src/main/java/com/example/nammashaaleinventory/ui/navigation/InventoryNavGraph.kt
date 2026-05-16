package com.example.nammashaaleinventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nammashaaleinventory.ui.AppViewModelProvider
import com.example.nammashaaleinventory.ui.admin.ManageUsersScreen
import com.example.nammashaaleinventory.ui.asset.AssetDetailScreen
import com.example.nammashaaleinventory.ui.asset.AssetListScreen
import com.example.nammashaaleinventory.ui.auth.LoginScreen
import com.example.nammashaaleinventory.ui.auth.RegisterScreen
import com.example.nammashaaleinventory.ui.home.DashboardScreen
import com.example.nammashaaleinventory.ui.maintenance.RaiseTicketScreen
import com.example.nammashaaleinventory.ui.maintenance.TicketListScreen
import com.example.nammashaaleinventory.ui.reports.ReportScreen
import com.example.nammashaaleinventory.ui.scanner.ScannerScreen
import com.example.nammashaaleinventory.viewmodel.LoginViewModel

enum class InventoryDestinations(val route: String) {
    Login("login"),
    Register("register"),
    Dashboard("dashboard"),
    AssetList("asset_list?condition={condition}"),
    AssetDetail("asset_detail/{assetId}?qrCode={qrCode}"),
    Scanner("scanner"),
    TicketList("ticket_list"),
    RaiseTicket("raise_ticket/{assetId}"),
    Reports("reports"),
    ManageUsers("manage_users")
}

@Composable
fun InventoryNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val userSession by loginViewModel.userSession.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (userSession.isLoggedIn) InventoryDestinations.Dashboard.route else InventoryDestinations.Login.route,
        modifier = modifier
    ) {
        composable(route = InventoryDestinations.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(InventoryDestinations.Dashboard.route) {
                        popUpTo(InventoryDestinations.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(InventoryDestinations.Register.route) }
            )
        }

        composable(route = InventoryDestinations.Register.route) {
            RegisterScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = InventoryDestinations.Dashboard.route) {
            DashboardScreen(
                navigateToAssetList = { condition ->
                    val route = if (condition != null) "asset_list?condition=$condition" else "asset_list"
                    navController.navigate(route)
                },
                navigateToScanner = { navController.navigate(InventoryDestinations.Scanner.route) },
                navigateToAssetDetail = { assetId -> 
                    navController.navigate(InventoryDestinations.AssetDetail.route.replace("{assetId}", assetId.toString()).replace("?qrCode={qrCode}", "")) 
                },
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(InventoryDestinations.Login.route) {
                        popUpTo(InventoryDestinations.Dashboard.route) { inclusive = true }
                    }
                },
                navigateToTickets = { navController.navigate(InventoryDestinations.TicketList.route) },
                navigateToRaiseTicket = { assetId -> navController.navigate("raise_ticket/$assetId") },
                navigateToReports = { navController.navigate(InventoryDestinations.Reports.route) },
                navigateToManageUsers = { navController.navigate(InventoryDestinations.ManageUsers.route) }
            )
        }
        
        composable(route = InventoryDestinations.ManageUsers.route) {
            ManageUsersScreen(navigateBack = { navController.popBackStack() })
        }
        
        composable(route = InventoryDestinations.Reports.route) {
            ReportScreen(navigateBack = { navController.popBackStack() })
        }
        
        composable(route = InventoryDestinations.TicketList.route) {
            TicketListScreen(
                navigateBack = { navController.popBackStack() },
                onRaiseTicket = { assetId -> navController.navigate("raise_ticket/$assetId") }
            )
        }

        composable(
            route = InventoryDestinations.RaiseTicket.route,
            arguments = listOf(navArgument("assetId") { type = NavType.IntType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getInt("assetId") ?: 0
            RaiseTicketScreen(
                assetId = assetId,
                navigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = InventoryDestinations.AssetList.route,
            arguments = listOf(
                navArgument("condition") { 
                    type = NavType.StringType 
                    nullable = true 
                    defaultValue = null 
                }
            )
        ) { backStackEntry ->
            val condition = backStackEntry.arguments?.getString("condition")
            AssetListScreen(
                initialCondition = condition,
                navigateBack = { navController.popBackStack() },
                navigateToAssetDetail = { assetId ->
                    navController.navigate(InventoryDestinations.AssetDetail.route.replace("{assetId}", assetId.toString()).replace("?qrCode={qrCode}", ""))
                }
            )
        }
        
        composable(
            route = InventoryDestinations.AssetDetail.route,
            arguments = listOf(
                navArgument("assetId") { type = NavType.IntType },
                navArgument("qrCode") { 
                    type = NavType.StringType 
                    nullable = true 
                    defaultValue = null 
                }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getInt("assetId") ?: 0
            val qrCode = backStackEntry.arguments?.getString("qrCode")
            AssetDetailScreen(
                assetId = assetId,
                qrCode = qrCode,
                navigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = InventoryDestinations.Scanner.route) {
            ScannerScreen(
                navigateBack = { navController.popBackStack() },
                onQrCodeScanned = { qrCode ->
                    // Navigate to asset detail. We pass 0 for ID and append the qrCode query param
                    navController.navigate("asset_detail/0?qrCode=$qrCode") {
                        popUpTo(InventoryDestinations.Scanner.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
