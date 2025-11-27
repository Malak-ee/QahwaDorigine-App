package com.example.qahwadorigine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qahwadorigine.data.dao.ProductDao
import com.example.qahwadorigine.data.model.Product
import com.example.qahwadorigine.iterator.ProductCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val productDao: ProductDao) : ViewModel() {
    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts

    fun filterByCategory(category: String?) {
        viewModelScope.launch {
            allProducts.value?.let { products ->
                val collection = ProductCollection(products)
                val iterator = if (category != null) {
                    collection.createIteratorByCategory(category)
                } else {
                    collection.createIterator()
                }

                val filtered = mutableListOf<Product>()
                while (iterator.hasNext()) {
                    iterator.next()?.let { filtered.add(it) }
                }
                _filteredProducts.value = filtered
            }
        }
    }

    fun selectProduct(productId: Int) {
        viewModelScope.launch {
            val product = productDao.getProductById(productId).value
            _selectedProduct.value = product
        }
    }

    fun getProductById(productId: Int): LiveData<Product?> {
        return productDao.getProductById(productId)
    }
}