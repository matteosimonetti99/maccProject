package com.example.myapplication

import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class Utility {

    companion object {
        val bootstrapBlue = Color(0xFF007BFF)
        val bootstrapGreen = Color(0xFF28A745)
        val bootstrapRed = Color(0xFFDC3545)
        val bootstrapSecondary = Color(0xFF6C757D)
        val bootstrapInfo = Color(0xFF17A2B8)
        val bootstrapWarning = Color(0xFFFFC107)
        val bootstrapLight = Color(0xFFF8F9FA)
        val bootstrapDark = Color(0xFF343A40)

        @Composable
        fun ErrorSnackbar(
            errorMessage: String?,
            modifier: Modifier = Modifier
        ) {
            Snackbar(
                modifier = modifier
                    .absoluteOffset(y = -16.dp) // Adjust the offset as needed
                    .fillMaxWidth(),
                action = {
                    // Add an action if needed
                },
                backgroundColor = bootstrapRed, // Bootstrap danger color
                elevation = 8.dp,
                contentColor = Color.White // Text color
            ) {
                Text(errorMessage ?: "Default Error Message", color = Color.White)
            }
        }
    }
}
