package com.example.accidentnotificationapp.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.accidentnotificationapp.data.UserPreferences
import com.example.accidentnotificationapp.navigation.UserScreens
import com.example.accidentnotificationapp.network.performLogin
import com.example.accidentnotificationapp.network.performSignup
import kotlinx.coroutines.launch

@Composable
fun UserLoginScreen(navController: NavController, userPreferences: UserPreferences) {
	var isLoginScreen by remember { mutableStateOf(true) }
	var name by remember { mutableStateOf("") }
	var email by remember { mutableStateOf("") }
	var password by remember { mutableStateOf("") }
	var showMessage by remember { mutableStateOf<String?>(null) }
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()
	
	val isLoggedIn by userPreferences.isLoggedIn.collectAsState(initial = false)
	
	LaunchedEffect(isLoggedIn) {
		if (isLoggedIn) {
			navController.navigate(UserScreens.HomeScreen.name)
		}
	}
	
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		if (isLoginScreen) {
			EmailPasswordFields(
				email = email,
				onEmailChange = { email = it },
				password = password,
				onPasswordChange = { password = it }
			)
			Spacer(modifier = Modifier.height(16.dp))
			AuthButton(
				text = "Login",
				onClick = {
					performLogin(email, password, context) { success, message ->
						if (success) {
								navController.navigate(UserScreens.HomeScreen.name)
						} else {
							showMessage = message
						}
					}
				}
			)
			if (showMessage != null) {
				ShowToast(showMessage!!)
				showMessage = null
			}
			Spacer(modifier = Modifier.height(8.dp))
			SwitchAuthModeText(
				text = "New user? Sign up here",
				onClick = { isLoginScreen = false }
			)
		} else {
			TextFieldWithLabel(
				value = name,
				onValueChange = { name = it },
				label = "Name"
			)
			Spacer(modifier = Modifier.height(8.dp))
			EmailPasswordFields(
				email = email,
				onEmailChange = { email = it },
				password = password,
				onPasswordChange = { password = it }
			)
			Spacer(modifier = Modifier.height(16.dp))
			AuthButton(
				text = "Signup",
				onClick = {
					performSignup(name, email, password, context) { success, message ->
						if (success) {
							navController.navigate(UserScreens.HomeScreen.name)
						} else {
							showMessage = message
						}
					}
				}
			)
			if (showMessage != null) {
				ShowToast(showMessage!!)
				showMessage = null
			}
			Spacer(modifier = Modifier.height(8.dp))
			SwitchAuthModeText(
				text = "Already have an account? Login here",
				onClick = { isLoginScreen = true }
			)
		}
	}
}

@Composable
fun ShowToast(message: String) {
	val context = LocalContext.current
	Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun TextFieldWithLabel(
	value: String,
	onValueChange: (String) -> Unit,
	label: String
) {
	TextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) }
	)
}

@Composable
fun EmailPasswordFields(
	email: String,
	onEmailChange: (String) -> Unit,
	password: String,
	onPasswordChange: (String) -> Unit
) {
	TextFieldWithLabel(
		value = email,
		onValueChange = onEmailChange,
		label = "Email"
	)
	Spacer(modifier = Modifier.height(8.dp))
	TextFieldWithLabel(
		value = password,
		onValueChange = onPasswordChange,
		label = "Password"
	)
}

@Composable
fun AuthButton(
	text: String,
	onClick: () -> Unit
) {
	Button(onClick = onClick) {
		Text(text)
	}
}

@Composable
fun SwitchAuthModeText(
	text: String,
	onClick: () -> Unit
) {
	Text(
		text = text,
		color = Color.Blue,
		modifier = Modifier.clickable { onClick() }
	)
}
