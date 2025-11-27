package com.example.qahwadorigine.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.qahwadorigine.data.model.CartItem
import com.example.qahwadorigine.data.model.Product

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): LiveData<List<CartItem>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItemByProductId(productId: Int): CartItem?

    @Query("SELECT SUM(price * quantity) FROM cart_items")
    fun getTotalPrice(): LiveData<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}