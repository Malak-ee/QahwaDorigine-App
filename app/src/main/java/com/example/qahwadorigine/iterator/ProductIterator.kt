package com.example.qahwadorigine.iterator

import com.example.qahwadorigine.data.model.Product

interface Iterator<T> {
    fun hasNext(): Boolean
    fun next(): T?
    fun reset()
}
interface Aggregate<T> {
    fun createIterator(): Iterator<T>
}
class ProductIterator(
    private val products: List<Product>,
    private val category: String? = null
) : Iterator<Product> {
    private var currentPosition = 0
    private val filteredProducts: List<Product>

    init {

        filteredProducts = if (category != null) {
            products.filter { it.category == category && it.inStock }
        } else {
            products.filter { it.inStock }
        }
    }

    override fun hasNext(): Boolean {
        return currentPosition < filteredProducts.size
    }

    override fun next(): Product? {
        return if (hasNext()) {
            filteredProducts[currentPosition++]
        } else {
            null
        }
    }

    override fun reset() {
        currentPosition = 0
    }

    fun getCurrentProduct(): Product? = filteredProducts.getOrNull(currentPosition)
}

class ProductCollection(private val products: List<Product>) : Aggregate<Product> {
    override fun createIterator(): Iterator<Product> {
        return ProductIterator(products)
    }

    fun createIteratorByCategory(category: String): Iterator<Product> {
        return ProductIterator(products, category)
    }
}