package `in`.bewithdhanu.medicinetracker.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

/**
 * Helper class for phone and WhatsApp communication
 */
object CommunicationHelper {
    
    /**
     * Make a phone call
     * @param phoneNumber Phone number with country code (e.g., +919876543210)
     */
    fun makePhoneCall(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to make call: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Open WhatsApp chat with a specific number
     * @param phoneNumber Phone number with country code WITHOUT + (e.g., 919876543210)
     */
    fun openWhatsAppChat(context: Context, phoneNumber: String) {
        try {
            // Remove + and spaces
            val cleanNumber = phoneNumber.replace("+", "").replace(" ", "")
            
            // Try to open WhatsApp directly
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$cleanNumber")
                setPackage("com.whatsapp")
            }
            
            // Check if WhatsApp is installed
            if (isWhatsAppInstalled(context)) {
                context.startActivity(intent)
            } else {
                // Open in browser if WhatsApp not installed
                val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$cleanNumber")
                }
                context.startActivity(browserIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Check if WhatsApp is installed
     */
    private fun isWhatsAppInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    /**
     * Send SMS (if needed in future)
     */
    fun sendSMS(context: Context, phoneNumber: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", message)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to send SMS: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

