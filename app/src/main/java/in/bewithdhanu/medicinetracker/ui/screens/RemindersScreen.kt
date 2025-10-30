package `in`.bewithdhanu.medicinetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import `in`.bewithdhanu.medicinetracker.data.model.Medicine
import `in`.bewithdhanu.medicinetracker.data.model.MedicineType
import `in`.bewithdhanu.medicinetracker.data.model.Reminder
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.MedicineViewModel
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.ReminderUiState
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.ReminderViewModel
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Reminders Screen - Manage medicine reminder schedules
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    reminderViewModel: ReminderViewModel,
    medicineViewModel: MedicineViewModel,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by reminderViewModel.uiState.collectAsState()
    val reminders by reminderViewModel.reminders.collectAsState()
    val medicines by medicineViewModel.medicines.collectAsState()
    val users by userViewModel.users.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Reminder?>(null) }
    
    // Load data if not already loaded
    LaunchedEffect(Unit) {
        if (users.isEmpty()) userViewModel.loadUsers()
        if (medicines.isEmpty()) medicineViewModel.loadMedicines()
        reminderViewModel.loadReminders()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.reminders_title),
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
                    contentDescription = stringResource(R.string.add_reminder),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is ReminderUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(64.dp),
                        strokeWidth = 6.dp
                    )
                }
                is ReminderUiState.Error -> {
                    ErrorView(
                        message = (uiState as ReminderUiState.Error).message,
                        onRetry = { reminderViewModel.loadReminders() }
                    )
                }
                is ReminderUiState.Success -> {
                    if (reminders.isEmpty()) {
                        EmptyRemindersView()
                    } else {
                        RemindersList(
                            reminders = reminders,
                            medicines = medicines,
                            onDeleteClick = { showDeleteDialog = it }
                        )
                    }
                }
            }
        }
    }
    
    // Add Reminder Dialog
    if (showAddDialog && medicines.isNotEmpty()) {
        AddReminderDialog(
            medicines = medicines.filter { it.isActive },
            onDismiss = { showAddDialog = false },
            onConfirm = { medicineId, time ->
                reminderViewModel.createReminder(medicineId, time)
                showAddDialog = false
            }
        )
    }
    
    // Delete Confirmation Dialog
    showDeleteDialog?.let { reminder ->
        DeleteReminderDialog(
            reminder = reminder,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                reminderViewModel.deleteReminder(reminder.id)
                showDeleteDialog = null
            }
        )
    }
}

@Composable
fun RemindersList(
    reminders: List<Reminder>,
    medicines: List<Medicine>,
    onDeleteClick: (Reminder) -> Unit
) {
    // Group reminders by medicine
    val groupedReminders = reminders.groupBy { it.medicineId }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        groupedReminders.forEach { (medicineId, medicineReminders) ->
            val medicine = medicines.find { it.id == medicineId }
            
            if (medicine != null) {
                item {
                    Text(
                        text = medicine.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(medicineReminders.sortedBy { it.scheduledTime }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        medicine = medicine,
                        onDeleteClick = { onDeleteClick(reminder) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    medicine: Medicine,
    onDeleteClick: () -> Unit
) {
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
            // Icon based on medicine type
            Surface(
                modifier = Modifier.size(64.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = when (medicine.type) {
                        MedicineType.TABLET -> Icons.Default.Medication
                        MedicineType.INJECTION -> Icons.Default.LocalHospital
                        MedicineType.INSULIN -> Icons.Default.Healing
                    },
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Time and Status
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatTime(reminder.scheduledTime),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (reminder.isActive) 
                            Icons.Default.Notifications 
                        else 
                            Icons.Default.NotificationsOff,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (reminder.isActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (reminder.isActive) "Active" else "Inactive",
                        fontSize = 18.sp,
                        color = if (reminder.isActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            // Delete Button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EmptyRemindersView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_reminders),
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    medicines: List<Medicine>,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var selectedMedicine by remember { mutableStateOf(medicines.firstOrNull()) }
    var showMedicineDropdown by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf(8) }
    var selectedMinute by remember { mutableStateOf(0) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.add_reminder),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Medicine Selector
                ExposedDropdownMenuBox(
                    expanded = showMedicineDropdown,
                    onExpandedChange = { showMedicineDropdown = !showMedicineDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedMedicine?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.medicine_name), fontSize = 18.sp) },
                        trailingIcon = { 
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showMedicineDropdown) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                    )
                    ExposedDropdownMenu(
                        expanded = showMedicineDropdown,
                        onDismissRequest = { showMedicineDropdown = false }
                    ) {
                        medicines.forEach { medicine ->
                            DropdownMenuItem(
                                text = { Text(medicine.name, fontSize = 20.sp) },
                                onClick = {
                                    selectedMedicine = medicine
                                    showMedicineDropdown = false
                                }
                            )
                        }
                    }
                }
                
                // Time Picker
                Text(
                    text = stringResource(R.string.reminder_time),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Hour Picker
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { selectedHour = (selectedHour + 1) % 24 }) {
                            Icon(Icons.Default.KeyboardArrowUp, null, Modifier.size(32.dp))
                        }
                        Text(
                            text = String.format("%02d", selectedHour),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { 
                            selectedHour = if (selectedHour == 0) 23 else selectedHour - 1 
                        }) {
                            Icon(Icons.Default.KeyboardArrowDown, null, Modifier.size(32.dp))
                        }
                    }
                    
                    Text(":", fontSize = 36.sp, modifier = Modifier.padding(top = 40.dp))
                    
                    // Minute Picker
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { selectedMinute = (selectedMinute + 5) % 60 }) {
                            Icon(Icons.Default.KeyboardArrowUp, null, Modifier.size(32.dp))
                        }
                        Text(
                            text = String.format("%02d", selectedMinute),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { 
                            selectedMinute = if (selectedMinute == 0) 55 else selectedMinute - 5 
                        }) {
                            Icon(Icons.Default.KeyboardArrowDown, null, Modifier.size(32.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (selectedMedicine != null) {
                        val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                        onConfirm(selectedMedicine!!.id, time)
                    }
                },
                modifier = Modifier.height(56.dp)
            ) {
                Text(stringResource(R.string.save), fontSize = 20.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(56.dp)
            ) {
                Text(stringResource(R.string.cancel), fontSize = 20.sp)
            }
        }
    )
}

@Composable
fun DeleteReminderDialog(
    reminder: Reminder,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = stringResource(R.string.delete),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Delete reminder at ${formatTime(reminder.scheduledTime)}?",
                fontSize = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.height(56.dp)
            ) {
                Text(stringResource(R.string.delete), fontSize = 20.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(56.dp)
            ) {
                Text(stringResource(R.string.cancel), fontSize = 20.sp)
            }
        }
    )
}

fun formatTime(time: String): String {
    return try {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(time)
        if (date != null) outputFormat.format(date) else time
    } catch (e: Exception) {
        time
    }
}

