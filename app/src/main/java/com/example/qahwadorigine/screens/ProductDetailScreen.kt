package com.example.qahwadorigine.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qahwadorigine.viewmodel.CartViewModel
import com.example.qahwadorigine.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val product by productViewModel.getProductById(productId).observeAsState(null)
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())

    var quantity by remember { mutableStateOf(1) }
    var showSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails du produit") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = onCartClick) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Panier")
                        }
                        if (cartItems.isNotEmpty()) {
                            Badge(
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text("${cartItems.size}")
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text("Produit ajouté au panier")
                }
            }
        }
    ) { paddingValues ->
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chargement...")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // ✅ Image du produit (même que dans la liste)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = getProductImageResource(product!!.imageUrl)),
                        contentDescription = product!!.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Product Name
                    Text(
                        text = product!!.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Badge
                    AssistChip(
                        onClick = { },
                        label = { Text(product!!.category) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price
                    Text(
                        text = "${product!!.price} DH",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = "Description",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product!!.description,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quantity Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quantité",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilledTonalIconButton(
                                onClick = { if (quantity > 1) quantity-- }
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Diminuer")
                            }

                            Text(
                                text = quantity.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            FilledTonalIconButton(
                                onClick = { quantity++ }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Augmenter")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Add to Cart Button
                    Button(
                        onClick = {
                            product?.let {
                                cartViewModel.addToCart(it, quantity)
                                showSnackbar = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ajouter au panier - ${product!!.price * quantity} DH",
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            kotlinx.coroutines.delay(3000)
            showSnackbar = false
        }
    }
}