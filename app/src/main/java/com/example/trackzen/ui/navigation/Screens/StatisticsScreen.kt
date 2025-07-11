package com.example.trackzen.ui.navigation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun StatisticsScreen( navController: NavController) {
    val isDark = isSystemInDarkTheme() // use your app toggle state here if needed

    val backgroundColor = if (isDark) Color(0xFF121212) else Color(0xFFF6F8FC)
    val cardColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val titleColor = if (isDark) Color.White else Color(0xFF1C1C1C)
    val labelColor = if (isDark) Color.LightGray else Color.Gray
    val valueColor = if (isDark) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TrackZen",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )

            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = titleColor
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Recent activities",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Cards Grid
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Total Km", "0", Color(0xFF00C853), cardColor, valueColor, Modifier.weight(1f))
                StatCard("AVG Pace", "00.00", Color(0xFF2979FF), cardColor, valueColor, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Total hours", "00:00",
                    Color(0xFFFF6D00), cardColor, valueColor, Modifier.weight(1f))
                StatCard("Total Kcal", "0.61", Color(0xFFD500F9),
                    cardColor, valueColor, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    glowColor: Color,
    cardColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(glowColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = glowColor
                )
            }

            Text(
                text = value,
                fontSize = 24.sp,
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TextStyle.copy(brush: Brush, fontWeight: FontWeight): TextStyle {
    return this.copy(
        brush = brush,
        fontWeight = fontWeight
    )
}




