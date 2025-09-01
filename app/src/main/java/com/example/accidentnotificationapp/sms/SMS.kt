package com.example.accidentnotificationapp.sms

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.example.accidentnotificationapp.data.Contact

fun sendSMS(context: Context, contacts: List<Contact>, address: String) {
	val smsManager = context.getSystemService(SmsManager::class.java)
	val finalMessage: String = "Your family member got into an accident, " + address.ifEmpty {
		 "Unable to get location."
	}
	try {
		for (contact in contacts) {
			smsManager.sendTextMessage(contact.phoneNumber, null, finalMessage, null, null)
		}
		Toast.makeText(context, "SMS sent to all numbers.", Toast.LENGTH_SHORT).show()
	} catch (e: Exception) {
		Log.d("current sms", finalMessage)
		Log.e("SMS_ERROR", "Failed to send SMS: ${e.message}")
		Toast.makeText(context, "Failed to send SMS.", Toast.LENGTH_SHORT).show()
	}
}
