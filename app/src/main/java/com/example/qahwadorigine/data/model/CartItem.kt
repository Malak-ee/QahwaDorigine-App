package com.example.qahwadorigine.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String
)