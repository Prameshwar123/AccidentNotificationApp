package com.example.accidentnotificationapp.data

data class Contact(val name: String, val phoneNumber: String)
data class User(var name: String, var email: String, var password: String)
data class LoginRequest(var email: String, var password: String)
data class SignupRequest(var name: String, var email: String, var password: String)
data class ApiResponse(var success: Boolean, var message: String?, var user: User?)
data class ContactRequest(val name: String, val phoneNumber: String)
data class ContactResponse(var success: Boolean, var message: String?, var contact: Contact?)
data class ContactsResponse(var success: Boolean, var contacts: List<ApiContact>?)
data class ApiContact(val _id: String?, val name: String, val phoneNumber: String)
