package com.example.accidentnotificationapp.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import com.example.accidentnotificationapp.data.ApiContact
import com.example.accidentnotificationapp.data.Contact
import com.example.accidentnotificationapp.data.UserPreferences
import com.example.accidentnotificationapp.getAddress
import com.example.accidentnotificationapp.isAccidentHappened
import com.example.accidentnotificationapp.navigation.UserScreens
import com.example.accidentnotificationapp.network.addContact
import com.example.accidentnotificationapp.network.deleteContact
import com.example.accidentnotificationapp.network.getContacts
import com.example.accidentnotificationapp.network.performLogout
import com.example.accidentnotificationapp.sms.sendSMS
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val REQUEST_SMS_PERMISSION = 101

@Composable
fun Home(
	navController: NavController,
) {
	Surface(modifier = Modifier.fillMaxSize()) {
		HomeContent(navController)
	}
}

@Composable
fun HomeContent(
	navController: NavController,
) {
	var IdContacts by remember { mutableStateOf(listOf<ApiContact>()) }
	var contacts by remember { mutableStateOf(listOf<Contact>()) }
	var showAddDialog by remember { mutableStateOf(false) }
	var showDeleteDialog by remember { mutableStateOf<Contact?>(null) }
	var showLogoutDialog by remember { mutableStateOf(false) }
	var menuExpanded by remember { mutableStateOf(false) }
	var accidentDetected = isAccidentHappened()
	val scope = rememberCoroutineScope()
	val address = getAddress()
	val context = LocalContext.current
	val userPreferences = remember { UserPreferences(context) }
	
	var permissionGranted by remember { mutableStateOf(false) }
	LaunchedEffect(Unit) {
		getContacts(context) { success, fetchedContacts ->
			if (success) {
				if (fetchedContacts != null) {
					IdContacts = fetchedContacts
				}
				contacts = fetchedContacts?.map { apiContactToUiContact(it) } ?: listOf()
			} else {
				Toast.makeText(context, "Failed to load contacts", Toast.LENGTH_SHORT).show()
			}
		}
		if(ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED ) { permissionGranted = true } else {
			ActivityCompat.requestPermissions(
				context as Activity,
				arrayOf(Manifest.permission.SEND_SMS),
				REQUEST_SMS_PERMISSION
			)
		}
	}
	var allConditionsMet by remember {
		mutableStateOf(false)
	}
	LaunchedEffect(Unit) {
		while (!allConditionsMet) {
			permissionGranted = ContextCompat.checkSelfPermission(
				context,
				Manifest.permission.SEND_SMS
			) == PackageManager.PERMISSION_GRANTED
			if (permissionGranted && accidentDetected && contacts.isNotEmpty()) {
				allConditionsMet = true
				accidentDetected = false
			}
			delay(1000L)
		}
	}
	if(allConditionsMet) {
		sendSMS(context = context, contacts = contacts, address = address)
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
			FloatingActionButton(onClick = { showAddDialog = true }) {
				Text("+")
			}
		},
		content = { it ->
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(it),
				contentAlignment = Alignment.TopStart
			) {
				if (contacts.isEmpty()) {
					EmptyContactState()
				} else {
					ContactList(contacts, onDeleteClick = { contact ->
						showDeleteDialog = contact
					})
				}
				if (showAddDialog) {
					AddContactDialog(
						onDismiss = { showAddDialog = false },
						onAddContact = { newContact ->
							if(contacts.contains(newContact)) {
								Toast.makeText(
									context,
									"Contact already exist",
									Toast.LENGTH_SHORT
								).show()
							}
							else {
								scope.launch {
									addContact(
										newContact.name,
										newContact.phoneNumber,
										context
									) { success, message ->
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
						}
					)
				}
				
				showDeleteDialog?.let { contact ->
					CustomAlertDialog(
						title = "Delete Contact",
						message = "Are you sure you want to delete ${contact.name}?",
						onDismiss = { showDeleteDialog = null },
						onConfirm = {
							scope.launch {
								val id = getIdOfContact(contact, IdContacts)
								deleteContact(id!!, context) { success, _ ->
									if (success) {
										contacts = contacts.filter { it.phoneNumber != contact.phoneNumber }
										Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show()
									} else {
										Toast.makeText(context, "Failed to delete contact", Toast.LENGTH_SHORT).show()
									}
								}
								showDeleteDialog = null
							}
						}
					)
				}
				
				if (showLogoutDialog) {
					CustomAlertDialog(
						title = "Logout",
						message = "Are you sure you want to log out?",
						onDismiss = { showLogoutDialog = false },
						onConfirm = {
							scope.launch {
								performLogout(context) { success, message ->
									if (success) {
										scope.launch {
											userPreferences.logout()
										}
										navController.navigate(UserScreens.LoginScreen.name)
									} else {
										Toast.makeText(context, message ?: "Logout failed", Toast.LENGTH_SHORT).show()
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

fun getIdOfContact(contact: Contact, contacts: List<ApiContact>): String? {
	return contacts.firstOrNull {
		it.phoneNumber == contact.phoneNumber
	}?._id
}

@Composable
fun CustomAlertDialog(
	title: String,
	message: String,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit
) {
	Dialog(onDismissRequest = onDismiss) {
		Surface(
			shape = RoundedCornerShape(8.dp),
			color = MaterialTheme.colorScheme.surface,
			modifier = Modifier.padding(16.dp)
		) {
			Column(
				modifier = Modifier.padding(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
				Spacer(modifier = Modifier.height(8.dp))
				Text(text = message, textAlign = TextAlign.Center)
				Spacer(modifier = Modifier.height(16.dp))
				Row(horizontalArrangement = Arrangement.SpaceBetween) {
					Button(onClick = onConfirm) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(menuExpanded: Boolean, onMenuExpand: (Boolean) -> Unit, onLogoutClick: () -> Unit) {
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
		},
		actions = {
			IconButton(onClick = onLogoutClick) {
				Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.Black)
			}
			IconButton(onClick = { }) {
				Icon(imageVector = Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.Black, modifier = Modifier.padding(end = 6.dp))
			}
		},
		colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
	)
}

@Composable
fun EmptyContactState() {
	Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		Text(
			text = "No contacts available,\n\nPlease add a contact.",
			style = MaterialTheme.typography.titleMedium.copy(fontSize = 27.sp, fontWeight = FontWeight.Light),
			textAlign = TextAlign.Center
		)
	}
}

@Composable
fun ContactList(contacts: List<Contact>, onDeleteClick: (Contact) -> Unit) {
	LazyColumn(modifier = Modifier.padding(16.dp)) {
		items(contacts.size) { index ->
			ContactCard(contact = contacts[index], onDeleteClick = onDeleteClick)
		}
	}
}

@Composable
fun AddContactDialog(onDismiss: () -> Unit, onAddContact: (Contact) -> Unit) {
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
fun ContactCard(
	contact: Contact = Contact(name = "John Doe", phoneNumber = "1234567890"),
	onDeleteClick: (Contact) -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(7.dp),
		shape = RoundedCornerShape(topEnd = 33.dp, bottomStart = 33.dp),
	) {
		Row(
			modifier = Modifier.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Start
		) {
			Column {
				Text(text = contact.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
				Spacer(modifier = Modifier.padding(4.dp))
				Text(text = contact.phoneNumber, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.End
			) {
				IconButton(onClick = { onDeleteClick(contact) }) {
					Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Contact")
				}
			}
		}
	}
}

fun apiContactToUiContact(apiContact: ApiContact): Contact {
	return Contact(name = apiContact.name, phoneNumber = apiContact.phoneNumber)
}

