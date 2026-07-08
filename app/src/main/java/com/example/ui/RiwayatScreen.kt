package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.TabungkuViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

enum class RiwayatFilter(val label: String) {
    SEMUA("Semua"),
    TABUNGAN_UTAMA("Tabungan Utama"),
    TARGET_TABUNGAN("Target Tabungan"),
    PEMASUKAN("Pemasukan"),
    PENGELUARAN("Pengeluaran")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(viewModel: TabungkuViewModel) {
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val allTargets by viewModel.allTargets.collectAsStateWithLifecycle()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(RiwayatFilter.SEMUA) }

    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }

    val totalPemasukan = allTransactions.filter { it.isIncome }.sumOf { it.amount }
    val totalPengeluaran = allTransactions.filter { !it.isIncome }.sumOf { it.amount }
    val totalCount = allTransactions.size
    
    val filteredTransactions = allTransactions.filter { tx ->
        val targetName = if (tx.targetId == null) "Tabungan Utama" else allTargets.find { it.id == tx.targetId }?.name ?: "Target"
        val jenisText = if (tx.isIncome) "Pemasukan" else "Pengeluaran"
        
        val matchesSearch = tx.note.contains(searchQuery, ignoreCase = true) || 
                            tx.amount.toString().contains(searchQuery) ||
                            targetName.contains(searchQuery, ignoreCase = true) ||
                            jenisText.contains(searchQuery, ignoreCase = true)
        
        val matchesFilter = when (selectedFilter) {
            RiwayatFilter.SEMUA -> true
            RiwayatFilter.TABUNGAN_UTAMA -> tx.targetId == null
            RiwayatFilter.TARGET_TABUNGAN -> tx.targetId != null
            RiwayatFilter.PEMASUKAN -> tx.isIncome
            RiwayatFilter.PENGELUARAN -> !tx.isIncome
        }
        
        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "RIWAYAT TRANSAKSI",
                color = PrimaryNeon,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lihat seluruh aktivitas tabungan Anda.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Summary Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryCard(
                title = "🟢 PEMASUKAN",
                amount = format.format(totalPemasukan).replace("Rp", "Rp "),
                color = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "🔴 PENGELUARAN",
                amount = format.format(totalPengeluaran).replace("Rp", "Rp "),
                color = ErrorRed,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "🟡 TOTAL TRANSAKSI",
                amount = totalCount.toString(),
                color = PrimaryNeon,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RiwayatFilter.values().forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) PrimaryNeon.copy(alpha = 0.2f) else CardDark)
                        .border(1.dp, if (isSelected) PrimaryNeon else Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filter.label,
                        color = if (isSelected) PrimaryNeon else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari transaksi...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryNeon,
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = CardDark,
                unfocusedContainerColor = CardDark
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // List
        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Empty",
                        tint = PrimaryNeon.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Belum ada riwayat transaksi.", color = TextWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Mulai menabung agar aktivitas muncul di sini.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 120.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredTransactions, key = { it.id }) { tx ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(500)) + fadeIn(tween(500))
                    ) {
                        val targetName = if (tx.targetId == null) "Tabungan Utama" else allTargets.find { it.id == tx.targetId }?.name ?: "Target Dihapus"
                        RiwayatItemCard(
                            isIncome = tx.isIncome,
                            amount = tx.amount,
                            targetName = targetName,
                            note = tx.note,
                            timestamp = tx.timestamp,
                            isUtama = tx.targetId == null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = color)
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(title, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RiwayatItemCard(isIncome: Boolean, amount: Long, targetName: String, note: String, timestamp: Long, isUtama: Boolean) {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
    val color = if (isIncome) SuccessGreen else ErrorRed
    val sign = if (isIncome) "+" else "-"
    val actionText = if (isIncome) "Masuk ke" else "Keluar dari"
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
    
    val context = LocalContext.current
    
    val iconName = if (isIncome) "ic_income" else "ic_expense"
    
    val fallbackIcon = if (isIncome) Icons.Default.CallReceived else Icons.Default.CallMade
    
    val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
    val painter = if (resId != 0) painterResource(id = resId) else rememberVectorPainter(fallbackIcon)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = BorderNeon.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(1.dp, BorderNeon.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painter,
                        contentDescription = "Icon",
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "$actionText $targetName",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = dateFormat.format(Date(timestamp)), color = TextSecondary, fontSize = 10.sp)
                        Text(text = " • ", color = TextSecondary, fontSize = 10.sp)
                        Text(text = timeFormat.format(Date(timestamp)), color = TextSecondary, fontSize = 10.sp)
                    }
                    if (note.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Catatan: $note", color = TextSecondary, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
                }
            }
            Text(
                text = "$sign ${format.format(amount).replace("Rp", "Rp ")}",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
