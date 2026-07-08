package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.Transaction
import com.example.model.TargetTabungan

@Database(entities = [Transaction::class, TargetTabungan::class], version = 1, exportSchema = false)
abstract class TabungkuDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun targetDao(): TargetDao

    companion object {
        @Volatile
        private var INSTANCE: TabungkuDatabase? = null

        fun getDatabase(context: Context): TabungkuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TabungkuDatabase::class.java,
                    "tabungku_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
