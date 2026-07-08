package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun FloatingMenu(
    onDismiss: () -> Unit,
    onNavigateUtama: () -> Unit,
    onNavigateTarget: () -> Unit
) {
    val items = listOf(
        MenuData("TABUNGAN UTAMA", "Mengelola seluruh pemasukan dan pengeluaran tabungan.", R.drawable.ic_launcher_foreground),
        MenuData("TABUNGAN TARGET", "Mengelola target tabungan beserta progres pencapaiannya.", R.drawable.ic_launcher_foreground)
    )

    val pagerState = rememberPagerState(
        initialPage = 1000, // Start in the middle
        pageCount = { 2000 }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 48.dp),
            modifier = Modifier.fillMaxWidth()
        ) { rawPage ->
            val page = rawPage % items.size
            
            // Calculate scale based on distance from current page
            // Since we can't easily get the absolute offset here in newer foundation, 
            // we just check if it's the currently snapped page
            val isSelected = pagerState.currentPage == rawPage
            val scale by animateFloatAsState(if (isSelected) 1.05f else 0.9f)
            val alpha = if (isSelected) 1f else 0.5f

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .scale(scale)
                    .shadow(if (isSelected) 24.dp else 8.dp, RoundedCornerShape(24.dp), spotColor = BorderNeon)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardDark.copy(alpha = alpha))
                    .border(
                        if (isSelected) 2.dp else 1.dp,
                        BorderNeon.copy(alpha = alpha),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
                    .clickable {
                        // Optional click to select page
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Icon
                    Icon(
                        painter = painterResource(id = items[page].icon),
                        contentDescription = items[page].title,
                        tint = PrimaryNeon,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = items[page].title,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = items[page].desc,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                val actualPage = pagerState.currentPage % items.size
                if (actualPage == 0) onNavigateUtama() else onNavigateTarget()
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .width(200.dp)
                .height(56.dp)
                .shadow(16.dp, spotColor = PrimaryNeon)
        ) {
            Text(text = "MASUK", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

data class MenuData(val title: String, val desc: String, val icon: Int)
