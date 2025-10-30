package `in`.bewithdhanu.medicinetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import `in`.bewithdhanu.medicinetracker.data.remote.RetrofitClient
import `in`.bewithdhanu.medicinetracker.data.repository.UserRepository
import `in`.bewithdhanu.medicinetracker.ui.screens.UsersScreen
import `in`.bewithdhanu.medicinetracker.ui.theme.MedicineTrackerTheme
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize repository and ViewModel
        val userRepository = UserRepository(RetrofitClient.apiService)
        val userViewModel = UserViewModel(userRepository)
        
        setContent {
            MedicineTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // For now, directly show UsersScreen
                    // TODO: Add proper navigation
                    UsersScreen(
                        viewModel = userViewModel,
                        onNavigateBack = { /* Handle navigation */ }
                    )
                }
            }
        }
    }
}