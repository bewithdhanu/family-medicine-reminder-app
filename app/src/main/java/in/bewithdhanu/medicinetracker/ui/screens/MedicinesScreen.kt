package `in`.bewithdhanu.medicinetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import `in`.bewithdhanu.medicinetracker.R
import `in`.bewithdhanu.medicinetracker.data.model.Medicine
import `in`.bewithdhanu.medicinetracker.data.model.MedicineType
import `in`.bewithdhanu.medicinetracker.data.model.User
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.MedicineUiState
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.MedicineViewModel
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.UserViewModel

/**
 * Medicines Screen - Display and manage medicines
 * Supports Tablet, Injection, and Insulin types
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicinesScreen(
    medicineViewModel: MedicineViewModel,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by medicineViewModel.uiState.collectAsState()
    val medicines by medicineViewModel.medicines.collectAsState()
    val users by userViewModel.users.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Medicine?>(null) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    
    // Load users if not already loaded
    LaunchedEffect(Unit) {
        if (users.isEmpty()) {
            userViewModel.loadUsers()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.medicines_title),
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
                    contentDescription = stringResource(R.string.add_medicine),
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
            // User Filter Chips
            if (users.isNotEmpty()) {
                UserFilterChips(
                    users = users,
                    selectedUser = selectedUser,
                    onUserSelected = { user ->
                        selectedUser = user
                        medicineViewModel.loadMedicines(user?.id)
                    }
                )
            }
            
            // Medicines List
            Box(modifier = Modifier.weight(1f)) {
                when (uiState) {
                    is MedicineUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(64.dp),
                            strokeWidth = 6.dp
                        )
                    }
                    is MedicineUiState.Error -> {
                        ErrorView(
                            message = (uiState as MedicineUiState.Error).message,
                            onRetry = { medicineViewModel.loadMedicines(selectedUser?.id) }
                        )
                    }
                    is MedicineUiState.Success -> {
                        if (medicines.isEmpty()) {
                            EmptyMedicinesView()
                        } else {
                            MedicinesList(
                                medicines = medicines,
                                onDeleteClick = { showDeleteDialog = it },
                                onToggleStatus = { medicine ->
                                    medicineViewModel.toggleMedicineStatus(
                                        medicine.id,
                                        medicine.isActive
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Add Medicine Dialog
    if (showAddDialog && users.isNotEmpty()) {
        AddMedicineDialog(
            users = users,
            preselectedUser = selectedUser,
            onDismiss = { showAddDialog = false },
            onConfirm = { userId, name, type, dosage, instructions ->
                medicineViewModel.createMedicine(
                    userId = userId,
                    name = name,
                    type = type,
                    dosage = dosage,
                    instructions = instructions
                )
                showAddDialog = false
            }
        )
    }
    
    // Delete Confirmation Dialog
    showDeleteDialog?.let { medicine ->
        DeleteMedicineDialog(
            medicine = medicine,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                medicineViewModel.deleteMedicine(medicine.id)
                showDeleteDialog = null
            }
        )
    }
}

@Composable
fun UserFilterChips(
    users: List<User>,
    selectedUser: User?,
    onUserSelected: (User?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // "All" filter chip
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
fun MedicinesList(
    medicines: List<Medicine>,
    onDeleteClick: (Medicine) -> Unit,
    onToggleStatus: (Medicine) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(medicines) { medicine ->
            MedicineCard(
                medicine = medicine,
                onDeleteClick = { onDeleteClick(medicine) },
                onToggleStatus = { onToggleStatus(medicine) }
            )
        }
    }
}

@Composable
fun MedicineCard(
    medicine: Medicine,
    onDeleteClick: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (medicine.isActive) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Medicine Image or Icon
            if (medicine.imageUrl != null) {
                AsyncImage(
                    model = medicine.imageUrl,
                    contentDescription = medicine.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = when (medicine.type) {
                            MedicineType.TABLET -> Icons.Default.Medication
                            MedicineType.INJECTION -> Icons.Default.LocalHospital
                            MedicineType.INSULIN -> Icons.Default.Healing
                        },
                        contentDescription = null,
                        modifier = Modifier.padding(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Medicine Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (medicine.isActive)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = stringResource(
                        when (medicine.type) {
                            MedicineType.TABLET -> R.string.type_tablet
                            MedicineType.INJECTION -> R.string.type_injection
                            MedicineType.INSULIN -> R.string.type_insulin
                        }
                    ),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                medicine.dosage?.let { dosage ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dosage,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Action Buttons
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Active/Inactive Toggle
                IconButton(
                    onClick = onToggleStatus,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        if (medicine.isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = if (medicine.isActive) "Active" else "Inactive",
                        modifier = Modifier.size(28.dp),
                        tint = if (medicine.isActive) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outline
                    )
                }
                
                // Delete Button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyMedicinesView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Medication,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_medicines),
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineDialog(
    users: List<User>,
    preselectedUser: User?,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, MedicineType, String?, String?) -> Unit
) {
    var selectedUser by remember { mutableStateOf(preselectedUser ?: users.firstOrNull()) }
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MedicineType.TABLET) }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var showUserDropdown by remember { mutableStateOf(false) }
    var showTypeDropdown by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.add_medicine),
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
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUserDropdown) },
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
                
                // Medicine Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.medicine_name), fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
                
                // Medicine Type
                ExposedDropdownMenuBox(
                    expanded = showTypeDropdown,
                    onExpandedChange = { showTypeDropdown = !showTypeDropdown }
                ) {
                    OutlinedTextField(
                        value = stringResource(
                            when (selectedType) {
                                MedicineType.TABLET -> R.string.type_tablet
                                MedicineType.INJECTION -> R.string.type_injection
                                MedicineType.INSULIN -> R.string.type_insulin
                            }
                        ),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.medicine_type), fontSize = 18.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                    )
                    ExposedDropdownMenu(
                        expanded = showTypeDropdown,
                        onDismissRequest = { showTypeDropdown = false }
                    ) {
                        MedicineType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        stringResource(
                                            when (type) {
                                                MedicineType.TABLET -> R.string.type_tablet
                                                MedicineType.INJECTION -> R.string.type_injection
                                                MedicineType.INSULIN -> R.string.type_insulin
                                            }
                                        ),
                                        fontSize = 20.sp
                                    ) 
                                },
                                onClick = {
                                    selectedType = type
                                    showTypeDropdown = false
                                }
                            )
                        }
                    }
                }
                
                // Dosage
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text(stringResource(R.string.medicine_dosage), fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
                
                // Instructions
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text(stringResource(R.string.medicine_instructions), fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (name.isNotBlank() && selectedUser != null) {
                        onConfirm(
                            selectedUser!!.id,
                            name,
                            selectedType,
                            dosage.ifBlank { null },
                            instructions.ifBlank { null }
                        )
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
fun DeleteMedicineDialog(
    medicine: Medicine,
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
                text = "Delete ${medicine.name}?",
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

