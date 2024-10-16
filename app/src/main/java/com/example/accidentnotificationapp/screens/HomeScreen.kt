package com.example.accidentnotificationapp.screens

import androidx.compose.ui.tooling.preview.Preview

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import com.example.accidentnotificationapp.data.Contact
import com.example.accidentnotificationapp.data.UserPreferences
import com.example.accidentnotificationapp.navigation.UserScreens
import com.example.accidentnotificationapp.network.*
import kotlinx.coroutines.launch

@Composable
fun Home(navController: NavController, userPreferences: UserPreferences) {
	Surface(modifier = Modifier.fillMaxSize()) {
		HomeContent(navController, userPreferences)
	}
}

@Composable
fun HomeContent(navController: NavController, userPreferences: UserPreferences) {
	var contacts by remember { mutableStateOf(listOf<Contact>()) }
	var showDialog by remember { mutableStateOf(false) }
	var showLogoutDialog by remember { mutableStateOf(false) }
	var menuExpanded by remember { mutableStateOf(false) }
	val scope = rememberCoroutineScope()
	val context = LocalContext.current
	
	LaunchedEffect(Unit) {
		getContacts(context) { success, fetchedContacts ->
			if (success) {
				contacts = fetchedContacts?.map { apiContactToUiContact(it) } ?: listOf()
			} else {
				Toast.makeText(context, "Failed to load contacts", Toast.LENGTH_SHORT).show()
			}
		}
	}
	
	Scaffold(
		topBar = {
			HomeTopBar(
				menuExpanded = menuExpanded,
				onMenuExpand = { menuExpanded = it },
				onLogoutClick = { showLogoutDialog = true }
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = { showDialog = true }) {
				Text("+")
			}
		},
		content = {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(it),
				contentAlignment = Alignment.TopStart
			) {
				if (contacts.isEmpty()) {
					EmptyContactState()
				} else {
					ContactList(contacts)
				}
				
				if (showDialog) {
					AddContactDialog(
						onDismiss = { showDialog = false },
						onAddContact = { newContact ->
							scope.launch {
								val apiContact = uiContactToApiContact(newContact)
								addContact(apiContact.name, apiContact.phoneNumber, context) { success, message ->
									if (success) {
										contacts = contacts + newContact
										Toast.makeText(
											context,
											message ?: "Contact added successfully",
											Toast.LENGTH_SHORT
										).show()
									} else {
										Toast.makeText(
											context,
											message ?: "Failed to add contact",
											Toast.LENGTH_SHORT
										).show()
									}
								}
							}
						}
					)
				}
				
				if (showLogoutDialog) {
					LogoutDialog(
						onDismiss = { showLogoutDialog = false },
						onConfirmLogout = {
							scope.launch {
								performLogout(context) { success, message ->
									if (success) {
										scope.launch {
											userPreferences.logout()
										}
										navController.navigate(UserScreens.LoginScreen.name)
									} else {
										Toast.makeText(
											context,
											message ?: "Logout failed",
											Toast.LENGTH_SHORT
										).show()
									}
								}
							}
						}
					)
				}
			}
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
	menuExpanded: Boolean,
	onMenuExpand: (Boolean) -> Unit,
	onLogoutClick: () -> Unit
) {
	TopAppBar(
		title = {
			Text(
				text = "Accident Notification App",
				color = Color.Black,
				fontSize = 20.sp,
				fontWeight = FontWeight.W400
			)
		},
		navigationIcon = {
			IconButton(onClick = { onMenuExpand(true) }) {
				Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
			}
			DropdownMenu(
				expanded = menuExpanded,
				onDismissRequest = { onMenuExpand(false) }
			) {
				DropdownMenuItem(text = { Text("Action 1") }, onClick = { onMenuExpand(false) })
				DropdownMenuItem(text = { Text("Action 2") }, onClick = { onMenuExpand(false) })
				DropdownMenuItem(text = { Text("Action 3") }, onClick = { onMenuExpand(false) })
			}
		},
		actions = {
			IconButton(onClick = onLogoutClick) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ExitToApp,
					contentDescription = "Logout",
					tint = Color.Black
				)
			}
			IconButton(onClick = { }) {
				Icon(
					imageVector = Icons.Filled.Notifications,
					contentDescription = "Notifications",
					tint = Color.Black,
					modifier = Modifier.padding(end = 6.dp)
				)
			}
		},
		colors = TopAppBarDefaults.mediumTopAppBarColors(
			containerColor = MaterialTheme.colorScheme.primaryContainer
		)
	)
}

@Composable
fun EmptyContactState() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		Text(
			text = "No contacts available,\n\nPlease add a contact.",
			style = MaterialTheme.typography.titleMedium.copy(
				fontSize = 27.sp,
				fontWeight = FontWeight.Light
			),
			textAlign = TextAlign.Center
		)
	}
}

@Composable
fun ContactList(contacts: List<Contact>) {
	LazyColumn(modifier = Modifier.padding(16.dp)) {
		items(contacts.size) { index ->
			ContactCard(contact = contacts[index])
		}
	}
}

@Composable
fun AddContactDialog(
	onDismiss: () -> Unit,
	onAddContact: (Contact) -> Unit
) {
	var name by remember { mutableStateOf("") }
	var phoneNumber by remember { mutableStateOf("") }
	val context = LocalContext.current
	Dialog(onDismissRequest = onDismiss) {
		Surface(
			shape = RoundedCornerShape(8.dp),
			color = MaterialTheme.colorScheme.surface,
		) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(text = "Add Contact", style = MaterialTheme.typography.titleMedium)
				Spacer(modifier = Modifier.height(8.dp))
				OutlinedTextField(
					value = name,
					onValueChange = { name = it },
					label = { Text("Name") }
				)
				Spacer(modifier = Modifier.height(8.dp))
				OutlinedTextField(
					value = phoneNumber,
					onValueChange = { phoneNumber = it },
					label = { Text("Phone Number") }
				)
				Spacer(modifier = Modifier.height(16.dp))
				Row(modifier = Modifier.align(Alignment.End)) {
					TextButton(onClick = onDismiss) {
						Text("Cancel")
					}
					Spacer(modifier = Modifier.width(8.dp))
					Button(onClick = {
						if (name.isNotBlank() && phoneNumber.isNotBlank() && phoneNumber.length == 10 && phoneNumber.isDigitsOnly()) {
							onAddContact(Contact(name, phoneNumber))
							onDismiss()
						} else {
							Toast.makeText(
								context,
								"Please enter valid name and phone number",
								Toast.LENGTH_SHORT
							).show()
						}
					}) {
						Text("Add")
					}
				}
			}
		}
	}
}

@Composable
fun LogoutDialog(onDismiss: () -> Unit, onConfirmLogout: () -> Unit) {
	Dialog(onDismissRequest = onDismiss) {
		Surface(
			shape = RoundedCornerShape(8.dp),
			color = MaterialTheme.colorScheme.surface,
			modifier = Modifier.padding(16.dp)
		) {
			Column {
				Text(
					text = "Are you sure you want to log out?",
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier.padding(16.dp)
				)
				Row(modifier = Modifier.align(Alignment.End).padding(16.dp)) {
					Button(onClick = onConfirmLogout) {
						Text("Yes")
					}
					Spacer(modifier = Modifier.width(8.dp))
					TextButton(onClick = onDismiss) {
						Text("No")
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun ContactCard(contact: Contact = Contact(name = "John Doe", phoneNumber = "1234567890")) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(7.dp),
		shape = RoundedCornerShape(topEnd = 33.dp, bottomStart = 33.dp),
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(text = contact.name, style = MaterialTheme.typography.titleMedium)
			Spacer(modifier = Modifier.padding(4.dp))
			Text(text = contact.phoneNumber, style = MaterialTheme.typography.bodyMedium)
		}
	}
}

fun apiContactToUiContact(apiContact: Contact): Contact {
	return Contact(name = apiContact.name, phoneNumber = apiContact.phoneNumber)
}

fun uiContactToApiContact(uiContact: Contact): ApiContact {
	return ApiContact(_id = null, name = uiContact.name, phoneNumber = uiContact.phoneNumber)
}
