package com.example.qahwadorigine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qahwadorigine.data.dao.CartDao
import com.example.qahwadorigine.data.model.CartItem
import com.example.qahwadorigine.data.model.Product
import com.example.qahwadorigine.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val cartDao: CartDao) : ViewModel() {
    val cartItems: LiveData<List<CartItem>> = cartDao.getAllCartItems()
    val totalPrice: LiveData<Double?> = cartDao.getTotalPrice()


    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            val existingItem = cartDao.getCartItemByProductId(product.id)

            if (existingItem != null) {
                val updatedItem = existingItem.copy(
                    quantity = existingItem.quantity + quantity
                )
                cartDao.updateCartItem(updatedItem)
            } else {
                // Nouvel article
                val cartItem = CartItem(
                    productId = product.id,
                    productName = product.name,
                    price = product.price,
                    quantity = quantity,
                    imageUrl = product.imageUrl
                )
                cartDao.insertCartItem(cartItem)
            }
        }
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                cartDao.updateCartItem(cartItem.copy(quantity = newQuantity))
            } else {
                cartDao.deleteCartItem(cartItem)
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartDao.deleteCartItem(cartItem)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartDao.clearCart()
        }
    }


    fun calculateTotal(items: List<CartItem>?): Double {
        return items?.sumOf { it.price * it.quantity } ?: 0.0
    }
}