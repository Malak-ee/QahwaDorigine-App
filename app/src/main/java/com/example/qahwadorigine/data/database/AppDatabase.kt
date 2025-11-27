package com.example.qahwadorigine.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.qahwadorigine.data.dao.CartDao
import com.example.qahwadorigine.data.dao.ProductDao
import com.example.qahwadorigine.data.model.CartItem
import com.example.qahwadorigine.data.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.room.migration.Migration

@Database(
    entities = [Product::class, CartItem::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE cart_items ADD COLUMN imageUrl TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "qahwa_database"
                )
                    .addMigrations(MIGRATION_1_2) // ← Ajoutez cette ligne
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDatabase(INSTANCE?.productDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(productDao: ProductDao?) {
            productDao?.let { dao ->
                val products = listOf(
                    Product(
                        name = "Espresso Traditionnel",
                        description = "Café espresso marocain riche et corsé",
                        price = 15.0,
                        imageUrl = "cafe_noir",
                        category = "Café",
                        inStock = true
                    ),
                    Product(
                        name = "Café au chocolat",
                        description = "Café espresso au chocolat et lait riche",
                        price = 15.0,
                        imageUrl = "cafe_chocolat",
                        category = "Café",
                        inStock = true
                    ),
                    Product(
                        name = "Cappuccino Crémeux",
                        description = "Cappuccino avec mousse de lait veloutée",
                        price = 25.0,
                        imageUrl = "cappuccino_cremeux",
                        category = "Café",
                        inStock = true
                    ),
                    Product(
                        name = "Café au lait",
                        description = "Caffée espresso avec mousse de lait veloutée",
                        price = 30.0,
                        imageUrl = "cafe_milk",
                        category = "Café",
                        inStock = true
                    ),
                    Product(
                        name = "Thé à la Menthe",
                        description = "Thé vert marocain à la menthe fraîche",
                        price = 12.0,
                        imageUrl = "the_menthe",
                        category = "Thé",
                        inStock = true
                    ),
                    Product(
                        name = "Thé Asian noir",
                        description = "Thé noir Asiatique riche et corsé",
                        price = 12.0,
                        imageUrl = "black_the",
                        category = "Thé",
                        inStock = true
                    ),
                    Product(
                        name = "Thé vert",
                        description = "Thé vert marocain riche et corsé",
                        price = 10.0,
                        imageUrl = "green_the",
                        category = "Thé",
                        inStock = true
                    ),
                    Product(
                        name = "Cupcake au chocolat",
                        description = "Cupcake crémeux au chocolat",
                        price = 20.0,
                        imageUrl = "cupcake",
                        category = "Pâtisserie",
                        inStock = true
                    ),
                    Product(
                        name = "Ghriba",
                        description = "Moroccan ghriba biscuit",
                        price = 15.0,
                        imageUrl = "ghriba",
                        category = "Pâtisserie",
                        inStock = true
                    ),
                    Product(
                        name = "Cake au chocolat",
                        description = "Gateaux au chocolat et noisette",
                        price = 15.0,
                        imageUrl = "cake_chocolat",
                        category = "Pâtisserie",
                        inStock = true
                    ),
                    Product(
                        name = "Tarte aux fraises",
                        description = "Tarte aux fraises crémeuse",
                        price = 30.0,
                        imageUrl = "p_fraise",
                        category = "Pâtisserie",
                        inStock = true
                    )
                )
                dao.insertAllProducts(products)
            }
        }
    }
}