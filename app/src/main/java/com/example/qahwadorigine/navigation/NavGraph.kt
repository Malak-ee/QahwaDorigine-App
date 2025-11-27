package com.example.qahwadorigine.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.qahwadorigine.ui.screens.*
import com.example.qahwadorigine.viewmodel.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ProductList : Screen("product_list")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object OrderConfirmation : Screen("order_confirmation/{totalAmount}/{itemCount}") {
        fun createRoute(totalAmount: Double, itemCount: Int) = "order_confirmation/$totalAmount/$itemCount"
    }
    object Profile : Screen("profile")
}

// Bottom Navigation Items
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(Screen.ProductList.route, Icons.Default.Home, "Accueil")
    object Cart : BottomNavItem(Screen.Cart.route, Icons.Default.ShoppingCart, "Panier")
    object Profile : BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profil")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Screen.Signup.route)
                }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                viewModel = authViewModel,
                onSignupSuccess = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ProductList.route) {
            ProductListScreen(
                productViewModel = productViewModel,
                cartViewModel = cartViewModel,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(
                productId = productId,
                productViewModel = productViewModel,
                cartViewModel = cartViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                viewModel = cartViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onCheckout = { totalAmount, itemCount ->
                    navController.navigate(
                        Screen.OrderConfirmation.createRoute(totalAmount, itemCount)
                    ) {
                        popUpTo(Screen.ProductList.route)
                    }
                }
            )
        }

        composable(
            route = Screen.OrderConfirmation.route,
            arguments = listOf(
                navArgument("totalAmount") { type = NavType.FloatType },
                navArgument("itemCount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val totalAmount = backStackEntry.arguments?.getFloat("totalAmount")?.toDouble() ?: 0.0
            val itemCount = backStackEntry.arguments?.getInt("itemCount") ?: 0
            OrderConfirmationScreen(
                totalAmount = totalAmount,
                itemCount = itemCount,
                onBackToHome = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.ProductList.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreenWithBottomNav(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())

    // Routes où on affiche la Bottom Nav
    val bottomNavRoutes = listOf(
        Screen.ProductList.route,
        Screen.Cart.route,
        Screen.Profile.route
    )

    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Cart,
                        BottomNavItem.Profile
                    )

                    items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                Box {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                    // Badge pour le panier
                                    if (item == BottomNavItem.Cart && cartItems.isNotEmpty()) {
                                        Badge(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = 8.dp, y = (-8).dp),
                                            containerColor = MaterialTheme.colorScheme.error
                                        ) {
                                            Text("${cartItems.size}")
                                        }
                                    }
                                }
                            },
                            label = {
                                Text(
                                    item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavGraph(
                navController = navController,
                authViewModel = authViewModel,
                productViewModel = productViewModel,
                cartViewModel = cartViewModel
            )
        }
    }
}