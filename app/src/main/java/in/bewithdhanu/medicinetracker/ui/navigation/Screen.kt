package `in`.bewithdhanu.medicinetracker.ui.navigation

/**
 * Navigation destinations
 */
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Users : Screen("users")
    object Medicines : Screen("medicines")
    object Reminders : Screen("reminders")
    object Settings : Screen("settings")
}

