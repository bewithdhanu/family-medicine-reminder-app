package `in`.bewithdhanu.medicinetracker.data.remote

import `in`.bewithdhanu.medicinetracker.BuildConfig

/**
 * API Configuration loaded from BuildConfig
 * Values come from local.properties (gitignored)
 * 
 * NEVER hardcode API_BASE_URL or API_KEY here!
 */
object ApiConfig {
    /**
     * Base URL for API - loaded from local.properties
     * Example: https://your-api-domain.com/
     */
    val BASE_URL: String
        get() {
            require(BuildConfig.API_BASE_URL.isNotEmpty()) {
                "API_BASE_URL not configured! Please set it in local.properties"
            }
            return BuildConfig.API_BASE_URL
        }
    
    /**
     * API Key for authentication - loaded from local.properties
     * This should be stored securely and never committed to git
     */
    val API_KEY: String
        get() {
            require(BuildConfig.API_KEY.isNotEmpty()) {
                "API_KEY not configured! Please set it in local.properties"
            }
            return BuildConfig.API_KEY
        }
    
    /**
     * Check if API is configured properly
     */
    fun isConfigured(): Boolean {
        return BuildConfig.API_BASE_URL.isNotEmpty() && 
               BuildConfig.API_KEY.isNotEmpty()
    }
    
    /**
     * Get configuration status message
     */
    fun getConfigStatus(): String {
        return when {
            BuildConfig.API_BASE_URL.isEmpty() && BuildConfig.API_KEY.isEmpty() ->
                "API not configured. Please set API_BASE_URL and API_KEY in local.properties"
            BuildConfig.API_BASE_URL.isEmpty() ->
                "API_BASE_URL not set in local.properties"
            BuildConfig.API_KEY.isEmpty() ->
                "API_KEY not set in local.properties"
            else ->
                "API configured successfully"
        }
    }
}

