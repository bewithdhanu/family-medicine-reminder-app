package `in`.bewithdhanu.medicinetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.bewithdhanu.medicinetracker.R
import `in`.bewithdhanu.medicinetracker.data.model.InsulinLog
import `in`.bewithdhanu.medicinetracker.data.model.InsulinStats
import `in`.bewithdhanu.medicinetracker.data.model.User
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.InsulinUiState
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.InsulinViewModel
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Insulin Tracking Screen - Track glucose readings and insulin dosage
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsulinTrackingScreen(
    insulinViewModel: InsulinViewModel,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by insulinViewModel.uiState.collectAsState()
    val insulinLogs by insulinViewModel.insulinLogs.collectAsState()
    val stats by insulinViewModel.stats.collectAsState()
    val users by userViewModel.users.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedPeriod by remember { mutableStateOf("weekly") }
    
    // Load data
    LaunchedEffect(Unit) {
        if (users.isEmpty()) userViewModel.loadUsers()
        insulinViewModel.loadInsulinLogs()
    }
    
    // Load stats when user changes
    LaunchedEffect(selectedUser, selectedPeriod) {
        selectedUser?.let { user ->
            insulinViewModel.loadStats(user.id, selectedPeriod)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.insulin_tracking_title),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = stringResource(R.string.back),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showStatsDialog = true }) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = "Statistics",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Add Reading",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // User Filter
            if (users.isNotEmpty()) {
                UserFilterChipsForInsulin(
                    users = users,
                    selectedUser = selectedUser,
                    onUserSelected = { user ->
                        selectedUser = user
                        insulinViewModel.loadInsulinLogs(user?.id)
                    }
                )
            }
            
            // Stats Summary Card
            selectedUser?.let { user ->
                stats?.let { statsData ->
                    StatsCard(stats = statsData)
                }
            }
            
            // Insulin Logs
            Box(modifier = Modifier.weight(1f)) {
                when (uiState) {
                    is InsulinUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(64.dp),
                            strokeWidth = 6.dp
                        )
                    }
                    is InsulinUiState.Error -> {
                        ErrorView(
                            message = (uiState as InsulinUiState.Error).message,
                            onRetry = { insulinViewModel.loadInsulinLogs(selectedUser?.id) }
                        )
                    }
                    is InsulinUiState.Success -> {
                        if (insulinLogs.isEmpty()) {
                            EmptyInsulinView()
                        } else {
                            InsulinLogsList(insulinLogs = insulinLogs)
                        }
                    }
                }
            }
        }
    }
    
    // Add Insulin Log Dialog
    if (showAddDialog && users.isNotEmpty()) {
        AddInsulinDialog(
            users = users,
            preselectedUser = selectedUser,
            insulinViewModel = insulinViewModel,
            onDismiss = { showAddDialog = false },
            onConfirm = { userId, glucose, insulin, notes ->
                insulinViewModel.createInsulinLog(userId, glucose, insulin, notes)
                showAddDialog = false
            }
        )
    }
    
    // Stats Dialog
    if (showStatsDialog && selectedUser != null && stats != null) {
        StatsDialog(
            stats = stats!!,
            user = selectedUser!!,
            selectedPeriod = selectedPeriod,
            onPeriodChange = { selectedPeriod = it },
            onDismiss = { showStatsDialog = false }
        )
    }
}

@Composable
fun UserFilterChipsForInsulin(
    users: List<User>,
    selectedUser: User?,
    onUserSelected: (User?) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedUser == null,
                onClick = { onUserSelected(null) },
                label = { 
                    Text(
                        "All", 
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) 
                },
                modifier = Modifier.height(48.dp)
            )
        }
        
        items(users) { user ->
            FilterChip(
                selected = selectedUser?.id == user.id,
                onClick = { onUserSelected(user) },
                label = { 
                    Text(
                        user.name, 
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) 
                },
                modifier = Modifier.height(48.dp)
            )
        }
    }
}

@Composable
fun StatsCard(stats: InsulinStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "${stats.period.capitalize()} Summary",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Avg Glucose",
                    value = String.format("%.1f mg/dL", stats.avgGlucose)
                )
                StatItem(
                    label = "Avg Insulin",
                    value = String.format("%.1f units", stats.avgInsulin)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Min Glucose",
                    value = String.format("%.0f", stats.minGlucose)
                )
                StatItem(
                    label = "Max Glucose",
                    value = String.format("%.0f", stats.maxGlucose)
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun InsulinLogsList(insulinLogs: List<InsulinLog>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(insulinLogs.sortedByDescending { it.recordedAt }) { log ->
            InsulinLogCard(log = log)
        }
    }
}

@Composable
fun InsulinLogCard(log: InsulinLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Glucose Icon
            Surface(
                modifier = Modifier.size(64.dp),
                shape = MaterialTheme.shapes.medium,
                color = getGlucoseColor(log.glucoseReading)
            ) {
                Icon(
                    imageVector = Icons.Default.Healing,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Readings
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = String.format("%.0f mg/dL", log.glucoseReading),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = String.format("Insulin: %.1f units", log.insulinDosage),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = formatDateTime(log.recordedAt),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                
                log.notes?.let { notes ->
                    if (notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = notes,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyInsulinView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Healing,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No insulin logs yet",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInsulinDialog(
    users: List<User>,
    preselectedUser: User?,
    insulinViewModel: InsulinViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Int, Float, Float, String?) -> Unit
) {
    var selectedUser by remember { mutableStateOf(preselectedUser ?: users.firstOrNull()) }
    var showUserDropdown by remember { mutableStateOf(false) }
    var glucoseReading by remember { mutableStateOf("") }
    var insulinDosage by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    val dosageSuggestion by insulinViewModel.dosageSuggestion.collectAsState()
    
    // Get suggestion when glucose changes
    LaunchedEffect(glucoseReading) {
        val glucose = glucoseReading.toFloatOrNull()
        if (glucose != null && glucose > 0) {
            insulinViewModel.getSuggestedDosage(glucose)
        }
    }
    
    AlertDialog(
        onDismissRequest = {
            insulinViewModel.clearDosageSuggestion()
            onDismiss()
        },
        title = {
            Text(
                text = "Add Insulin Reading",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // User Selector
                ExposedDropdownMenuBox(
                    expanded = showUserDropdown,
                    onExpandedChange = { showUserDropdown = !showUserDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedUser?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.user_name), fontSize = 18.sp) },
                        trailingIcon = { 
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUserDropdown) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                    )
                    ExposedDropdownMenu(
                        expanded = showUserDropdown,
                        onDismissRequest = { showUserDropdown = false }
                    ) {
                        users.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(user.name, fontSize = 20.sp) },
                                onClick = {
                                    selectedUser = user
                                    showUserDropdown = false
                                }
                            )
                        }
                    }
                }
                
                // Glucose Reading
                OutlinedTextField(
                    value = glucoseReading,
                    onValueChange = { glucoseReading = it },
                    label = { Text("Glucose Reading (mg/dL)", fontSize = 18.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                    supportingText = {
                        dosageSuggestion?.let { suggestion ->
                            Text(
                                text = suggestion.note,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                
                // Insulin Dosage
                OutlinedTextField(
                    value = insulinDosage,
                    onValueChange = { insulinDosage = it },
                    label = { Text("Insulin Dosage (units)", fontSize = 18.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                    supportingText = {
                        dosageSuggestion?.let { suggestion ->
                            Text(
                                text = "Suggested: ${suggestion.suggestedDosage} ${suggestion.unit}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                )
                
                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val glucose = glucoseReading.toFloatOrNull()
                    val insulin = insulinDosage.toFloatOrNull()
                    
                    if (selectedUser != null && glucose != null && insulin != null) {
                        onConfirm(
                            selectedUser!!.id,
                            glucose,
                            insulin,
                            notes.ifBlank { null }
                        )
                        insulinViewModel.clearDosageSuggestion()
                    }
                },
                modifier = Modifier.height(56.dp)
            ) {
                Text(stringResource(R.string.save), fontSize = 20.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    insulinViewModel.clearDosageSuggestion()
                    onDismiss()
                },
                modifier = Modifier.height(56.dp)
            ) {
                Text(stringResource(R.string.cancel), fontSize = 20.sp)
            }
        }
    )
}

@Composable
fun StatsDialog(
    stats: InsulinStats,
    user: User,
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${user.name}'s Stats",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Period Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("daily", "weekly", "monthly").forEach { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { onPeriodChange(period) },
                            label = { Text(period.capitalize(), fontSize = 18.sp) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StatsCard(stats = stats)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(56.dp)
            ) {
                Text("Close", fontSize = 20.sp)
            }
        }
    )
}

@Composable
fun getGlucoseColor(glucose: Float): androidx.compose.ui.graphics.Color {
    return when {
        glucose < 70 -> MaterialTheme.colorScheme.error
        glucose > 180 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
}

fun formatDateTime(dateTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        if (date != null) outputFormat.format(date) else dateTime
    } catch (e: Exception) {
        dateTime
    }
}

