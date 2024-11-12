package com.example.accidentnotificationapp.network

import android.content.Context
import com.example.accidentnotificationapp.data.ApiContact
import com.example.accidentnotificationapp.data.ApiResponse
import com.example.accidentnotificationapp.data.ContactRequest
import com.example.accidentnotificationapp.data.ContactResponse
import com.example.accidentnotificationapp.data.ContactsResponse
import com.example.accidentnotificationapp.data.LoginRequest
import com.example.accidentnotificationapp.data.SignupRequest
import com.example.accidentnotificationapp.data.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private const val BASE_URL = "https://accidentnotificationappbackend.onrender.com/"

object RetrofitInstance {
	
	fun getRetrofit(context: Context): Retrofit {
		val okHttpClient = OkHttpClient.Builder()
			.addInterceptor(CookieInterceptor(context))
			.build()
		
		return Retrofit.Builder()
			.baseUrl(BASE_URL)
			.client(okHttpClient)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}
}


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
	
	@DELETE("contacts/{id}")
	fun deleteContact(@Path("id") id: String): Call<ContactResponse>
}

fun performSignup(
	name: String,
	email: String,
	password: String,
	context: Context,
	onResult: (Boolean, String?) -> Unit
) {
	val request = SignupRequest(name, email, password)
	val retrofit = RetrofitInstance.getRetrofit(context)
	val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
	val call: Call<ApiResponse> = retrofitAPI.registerUser(request)
	
	call.enqueue(object : Callback<ApiResponse> {
		override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
			val apiResponse: ApiResponse? = response.body()
			if (apiResponse != null && apiResponse.success) {
				onResult(true, apiResponse.message)
				val cookie = response.headers()["Set-Cookie"]
				if (cookie != null) {
					val userPreferences = UserPreferences(context)
					runBlocking {
						userPreferences.setLoginState(true, email, cookie)
					}
				}
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
	context: Context,
	onResult: (Boolean, String?) -> Unit
) {
	val request = LoginRequest(email, password)
	val retrofit = RetrofitInstance.getRetrofit(context)
	val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
	val call: Call<ApiResponse> = retrofitAPI.loginUser(request)
	
	call.enqueue(object : Callback<ApiResponse> {
		override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
			val apiResponse: ApiResponse? = response.body()
			if (apiResponse != null && apiResponse.success) {
				onResult(true, apiResponse.message)
				val cookie = response.headers()["Set-Cookie"]
				if (cookie != null) {
					val userPreferences = UserPreferences(context)
					runBlocking {
						userPreferences.setLoginState(true, email, cookie)
					}
				}
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
	context: Context,
	onResult: (Boolean, String?) -> Unit
) {
	val request = ContactRequest(name, phoneNumber)
	val retrofit = RetrofitInstance.getRetrofit(context)
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
	context: Context,
	onResult: (Boolean, List<ApiContact>?) -> Unit
) {
	val retrofit = RetrofitInstance.getRetrofit(context)
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

fun deleteContact(
	contactId: String,
	context: Context,
	onResult: (Boolean, String?) -> Unit
) {
	val retrofit = RetrofitInstance.getRetrofit(context)
	val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
	val call: Call<ContactResponse> = retrofitAPI.deleteContact(contactId)
	
	call.enqueue(object : Callback<ContactResponse> {
		override fun onResponse(call: Call<ContactResponse>, response: Response<ContactResponse>) {
			val apiResponse: ContactResponse? = response.body()
			if (apiResponse != null && apiResponse.success) {
				onResult(true, apiResponse.message)
			} else {
				onResult(false, apiResponse?.message ?: "Failed to delete contact")
			}
		}
		
		override fun onFailure(call: Call<ContactResponse>, t: Throwable) {
			onResult(false, t.message)
		}
	})
}


fun performLogout(context: Context, onResult: (Boolean, String?) -> Unit) {
	val retrofit = RetrofitInstance.getRetrofit(context)
	val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
	val call: Call<ApiResponse> = retrofitAPI.logout()
	
	call.enqueue(object : Callback<ApiResponse> {
		override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
			val apiResponse: ApiResponse? = response.body()
			if (apiResponse != null && apiResponse.success) {
				val userPreferences = UserPreferences(context)
				runBlocking {
					userPreferences.logout()
				}
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

