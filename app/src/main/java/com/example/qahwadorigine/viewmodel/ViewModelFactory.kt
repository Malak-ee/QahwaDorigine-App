package com.example.qahwadorigine.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qahwadorigine.data.dao.CartDao
import com.example.qahwadorigine.data.dao.ProductDao

class ViewModelFactory(
    private val productDao: ProductDao,
    private val cartDao: CartDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductViewModel::class.java) -> {
                ProductViewModel(productDao) as T
            }
            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(cartDao) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}