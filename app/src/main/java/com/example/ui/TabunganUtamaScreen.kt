package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.TabungkuViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabunganUtamaScreen(viewModel: TabungkuViewModel, onBack: () -> Unit) {
    val transactions by viewModel.utamaTransactions.collectAsStateWithLifecycle()
    val total = transactions.sumOf { if (it.isIncome) it.amount else -it.amount }
    
    var showDialog by remember { mutableStateOf(false) }
    var isAdd by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 24.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = PrimaryNeon,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("TABUNGAN UTAMA", color = PrimaryNeon, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Summary Card
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = BorderNeon.copy(alpha = 0.3f))
                .clip(RoundedCornerShape(24.dp))
                .background(CardDark)
                .border(1.dp, BorderNeon.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Saldo", color = TextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(format.format(total).replace("Rp", "Rp "), color = PrimaryNeon, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { isAdd = true; showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { isAdd = false; showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Tarik", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tarik", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Riwayat Transaksi", color = TextWhite, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // History List
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 32.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(transactions, key = { it.id }) { tx ->
                HistoryItemRow(
                    isIncome = tx.isIncome,
                    amount = tx.amount,
                    note = tx.note,
                    timestamp = tx.timestamp,
                    onDelete = { viewModel.deleteTransaction(tx) }
                )
            }
        }
    }

    if (showDialog) {
        TransactionDialog(
            isIncome = isAdd,
            onDismiss = { showDialog = false },
            onConfirm = { amount, note ->
                viewModel.addTransaction(isAdd, amount, note, null)
                showDialog = false
            }
        )
    }
}

@Composable
fun HistoryItemRow(isIncome: Boolean, amount: Long, note: String, timestamp: Long, onDelete: () -> Unit) {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
    val color = if (isIncome) SuccessGreen else ErrorRed
    val sign = if (isIncome) "+" else "-"
    val actionText = note.ifEmpty { if (isIncome) "Pemasukan" else "Pengeluaran" }
    val icon = if (isIncome) "＋" else "－"
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = actionText, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = dateFormat.format(Date(timestamp)), color = TextSecondary, fontSize = 10.sp)
            }
        }
        Text(
            text = "$sign${format.format(amount).replace("Rp", "Rp ")}",
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TransactionDialog(isIncome: Boolean, onDismiss: () -> Unit, onConfirm: (Long, String) -> Unit) {
    var amountStr by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text(if (isIncome) "Tambah Saldo" else "Tarik Saldo", color = if (isIncome) SuccessGreen else ErrorRed) },
        text = {
            Column {
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { if (it.all { char -> char.isDigit() }) amountStr = it },
                    label = { Text("Nominal (Rp)", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryNeon,
                        unfocusedBorderColor = BorderNeon.copy(alpha = 0.5f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Catatan", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryNeon,
                        unfocusedBorderColor = BorderNeon.copy(alpha = 0.5f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = amountStr.toLongOrNull()
                if (amount != null && amount > 0) {
                    onConfirm(amount, note)
                }
            }) {
                Text("Simpan", color = PrimaryNeon)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}
