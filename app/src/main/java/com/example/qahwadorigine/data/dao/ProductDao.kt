package com.example.qahwadorigine.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.qahwadorigine.data.model.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE inStock = 1")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category AND inStock = 1")
    fun getProductsByCategory(category: String): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Int): LiveData<Product?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<Product>)

    @Delete
    suspend fun deleteProduct(product: Product)
}