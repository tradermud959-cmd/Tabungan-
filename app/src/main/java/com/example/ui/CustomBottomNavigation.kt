package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderNeon
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite

@Composable
fun CustomBottomNavigation(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    onMulaiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(104.dp)
    ) {
        // Nav background (glass-nav)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xCC151515))
                .border(1.dp, BorderNeon.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    iconName = "ic_home",
                    fallbackIcon = Icons.Default.Home,
                    label = "HOME",
                    isSelected = currentTab == 0,
                    onClick = { onTabSelected(0) }
                )
                
                NavItem(
                    iconName = "ic_history",
                    fallbackIcon = Icons.Default.List,
                    label = "RIWAYAT",
                    isSelected = currentTab == 1,
                    onClick = { onTabSelected(1) }
                )

                Spacer(modifier = Modifier.width(48.dp)) // Space for FAB

                NavItem(
                    iconName = "ic_setting",
                    fallbackIcon = Icons.Default.Settings,
                    label = "PENGATURAN",
                    isSelected = currentTab == 2,
                    onClick = { onTabSelected(2) }
                )

                NavItem(
                    iconName = "ic_info",
                    fallbackIcon = Icons.Default.Info,
                    label = "INFO",
                    isSelected = currentTab == 3,
                    onClick = { onTabSelected(3) }
                )
            }
        }
        
        // FAB
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MulaiButton(onClick = onMulaiClick)
            Spacer(modifier = Modifier.height(4.dp))
            Text("MULAI", color = PrimaryNeon, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        }
    }
}

@Composable
fun NavItem(
    iconName: String,
    fallbackIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    itemAlpha: Float = 1f
) {
    val color = if (isSelected) PrimaryNeon else TextSecondary
    val iconAlpha = if (isSelected) 1f else 0.4f
    val interactionSource = remember { MutableInteractionSource() }

    val context = LocalContext.current
    val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
    val painter = if (resId != 0) painterResource(id = resId) else rememberVectorPainter(fallbackIcon)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        ).alpha(itemAlpha)
    ) {
        Icon(
            painter = painter,
            contentDescription = label,
            tint = if (isSelected) PrimaryNeon else TextWhite.copy(alpha = iconAlpha),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MulaiButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(64.dp)
            .shadow(elevation = 15.dp, shape = CircleShape, spotColor = PrimaryNeon.copy(alpha = 0.4f))
            .clip(CircleShape)
            .background(PrimaryNeon)
            .border(4.dp, BackgroundDark, CircleShape)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Mulai",
            tint = Color.Black,
            modifier = Modifier.size(32.dp)
        )
    }
}
