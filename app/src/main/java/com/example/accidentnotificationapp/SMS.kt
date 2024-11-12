package com.example.accidentnotificationapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.service.autofill.Validators.or
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.accidentnotificationapp.data.Contact

private const val REQUEST_SMS_PERMISSION = 101

@Composable
fun SendSMS(context: Context, contacts:List<Contact>) {
	if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
		ActivityCompat.requestPermissions(
			(context as Activity),
			arrayOf(Manifest.permission.SEND_SMS),
			REQUEST_SMS_PERMISSION
		)
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) SendSMS(context, contacts)
		else return
	}
	Toast.makeText(context, "sms sending", Toast.LENGTH_SHORT).show()
	Toast.makeText(context, contacts.toString(), Toast.LENGTH_SHORT).show()
	for (contact in contacts) {
		Toast.makeText(context, "inside for loop", Toast.LENGTH_SHORT).show()
		var message = "Your family member got into an accident " +
				"Here's the location where the accident happened,\n" +
				" ${"https://maps.google.com/?q=${23.176520},${80.028069}"}"
		val smsManager = context.getSystemService(
			SmsManager::class.java
		)
		smsManager.sendTextMessage(contact.phoneNumber, null, message, null, null)
		Toast.makeText(context, "SMS sent to all numbers.", Toast.LENGTH_SHORT).show()
	}
}