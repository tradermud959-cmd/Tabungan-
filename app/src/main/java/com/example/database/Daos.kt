package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.model.Transaction
import com.example.model.TargetTabungan
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE targetId = :targetId ORDER BY timestamp DESC")
    fun getTransactionsByTarget(targetId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE targetId IS NULL ORDER BY timestamp DESC")
    fun getUtamaTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
    
    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

@Dao
interface TargetDao {
    @Query("SELECT * FROM targets ORDER BY orderIndex ASC")
    fun getAllTargets(): Flow<List<TargetTabungan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: TargetTabungan)

    @Update
    suspend fun updateTarget(target: TargetTabungan)

    @Delete
    suspend fun deleteTarget(target: TargetTabungan)
    
    @Query("DELETE FROM targets")
    suspend fun deleteAll()
}
