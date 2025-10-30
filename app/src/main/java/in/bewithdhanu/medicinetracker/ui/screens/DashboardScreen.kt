package `in`.bewithdhanu.medicinetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.bewithdhanu.medicinetracker.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dashboard/Home Screen
 * Shows current date, time, and navigation to other features
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToUsers: () -> Unit,
    onNavigateToMedicines: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToInsulinTracking: () -> Unit
) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    
    // Update time every minute
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60000) // Update every minute
            currentTime = getCurrentTime()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting and Time Card
            item {
                DateTimeCard(currentTime)
            }
            
            // Quick Actions
            item {
                Text(
                    text = "Quick Actions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            // Users Card
            item {
                DashboardCard(
                    title = stringResource(R.string.nav_users),
                    icon = Icons.Default.Person,
                    onClick = onNavigateToUsers
                )
            }
            
            // Medicines Card
            item {
                DashboardCard(
                    title = stringResource(R.string.nav_medicines),
                    icon = Icons.Default.Medication,
                    onClick = onNavigateToMedicines
                )
            }
            
            // Reminders Card
            item {
                DashboardCard(
                    title = stringResource(R.string.nav_reminders),
                    icon = Icons.Default.Notifications,
                    onClick = onNavigateToReminders
                )
            }
            
            // Insulin Tracking Card
            item {
                DashboardCard(
                    title = "Insulin Tracking",
                    icon = Icons.Default.Healing,
                    onClick = onNavigateToInsulinTracking
                )
            }
        }
    }
}

@Composable
fun DateTimeCard(currentTime: TimeInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Greeting
            Text(
                text = currentTime.greeting,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Time
            Text(
                text = currentTime.time,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Date
            Text(
                text = currentTime.date,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

data class TimeInfo(
    val greeting: String,
    val time: String,
    val date: String
)

fun getCurrentTime(): TimeInfo {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    
    val greeting = when (hour) {
        in 5..11 -> "Good Morning"  // stringResource can't be used here
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    
    return TimeInfo(
        greeting = greeting,
        time = timeFormat.format(calendar.time),
        date = dateFormat.format(calendar.time)
    )
}

