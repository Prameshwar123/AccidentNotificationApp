package com.example.accidentnotificationapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UserLogo(modifier: Modifier = Modifier) {
	Text(
		modifier = modifier.padding(bottom = 16.dp),
		text = "  Accident\nNotification\n       App",
		style = MaterialTheme.typography.displaySmall,
		color = Color.Blue.copy(alpha = 0.7f)
	)
}