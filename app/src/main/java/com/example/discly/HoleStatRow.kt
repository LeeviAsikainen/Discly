package com.example.discly.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.discly.AppColors
import com.example.discly.HoleStats

@Composable
fun HoleStatRow(
    stat: HoleStats,
    colors: AppColors
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = stat.holeNumber.toString(),
            color = colors.text,
            fontSize = 18.sp
        )

        Text(
            text = stat.best.toString(),
            color = colors.text,
            fontSize = 18.sp
        )

        Text(
            text = String.format("%.1f", stat.average),
            color = colors.text,
            fontSize = 18.sp
        )

        Text(
            text = stat.worst.toString(),
            color = colors.text,
            fontSize = 18.sp
        )
    }
}