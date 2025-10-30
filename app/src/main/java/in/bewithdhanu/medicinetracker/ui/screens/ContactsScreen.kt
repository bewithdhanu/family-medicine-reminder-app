package `in`.bewithdhanu.medicinetracker.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import `in`.bewithdhanu.medicinetracker.data.model.Bookmark
import `in`.bewithdhanu.medicinetracker.ui.components.AvatarPickerDialog
import `in`.bewithdhanu.medicinetracker.ui.components.ImagePickerDialog
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.BookmarkViewModel
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.LocalContext
import `in`.bewithdhanu.medicinetracker.utils.CameraHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: BookmarkViewModel,
    onNavigateBack: () -> Unit
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editing: Bookmark? by remember { mutableStateOf(null) }
    
    LaunchedEffect(Unit) { viewModel.loadBookmarks() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts", fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(32.dp))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showAddEditDialog = true }, modifier = Modifier.size(72.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(36.dp))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(64.dp), strokeWidth = 6.dp)
                }
            } else if (bookmarks.isEmpty()) {
                EmptyContactsView()
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(bookmarks) { b ->
                        ContactRowItem(
                            bookmark = b,
                            onEdit = { editing = b; showAddEditDialog = true },
                            onDelete = { viewModel.deleteBookmark(b.id) }
                        )
                    }
                }
            }
        }
    }
    
    if (showAddEditDialog) {
        AddEditContactDialog(
            initial = editing,
            onDismiss = { showAddEditDialog = false },
            onSave = { name, number, type, emoji, imageUri ->
                val context = LocalContext.current
                val dataUrl = imageUri?.let { CameraHelper.uriToBase64(context, it) }?.let { CameraHelper.base64ToDataUrl(it) }
                viewModel.upsertBookmark(
                    existingId = editing?.id,
                    name = name,
                    phone = number,
                    type = type,
                    photoUrl = dataUrl,
                    emoji = emoji
                )
                showAddEditDialog = false
            }
        )
    }
}

@Composable
private fun ContactRowItem(
    bookmark: Bookmark,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (bookmark.photoUrl != null) {
                AsyncImage(
                    model = bookmark.photoUrl,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).clip(CircleShape)
                )
            } else {
                Surface(
                    modifier = Modifier.size(56.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = bookmark.avatarEmoji ?: "🙂",
                        style = TextStyle(fontSize = 28.sp),
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(bookmark.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(bookmark.phoneNumber, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onEdit, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(28.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditContactDialog(
    initial: Bookmark?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String?, Uri?) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var number by remember { mutableStateOf(initial?.phoneNumber ?: "") }
    var contactType by remember { mutableStateOf(initial?.contactType ?: "phone") }
    var emoji by remember { mutableStateOf(initial?.avatarEmoji ?: "🙂") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var showImagePicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (initial == null) "Add Contact" else "Edit Contact", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Phone Number", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
                // Contact Type
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(selected = contactType == "phone", onClick = { contactType = "phone" }, label = { Text("Phone", fontSize = 18.sp) })
                    FilterChip(selected = contactType == "whatsapp", onClick = { contactType = "whatsapp" }, label = { Text("WhatsApp", fontSize = 18.sp) })
                }
                // Avatar Controls
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { showAvatarPicker = true }, modifier = Modifier.height(56.dp)) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose Avatar", fontSize = 20.sp)
                    }
                    Text(text = emoji, fontSize = 28.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, number, contactType, emoji, imageUri) }, modifier = Modifier.height(56.dp)) {
                Text("Save", fontSize = 20.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.height(56.dp)) {
                Text("Cancel", fontSize = 20.sp)
            }
        }
    )
    
    if (showAvatarPicker) {
        AvatarPickerDialog(
            currentEmoji = emoji,
            currentImageUri = imageUri,
            onPickEmoji = { e -> emoji = e; imageUri = null; showAvatarPicker = false },
            onPickImage = { showImagePicker = true },
            onDismiss = { showAvatarPicker = false }
        )
    }
    if (showImagePicker) {
        ImagePickerDialog(
            onDismiss = { showImagePicker = false },
            onImageSelected = { uri -> imageUri = uri; showImagePicker = false }
        )
    }
}

@Composable
private fun EmptyContactsView() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Contacts, contentDescription = null, modifier = Modifier.size(120.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(16.dp))
        Text("No contacts yet", fontSize = 24.sp, color = MaterialTheme.colorScheme.outline)
    }
}


