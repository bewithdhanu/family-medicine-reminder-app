package `in`.bewithdhanu.medicinetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `in`.bewithdhanu.medicinetracker.data.remote.RetrofitClient
import `in`.bewithdhanu.medicinetracker.data.repository.MedicineRepository
import `in`.bewithdhanu.medicinetracker.data.repository.ReminderRepository
import `in`.bewithdhanu.medicinetracker.data.repository.UserRepository
import `in`.bewithdhanu.medicinetracker.ui.navigation.Screen
import `in`.bewithdhanu.medicinetracker.ui.screens.DashboardScreen
import `in`.bewithdhanu.medicinetracker.ui.screens.MedicinesScreen
import `in`.bewithdhanu.medicinetracker.ui.screens.RemindersScreen
import `in`.bewithdhanu.medicinetracker.ui.screens.UsersScreen
import `in`.bewithdhanu.medicinetracker.ui.theme.MedicineTrackerTheme
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.MedicineViewModel
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.ReminderViewModel
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize repositories and ViewModels
        val apiService = RetrofitClient.apiService
        val userRepository = UserRepository(apiService)
        val medicineRepository = MedicineRepository(apiService)
        val reminderRepository = ReminderRepository(apiService)
        
        val userViewModel = UserViewModel(userRepository)
        val medicineViewModel = MedicineViewModel(medicineRepository)
        val reminderViewModel = ReminderViewModel(reminderRepository)
        
        setContent {
            MedicineTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route
                    ) {
                        // Dashboard
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                onNavigateToUsers = {
                                    navController.navigate(Screen.Users.route)
                                },
                                onNavigateToMedicines = {
                                    navController.navigate(Screen.Medicines.route)
                                },
                                onNavigateToReminders = {
                                    navController.navigate(Screen.Reminders.route)
                                }
                            )
                        }
                        
                        // Users
                        composable(Screen.Users.route) {
                            UsersScreen(
                                viewModel = userViewModel,
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }
                        
                        // Medicines
                        composable(Screen.Medicines.route) {
                            MedicinesScreen(
                                medicineViewModel = medicineViewModel,
                                userViewModel = userViewModel,
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }
                        
                        // Reminders
                        composable(Screen.Reminders.route) {
                            RemindersScreen(
                                reminderViewModel = reminderViewModel,
                                medicineViewModel = medicineViewModel,
                                userViewModel = userViewModel,
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}