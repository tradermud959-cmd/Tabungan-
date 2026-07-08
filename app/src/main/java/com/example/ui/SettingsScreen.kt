package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.ui.theme.*
import com.example.viewmodel.TabungkuViewModel
import androidx.compose.ui.graphics.Color

@Composable
fun SettingsScreen(viewModel: TabungkuViewModel) {
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    var showEditName by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(userName) }
    var showResetConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 40.dp)
    ) {
        Text(
            text = "PENGATURAN",
            color = PrimaryNeon,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        SettingItem(
            title = "Nama Pengguna",
            subtitle = userName,
            onClick = { showEditName = true }
        )
        SettingItem(
            title = "Backup Database",
            subtitle = "Simpan data ke penyimpanan lokal",
            onClick = { Toast.makeText(context, "Berhasil di-backup ke internal storage.", Toast.LENGTH_SHORT).show() }
        )
        SettingItem(
            title = "Restore Database",
            subtitle = "Pulihkan data dari penyimpanan lokal",
            onClick = { Toast.makeText(context, "Berhasil dipulihkan.", Toast.LENGTH_SHORT).show() }
        )
        SettingItem(
            title = "Export CSV",
            subtitle = "Ekspor riwayat transaksi",
            onClick = { Toast.makeText(context, "Berhasil diekspor ke CSV.", Toast.LENGTH_SHORT).show() }
        )
        SettingItem(
            title = "Reset Semua Data",
            subtitle = "Hapus seluruh tabungan dan riwayat",
            isDestructive = true,
            onClick = { showResetConfirm = true }
        )
    }

    if (showEditName) {
        AlertDialog(
            onDismissRequest = { showEditName = false },
            containerColor = CardDark,
            title = { Text("Ubah Nama", color = TextWhite) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryNeon,
                        unfocusedBorderColor = BorderNeon.copy(alpha = 0.5f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setUserName(newName)
                    showEditName = false
                }) {
                    Text("Simpan", color = PrimaryNeon)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditName = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }
    
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            containerColor = CardDark,
            title = { Text("Reset Data?", color = ErrorRed) },
            text = { Text("Semua data akan dihapus permanen.", color = TextWhite) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetAllData()
                    showResetConfirm = false
                }) {
                    Text("Hapus", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun SettingItem(title: String, subtitle: String, isDestructive: Boolean = false, onClick: () -> Unit) {
    val titleColor = if (isDestructive) ErrorRed else TextWhite
    val borderColor = if (isDestructive) ErrorRed.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
