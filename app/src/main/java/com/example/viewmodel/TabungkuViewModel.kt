package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.TabungkuDatabase
import com.example.model.TargetTabungan
import com.example.model.Transaction
import com.example.repository.SettingsRepository
import com.example.repository.TabungkuRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TabungkuViewModel(application: Application) : AndroidViewModel(application) {

    private val db = TabungkuDatabase.getDatabase(application)
    private val repository = TabungkuRepository(db.transactionDao(), db.targetDao())
    private val settingsRepository = SettingsRepository(application)

    val userName: StateFlow<String> = settingsRepository.userNameFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Pengguna"
    )

    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentTransactions: StateFlow<List<Transaction>> = repository.recentTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val utamaTransactions: StateFlow<List<Transaction>> = repository.utamaTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTargets: StateFlow<List<TargetTabungan>> = repository.allTargets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTransaction(isIncome: Boolean, amount: Long, note: String, targetId: Int? = null) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(isIncome = isIncome, amount = amount, note = note, targetId = targetId)
            )
            // Update target amount if needed
            if (targetId != null) {
                val target = allTargets.value.find { it.id == targetId }
                if (target != null) {
                    val newAmount = if (isIncome) target.currentAmount + amount else target.currentAmount - amount
                    repository.updateTarget(target.copy(currentAmount = newAmount))
                }
            }
        }
    }

    fun addTarget(name: String, targetAmount: Long) {
        viewModelScope.launch {
            repository.insertTarget(
                TargetTabungan(name = name, targetAmount = targetAmount, orderIndex = allTargets.value.size)
            )
        }
    }

    fun updateTarget(target: TargetTabungan) {
        viewModelScope.launch {
            repository.updateTarget(target)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            
            // if it was target transaction, we should probably update the target
            if (transaction.targetId != null) {
                val target = allTargets.value.find { it.id == transaction.targetId }
                if (target != null) {
                    val newAmount = if (transaction.isIncome) target.currentAmount - transaction.amount else target.currentAmount + transaction.amount
                    repository.updateTarget(target.copy(currentAmount = newAmount))
                }
            }
        }
    }

    fun deleteTarget(target: TargetTabungan) {
        viewModelScope.launch {
            repository.deleteTarget(target)
            // also we could delete transactions related to this target but maybe let's keep it simple
        }
    }
    
    fun setUserName(name: String) {
        viewModelScope.launch {
            settingsRepository.setUserName(name)
        }
    }
    
    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }
}
