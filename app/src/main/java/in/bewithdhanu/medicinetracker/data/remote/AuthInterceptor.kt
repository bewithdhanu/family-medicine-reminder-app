package `in`.bewithdhanu.medicinetracker.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add API Key to all requests
 * API Key is loaded from BuildConfig (local.properties)
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Add API Key header to all requests
        val newRequest = originalRequest.newBuilder()
            .addHeader("X-API-Key", ApiConfig.API_KEY)
            .addHeader("Content-Type", "application/json")
            .build()
        
        return chain.proceed(newRequest)
    }
}

