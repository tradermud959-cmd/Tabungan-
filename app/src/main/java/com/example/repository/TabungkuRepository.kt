package com.example.repository

import com.example.database.TargetDao
import com.example.database.TransactionDao
import com.example.model.TargetTabungan
import com.example.model.Transaction
import kotlinx.coroutines.flow.Flow

class TabungkuRepository(
    private val transactionDao: TransactionDao,
    private val targetDao: TargetDao
) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val recentTransactions: Flow<List<Transaction>> = transactionDao.getRecentTransactions(10)
    val utamaTransactions: Flow<List<Transaction>> = transactionDao.getUtamaTransactions()
    val allTargets: Flow<List<TargetTabungan>> = targetDao.getAllTargets()

    fun getTransactionsByTarget(targetId: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByTarget(targetId)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun insertTarget(target: TargetTabungan) {
        targetDao.insertTarget(target)
    }

    suspend fun updateTarget(target: TargetTabungan) {
        targetDao.updateTarget(target)
    }

    suspend fun deleteTarget(target: TargetTabungan) {
        targetDao.deleteTarget(target)
    }

    suspend fun resetAllData() {
        transactionDao.deleteAll()
        targetDao.deleteAll()
    }
}
