package com.example.nammashaaleinventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [AssetEntity::class, CategoryEntity::class, UserEntity::class, RepairTicketEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun repairTicketDao(): RepairTicketDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        private val defaultCategories = listOf(
            "Tablet", "Furniture", "Lab Tool", "Sports Kit", "Library Item",
            "Electronics", "Classroom Utility", "Stationery", "Laboratory Equipment",
            "Office Equipment", "Smart Devices", "Musical Instrument", "Cleaning Supply",
            "Medical Kit", "Projector", "Computer Accessory", "Science Model",
            "Art Supply", "Examination Material", "Security Equipment"
        )

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "inventory_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed data on first creation
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            val assetDao = database.assetDao()
                            val categoryDao = database.categoryDao()
                            val userDao = database.userDao()
                            val repairTicketDao = database.repairTicketDao()
                            
                            // Seed default categories
                            defaultCategories.forEach { 
                                categoryDao.insertCategory(CategoryEntity(name = it, isDefault = true)) 
                            }
                            
                            // Seed sample assets
                            SampleData.assets.forEach { assetDao.insertAsset(it) }

                            // Seed sample tickets
                            SampleData.tickets.forEach { repairTicketDao.insertTicket(it) }

                            // Seed default users
                            SampleData.users.forEach { userDao.insertUser(it) }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                .also { Instance = it }
            }
        }
    }
}
