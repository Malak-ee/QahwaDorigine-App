package com.example.qahwadorigine.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qahwadorigine.data.model.Product
import com.example.qahwadorigine.R
import com.example.qahwadorigine.viewmodel.CartViewModel
import com.example.qahwadorigine.viewmodel.ProductViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CozyCafeTopBar(
    cartCount: Int,
    onCartClick: () -> Unit,
    onLogout: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.logoo),
                contentDescription = "Logo",
                modifier = Modifier.height(60.dp)
            )
        },
        actions = {
            // 🛒 Icône Panier avec badge
            Box {
                IconButton(onClick = onCartClick) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Panier",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if (cartCount > 0) {
                    Badge(
                        modifier = Modifier.align(Alignment.TopEnd),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Text("$cartCount")
                    }
                }
            }

            // ⋮ Menu
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Se déconnecter") },
                    onClick = {
                        showMenu = false
                        onLogout()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f)
        )
    )
}

// Fonction utilitaire pour obtenir l'image depuis le nom en BDD
@Composable
fun getProductImageResource(imageUrl: String): Int {
    val context = LocalContext.current

    val resourceId = context.resources.getIdentifier(
        imageUrl,
        "drawable",
        context.packageName
    )

    return if (resourceId != 0) resourceId else R.drawable.cafe_milk
}

// PRODUCT LIST SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onLogout: () -> Unit
) {
    val products by productViewModel.allProducts.observeAsState(emptyList())
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("Tout", "Café", "Thé", "Pâtisserie")

    // Filtrage par catégorie ET recherche
    val displayedProducts = remember(products, selectedCategory, searchQuery) {
        products.filter { product ->
            val matchesCategory = if (selectedCategory == null || selectedCategory == "Tout")
                true
            else
                product.category == selectedCategory

            val matchesSearch = if (searchQuery.isBlank())
                true
            else
                product.name.contains(searchQuery, ignoreCase = true) ||
                        product.description.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            CozyCafeTopBar(
                cartCount = cartItems.size,
                onCartClick = onCartClick,
                onLogout = onLogout
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 🔍 Barre de Recherche Moderne
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Rechercher un produit...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Rechercher",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Effacer",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // Filtre Catégories - Version Moderne
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    val isSelected = if (category == "Tout")
                        selectedCategory == null else selectedCategory == category

                    val categoryIcon = when (category) {
                        "Tout" -> Icons.Default.Home
                        "Café" -> Icons.Default.FavoriteBorder
                        "Thé" -> Icons.Default.LocalCafe
                        "Pâtisserie" -> Icons.Default.Star
                        else -> Icons.Default.ShoppingCart
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = if (category == "Tout") null else category
                        },
                        label = {
                            Text(
                                text = category,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderWidth = if (isSelected) 2.dp else 1.dp,
                            selectedBorderWidth = 2.dp
                        ),
                        elevation = FilterChipDefaults.filterChipElevation(
                            elevation = if (isSelected) 4.dp else 1.dp
                        )
                    )
                }
            }

            // Résultat de recherche
            if (searchQuery.isNotEmpty() || selectedCategory != null) {
                Text(
                    text = "${displayedProducts.size} produit(s) trouvé(s)",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Liste Produits
            if (displayedProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Aucun produit trouvé",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        if (searchQuery.isNotEmpty()) {
                            Text(
                                "Essayez une autre recherche",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayedProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { cartViewModel.addToCart(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    var showSnackbar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Image unique depuis la base de données
            Image(
                painter = painterResource(id = getProductImageResource(product.imageUrl)),
                contentDescription = product.name,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 12.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(product.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Text(
                    product.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${product.price} DH",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    FilledTonalButton(
                        onClick = {
                            onAddToCart()
                            showSnackbar = true
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Ajouter")
                    }
                }
            }
        }
    }

    if (showSnackbar) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            showSnackbar = false
        }
    }
}