package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.TabungkuViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: TabungkuViewModel) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val utamaTransactions by viewModel.utamaTransactions.collectAsStateWithLifecycle()
    val allTargets by viewModel.allTargets.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()

    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while(true) {
            currentTime = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000)
        }
    }

    val totalUtama = utamaTransactions.sumOf { if (it.isIncome) it.amount else -it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            HeaderSection(userName, currentTime)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            MainSavingCard(totalUtama)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            val targetUtama = 10_000_000L
            val utamaProgress = (totalUtama.toFloat() / targetUtama.toFloat()).coerceIn(0f, 1f)
            ProgressBarItem("TABUNGAN UTAMA", totalUtama, targetUtama, utamaProgress, PrimaryNeon)
            Spacer(modifier = Modifier.height(12.dp))
            
            val colors = listOf(TargetGreen, TargetBlue, TargetPurple)
            if (allTargets.isNotEmpty()) {
                allTargets.forEachIndexed { index, target ->
                    val progress = if (target.targetAmount > 0) (target.currentAmount.toFloat() / target.targetAmount.toFloat()).coerceIn(0f, 1f) else 0f
                    ProgressBarItem(target.name.uppercase(), target.currentAmount, target.targetAmount, progress, colors[index % colors.size])
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "AKTIVITAS TERBARU",
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(recentTransactions.take(10), key = { it.id }) { tx ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(500))
            ) {
                ActivityItem(
                    isIncome = tx.isIncome,
                    amount = tx.amount,
                    targetName = if (tx.targetId == null) "Tabungan Utama" else allTargets.find { it.id == tx.targetId }?.name ?: "Target"
                )
            }
        }
    }
}

@Composable
fun HeaderSection(userName: String, currentTime: Long) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(text = "SELAMAT DATANG,", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
            Text(text = userName, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = timeFormat.format(Date(currentTime)), 
                color = PrimaryNeon, 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = dateFormat.format(Date(currentTime)).uppercase(), 
                color = TextSecondary, 
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun MainSavingCard(total: Long) {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    val formattedTotal = format.format(total).replace("Rp", "Rp ")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(24.dp), spotColor = BorderNeon.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(24.dp))
            .background(CardDark)
            .border(1.dp, BorderNeon.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "TABUNGAN UTAMA",
                    color = PrimaryNeon,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .background(SurfaceDark, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderNeon.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                
                val infiniteTransition = rememberInfiniteTransition()
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(SurfaceDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(PrimaryNeon.copy(alpha = alpha), CircleShape)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = formattedTotal,
                color = TextWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Terakhir diperbarui: Baru saja",
                color = TextSecondary,
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ProgressBarItem(title: String, current: Long, target: Long, progress: Float, color: Color) {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    val formattedCurrent = format.format(current).replace("Rp", "Rp")
    val formattedTarget = format.format(target).replace("Rp", "Rp")
    val percentage = (progress * 100).toInt()

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(text = "$percentage% — $formattedCurrent / $formattedTarget", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(SurfaceDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
                    .shadow(4.dp, spotColor = color)
            )
        }
    }
}

@Composable
fun ActivityItem(isIncome: Boolean, amount: Long, targetName: String) {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    val formattedAmount = format.format(amount).replace("Rp", "Rp")
    val color = if (isIncome) SuccessGreen else ErrorRed
    val sign = if (isIncome) "+" else "-"
    val actionText = if (isIncome) "Pemasukan" else "Pengeluaran"
    val icon = if (isIncome) "＋" else "－"
    
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
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                Text(text = targetName, color = TextSecondary, fontSize = 10.sp)
            }
        }
        Text(
            text = "$sign$formattedAmount",
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
