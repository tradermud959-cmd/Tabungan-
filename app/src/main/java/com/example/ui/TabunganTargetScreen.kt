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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.TargetTabungan
import com.example.ui.theme.*
import com.example.viewmodel.TabungkuViewModel
import java.text.NumberFormat
import java.util.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabunganTargetScreen(viewModel: TabungkuViewModel, onBack: () -> Unit) {
    val allTargets by viewModel.allTargets.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

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
            Text("TABUNGAN TARGET", color = PrimaryNeon, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            if (allTargets.size < 3) {
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Target", tint = PrimaryNeon)
                }
            }
        }

        if (allTargets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada target. Maksimal 3 target.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allTargets) { target ->
                    TargetItemCard(
                        target = target,
                        onAdd = { amount -> viewModel.addTransaction(true, amount, "Nabung ke ${target.name}", target.id) },
                        onEdit = { android.widget.Toast.makeText(context, "Edit sedang dalam pengembangan", android.widget.Toast.LENGTH_SHORT).show() },
                        onDelete = { viewModel.deleteTarget(target) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var targetAmountStr by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = CardDark,
            title = { Text("Tambah Target Baru", color = PrimaryNeon) },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Target", color = TextSecondary) },
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
                        value = targetAmountStr,
                        onValueChange = { if (it.all { char -> char.isDigit() }) targetAmountStr = it },
                        label = { Text("Nominal Target (Rp)", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    val targetAmount = targetAmountStr.toLongOrNull()
                    if (name.isNotBlank() && targetAmount != null && targetAmount > 0) {
                        viewModel.addTarget(name, targetAmount)
                        showAddDialog = false
                    }
                }) {
                    Text("Simpan", color = PrimaryNeon)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun TargetItemCard(target: TargetTabungan, onAdd: (Long) -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
    val progress = if (target.targetAmount > 0) (target.currentAmount.toFloat() / target.targetAmount.toFloat()).coerceIn(0f, 1f) else 0f
    val percentage = (progress * 100).toInt()
    
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = TargetBlue.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(24.dp))
            .background(CardDark)
            .border(1.dp, TargetBlue.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(target.name.uppercase(), color = TargetBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row {
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = ErrorRed)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Terkumpul: ${format.format(target.currentAmount).replace("Rp", "Rp ")}", color = TextWhite, fontSize = 16.sp)
            Text("Target: ${format.format(target.targetAmount).replace("Rp", "Rp ")}", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceDark)
                        .border(1.dp, TargetBlue.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(TargetBlue)
                            .shadow(8.dp, spotColor = TargetBlue)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("$percentage%", color = TargetBlue, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = TargetBlue),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Isi Tabungan", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Isi Tabungan", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showAddDialog) {
        var amountStr by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = CardDark,
            title = { Text("Isi Tabungan", color = TargetBlue) },
            text = {
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { if (it.all { char -> char.isDigit() }) amountStr = it },
                    label = { Text("Nominal (Rp)", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TargetBlue,
                        unfocusedBorderColor = TargetBlue.copy(alpha = 0.5f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = amountStr.toLongOrNull()
                    if (amount != null && amount > 0) {
                        onAdd(amount)
                        showAddDialog = false
                    }
                }) {
                    Text("Simpan", color = TargetBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }
}
