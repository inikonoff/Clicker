package com.inikonoff.clicker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DigitDisplay(
    count: Int,
    style: String, // "mechanical" or "electronic"
    modifier: Modifier = Modifier
) {
    val digits = count.toString()
    when (style) {
        "electronic" -> ElectronicDigits(digits, modifier)
        else -> MechanicalDigits(digits, modifier)
    }
}

@Composable
private fun MechanicalDigits(digits: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF111111))
            .border(2.dp, Color(0xFF555555), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row {
            digits.forEach { digit ->
                AnimatedContent(
                    targetState = digit,
                    transitionSpec = {
                        slideInVertically { it } togetherWith slideOutVertically { -it }
                    },
                    label = "digit"
                ) { d ->
                    Text(
                        text = d.toString(),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ElectronicDigits(digits: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A2A0A))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row {
            digits.forEach { digit ->
                AnimatedContent(
                    targetState = digit,
                    transitionSpec = {
                        slideInVertically { it } togetherWith slideOutVertically { -it }
                    },
                    label = "digit"
                ) { d ->
                    Text(
                        text = d.toString(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF39FF14),
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}
