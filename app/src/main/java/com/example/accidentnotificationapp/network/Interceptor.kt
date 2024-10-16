package com.example.accidentnotificationapp.network

import android.content.Context
import com.example.accidentnotificationapp.data.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor(private val context: Context) : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val original = chain.request()
		val url = original.url.toString()
		if (url.contains("contacts")) {
			val userPreferences = UserPreferences(context)
			var cookie: String? = null
			
			runBlocking {
				cookie = userPreferences.cookie.first()
			}
			
			if (!cookie.isNullOrEmpty()) {
				val requestBuilder = original.newBuilder()
					.addHeader("Cookie", cookie!!)
				val request = requestBuilder.build()
				return chain.proceed(request)
			}
		}
		return chain.proceed(original)
	}
}