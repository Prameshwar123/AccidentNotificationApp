package com.example.accidentnotificationapp.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.accidentnotificationapp.components.UserLogo
import com.example.accidentnotificationapp.navigation.UserScreens
import kotlinx.coroutines.delay

@Composable
fun UserSplashScreen(navController: NavController) {
	val scale = remember { Animatable(0f) }
	val fade = remember { Animatable(0f) }
	
	LaunchedEffect(key1 = true) {
		scale.animateTo(
			targetValue = 1f,
			animationSpec = tween(durationMillis = 800, easing = {
				OvershootInterpolator(4f).getInterpolation(it)
			})
		)
		fade.animateTo(
			targetValue = 1f,
			animationSpec = tween(durationMillis = 1200)
		)
		delay(2500L)
		navController.navigate(UserScreens.LoginScreen.name)
	}
	
	Surface(
		modifier = Modifier
			.padding(16.dp)
			.size(320.dp)
			.scale(scale.value),
		shape = CircleShape,
		color = MaterialTheme.colorScheme.primaryContainer,
		border = BorderStroke(3.dp, Color.Cyan)
	) {
		Column(
			modifier = Modifier
				.padding(24.dp)
				.scale(scale.value)
				.alpha(fade.value),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			UserLogo(modifier = Modifier.size(200.dp))
			Spacer(modifier = Modifier.height(10.dp))
			Text(
				text = "\"For you & your safety\"",
				style = MaterialTheme.typography.titleLarge.copy(
					fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
					color = MaterialTheme.colorScheme.onPrimary
				),
				textAlign = androidx.compose.ui.text.style.TextAlign.Center,
				maxLines = 1,
				overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
				modifier = Modifier.padding(horizontal = 8.dp)
			)
		}
	}
}



