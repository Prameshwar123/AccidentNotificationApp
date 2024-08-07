package com.example.accidentnotificationapp.screens.homescreen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import com.example.accidentnotificationapp.data.Contact

@Composable
fun Home(navController: NavController, value: Boolean){
	Surface(modifier = Modifier.fillMaxSize()) {
		//home content
		HomeContent(navController, value)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeContent(navController: NavController?=null, value: Boolean = false){
	var contacts by remember { mutableStateOf(listOf<Contact>()) }
	var showDialog by remember { mutableStateOf(false) }
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = "Accident Notification App",
						color = Color.Black,
						fontSize = 20.sp,
						fontWeight = FontWeight.W400
					)
				},
				actions = {
					Icon(
						imageVector = Icons.Filled.Notifications,
						contentDescription = "Notifications",
						tint = Color.Black,
						modifier = Modifier.padding(end = 6.dp)
					)
				},
				colors = TopAppBarDefaults.mediumTopAppBarColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer
				),
				
			)
	 	},
		floatingActionButton = {
			FloatingActionButton(
				onClick = { showDialog = true }
			) {
				Text("+")
			}
		},
		content = { padding ->
			Box(modifier = Modifier
				.fillMaxSize()
				.padding(padding), contentAlignment =  Alignment.Center) {
				if(contacts.isEmpty()){
					Text("No contacts available,\n\nPlease add a contact.", style = MaterialTheme.typography.titleMedium.copy(
						fontSize = 27.sp,
						fontWeight = FontWeight.Light
					))
				}
				else {
					LazyColumn {
						items(contacts.size) { index ->
							ContactCard(contact = contacts[index])
						}
					}
				}
				if (showDialog) {
					AddContactDialog(
						onDismiss = { showDialog = false },
						onAddContact = { newContact ->
							contacts = contacts + newContact
						}
					)
				}
			}
		}
	)
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
			color = MaterialTheme.colorScheme.surface
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
				Row(
					modifier = Modifier.align(Alignment.End)
				) {
					TextButton(onClick = onDismiss) {
						Text("Cancel")
					}
					Spacer(modifier = Modifier.width(8.dp))
					Button(onClick = {
						if (name.isNotBlank() && phoneNumber.isNotBlank() && phoneNumber.length == 10) {
							onAddContact(Contact(name, phoneNumber))
						}
						else{
							if(phoneNumber.isDigitsOnly()) {
								Toast.makeText(context, "Invalid Name", Toast.LENGTH_SHORT).show()
							}
							else {
								Toast.makeText(context, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
							}
						}
						onDismiss()
					}) {
						Text("Add")
					}
				}
			}
		}
	}
}


@Preview
@Composable
fun ContactCard(contact: Contact = Contact(name = "John Doe", phoneNumber = "123-456-7890")) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(8.dp)
	) {
		Column(modifier = Modifier.padding(16.dp)){
			Text(text = contact.name, style = MaterialTheme.typography.titleMedium)
			Spacer(modifier = Modifier.padding(4.dp))
			Text(text = contact.phoneNumber, style = MaterialTheme.typography.bodyMedium)
		}
	}
}

