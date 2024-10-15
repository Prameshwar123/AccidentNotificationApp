package com.example.accidentnotificationapp.network

import com.example.accidentnotificationapp.data.Contact
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class User(var name: String, var email: String, var password: String)
data class LoginRequest(var email: String, var password: String)
data class SignupRequest(var name: String, var email: String, var password: String)
data class ApiResponse(var success: Boolean, var message: String?, var user: User?)
data class ContactRequest(val name: String, val phoneNumber: String)
data class ContactResponse(var success: Boolean, var message: String?, var contact: Contact?)
data class ContactsResponse(var success: Boolean, var contacts: List<Contact>?)
data class ApiContact(val _id: String?, val name: String, val phoneNumber: String)

private const val BASE_URL = "https://accidentnotificationappbackend.onrender.com/"

interface RetrofitAPI {

	@POST("login")
	fun loginUser(@Body request: LoginRequest): Call<ApiResponse>

	@POST("register")
	fun registerUser(@Body request: SignupRequest): Call<ApiResponse>
	
	@POST("contacts")
	fun addContact(@Body request: ContactRequest): Call<ContactResponse>
	
	@GET("contacts")
	fun getContacts(): Call<ContactsResponse>
	
	@POST("logout")
	fun logout(): Call<ApiResponse>
}

fun performSignup(
	name: String,
	email: String,
	password: String,
	onResult: (Boolean, String?) -> Unit
) {
	val request = SignupRequest(name, email, password)
	val retrofit = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.build()
	val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
	val call: Call<ApiResponse> = retrofitAPI.registerUser(request)
	call.enqueue(object : Callback<ApiResponse> {
		override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
			val apiResponse: ApiResponse? = response.body()
			if (apiResponse != null && apiResponse.success) {
				onResult(true, apiResponse.message)
			} else {
				onResult(false, apiResponse?.message ?: "Registration failed")
			}
		}
		
		override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
			onResult(false, t.message)
		}
	})
}

fun performLogin(
	email: String,
	password: String,
	onResult: (Boolean, String?) -> Unit
) {
	val request = LoginRequest(email, password)
	val retrofit = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.build()
	val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
	val call: Call<ApiResponse> = retrofitAPI.loginUser(request)
	call.enqueue(object : Callback<ApiResponse> {
		override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
			val apiResponse: ApiResponse? = response.body()
			if (apiResponse != null && apiResponse.success) {
				onResult(true, apiResponse.message)
			} else {
				onResult(false, apiResponse?.message ?: "Login failed")
			}
		}
		
		override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
			onResult(false, t.message)
		}
	})
}

fun addContact(
	name: String,
	phoneNumber: String,
	onResult: (Boolean, String?) -> Unit
) {
	val request = ContactRequest(name, phoneNumber)
	val retrofit = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.build()
	
	val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
	val call: Call<ContactResponse> = retrofitAPI.addContact(request)
	call.enqueue(object : Callback<ContactResponse> {
		override fun onResponse(call: Call<ContactResponse>, response: Response<ContactResponse>) {
			val apiResponse: ContactResponse? = response.body()
			if (apiResponse != null && apiResponse.success) {
				onResult(true, apiResponse.message)
			} else {
				onResult(false, apiResponse?.message ?: "Failed to add contact")
			}
		}
		
		override fun onFailure(call: Call<ContactResponse>, t: Throwable) {
			onResult(false, t.message)
		}
	})
}

fun getContacts(
	onResult: (Boolean, List<Contact>?) -> Unit
) {
	val retrofit = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.build()
	
	val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
	val call: Call<ContactsResponse> = retrofitAPI.getContacts()
	call.enqueue(object : Callback<ContactsResponse> {
		override fun onResponse(call: Call<ContactsResponse>, response: Response<ContactsResponse>) {
			val contactsResponse: ContactsResponse? = response.body()
			if (contactsResponse != null && contactsResponse.success) {
				onResult(true, contactsResponse.contacts)
			} else {
				onResult(false, null)
			}
		}
		override fun onFailure(call: Call<ContactsResponse>, t: Throwable) {
			onResult(false, null)
		}
	})
}

fun performLogout(onResult: (Boolean, String?) -> Unit) {
	val retrofit = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.build()
	
	val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
	val call: Call<ApiResponse> = retrofitAPI.logout()
	call.enqueue(object : Callback<ApiResponse> {
		override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
			val apiResponse: ApiResponse? = response.body()
			if (apiResponse != null && apiResponse.success) {
				onResult(true, apiResponse.message)
			} else {
				onResult(false, apiResponse?.message ?: "Logout failed")
			}
		}
		
		override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
			onResult(false, t.message)
		}
	})
}


