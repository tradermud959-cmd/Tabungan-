package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isIncome: Boolean,
    val amount: Long,
    val note: String,
    val timestamp: Long = System.currentTimeMillis(),
    val targetId: Int? = null // If null, it's Tabungan Utama
)

@Entity(tableName = "targets")
data class TargetTabungan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Long,
    val currentAmount: Long = 0,
    val orderIndex: Int = 0
)
