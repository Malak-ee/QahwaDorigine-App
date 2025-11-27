package com.example.qahwadorigine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.qahwadorigine.data.database.AppDatabase
import com.example.qahwadorigine.ui.navigation.MainScreenWithBottomNav
import com.example.qahwadorigine.ui.theme.QahwaDOrigineTheme
import com.example.qahwadorigine.viewmodel.*

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var cartViewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val productDao = database.productDao()
        val cartDao = database.cartDao()
        val factory = ViewModelFactory(productDao, cartDao)

        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        productViewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]
        cartViewModel = ViewModelProvider(this, factory)[CartViewModel::class.java]

        setContent {
            QahwaDOrigineTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    MainScreenWithBottomNav(
                        navController = navController,
                        authViewModel = authViewModel,
                        productViewModel = productViewModel,
                        cartViewModel = cartViewModel
                    )
                }
            }
        }
    }
}