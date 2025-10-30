package `in`.bewithdhanu.medicinetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.bewithdhanu.medicinetracker.R
import `in`.bewithdhanu.medicinetracker.utils.CommunicationHelper
import `in`.bewithdhanu.medicinetracker.ui.viewmodel.BookmarkViewModel
import `in`.bewithdhanu.medicinetracker.data.model.Bookmark
import `in`.bewithdhanu.medicinetracker.data.model.UpdateBookmarkRequest
import `in`.bewithdhanu.medicinetracker.data.model.CreateBookmarkRequest
import `in`.bewithdhanu.medicinetracker.utils.CameraHelper
import `in`.bewithdhanu.medicinetracker.ui.components.ImagePickerDialog
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import android.net.Uri

/**
 * Communication shortcuts card for Dashboard
 */
@Composable
fun CommunicationCard(viewModel: BookmarkViewModel) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    
    val bookmarks by viewModel.bookmarks.collectAsState()
    val contact1: Bookmark? = bookmarks.getOrNull(0)
    val contact2: Bookmark? = bookmarks.getOrNull(1)
    var phoneNumber1 by remember { mutableStateOf(contact1?.phoneNumber ?: "+919876543210") }
    var contactName1 by remember { mutableStateOf(contact1?.name ?: "Father") }
    var phoneNumber2 by remember { mutableStateOf(contact2?.phoneNumber ?: "+919876543211") }
    var contactName2 by remember { mutableStateOf(contact2?.name ?: "Mother") }
    // Avatars (emoji or image)
    var contactEmoji1 by remember { mutableStateOf("👨") }
    var contactImageUri1 by remember { mutableStateOf<Uri?>(null) }
    var contactEmoji2 by remember { mutableStateOf("👩") }
    var contactImageUri2 by remember { mutableStateOf<Uri?>(null) }
    var showAvatarPickerFor by remember { mutableStateOf<Int?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }
    var pendingAvatarIndex by remember { mutableStateOf<Int?>(null) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Contacts",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Edit Contacts",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Contact 1
            ContactRow(
                name = contactName1,
                phoneNumber = phoneNumber1,
                emoji = contact1?.avatarEmoji ?: contactEmoji1,
                imageUri = contact1?.photoUrl?.let { Uri.parse(it) } ?: contactImageUri1,
                onCallClick = { CommunicationHelper.makePhoneCall(context, phoneNumber1) },
                onWhatsAppClick = { CommunicationHelper.openWhatsAppChat(context, phoneNumber1) },
                onAvatarClick = { showAvatarPickerFor = 1 }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Contact 2
            ContactRow(
                name = contactName2,
                phoneNumber = phoneNumber2,
                emoji = contact2?.avatarEmoji ?: contactEmoji2,
                imageUri = contact2?.photoUrl?.let { Uri.parse(it) } ?: contactImageUri2,
                onCallClick = { CommunicationHelper.makePhoneCall(context, phoneNumber2) },
                onWhatsAppClick = { CommunicationHelper.openWhatsAppChat(context, phoneNumber2) },
                onAvatarClick = { showAvatarPickerFor = 2 }
            )
        }
    }
    
    // Edit Contacts Dialog
    if (showEditDialog) {
        EditContactsDialog(
            contact1Name = contactName1,
            contact1Number = phoneNumber1,
            contact2Name = contactName2,
            contact2Number = phoneNumber2,
            onDismiss = { showEditDialog = false },
            onSave = { name1, number1, name2, number2 ->
                contactName1 = name1
                phoneNumber1 = number1
                contactName2 = name2
                phoneNumber2 = number2
                // Persist to backend (create or update first two bookmarks)
                if (contact1 == null) {
                    viewModel.upsertBookmark(
                        existingId = null,
                        name = contactName1,
                        phone = phoneNumber1,
                        type = "phone",
                        photoUrl = contactImageUri1?.let { CameraHelper.uriToBase64(context, it) }?.let { CameraHelper.base64ToDataUrl(it) },
                        emoji = contactEmoji1
                    )
                } else {
                    viewModel.upsertBookmark(
                        existingId = contact1.id,
                        name = contactName1,
                        phone = phoneNumber1,
                        type = contact1.contactType,
                        photoUrl = contactImageUri1?.let { CameraHelper.uriToBase64(context, it) }?.let { CameraHelper.base64ToDataUrl(it) } ?: contact1.photoUrl,
                        emoji = contactEmoji1
                    )
                }
                if (contact2 == null) {
                    viewModel.upsertBookmark(
                        existingId = null,
                        name = contactName2,
                        phone = phoneNumber2,
                        type = "phone",
                        photoUrl = contactImageUri2?.let { CameraHelper.uriToBase64(context, it) }?.let { CameraHelper.base64ToDataUrl(it) },
                        emoji = contactEmoji2
                    )
                } else {
                    viewModel.upsertBookmark(
                        existingId = contact2.id,
                        name = contactName2,
                        phone = phoneNumber2,
                        type = contact2.contactType,
                        photoUrl = contactImageUri2?.let { CameraHelper.uriToBase64(context, it) }?.let { CameraHelper.base64ToDataUrl(it) } ?: contact2.photoUrl,
                        emoji = contactEmoji2
                    )
                }
                showEditDialog = false
            }
        )
    }

    // Avatar Picker (emoji or image)
    showAvatarPickerFor?.let { which ->
        AvatarPickerDialog(
            currentEmoji = if (which == 1) contactEmoji1 else contactEmoji2,
            currentImageUri = if (which == 1) contactImageUri1 else contactImageUri2,
            onPickEmoji = { emoji ->
                if (which == 1) {
                    contactEmoji1 = emoji
                    contactImageUri1 = null
                } else {
                    contactEmoji2 = emoji
                    contactImageUri2 = null
                }
                showAvatarPickerFor = null
            },
            onPickImage = {
                pendingAvatarIndex = which
                showImagePicker = true
            },
            onDismiss = { showAvatarPickerFor = null }
        )
    }

    if (showImagePicker) {
        ImagePickerDialog(
            onDismiss = { showImagePicker = false },
            onImageSelected = { uri ->
                if (pendingAvatarIndex == 1) {
                    contactImageUri1 = uri
                } else if (pendingAvatarIndex == 2) {
                    contactImageUri2 = uri
                }
                showImagePicker = false
                showAvatarPickerFor = null
            }
        )
    }
}

@Composable
fun ContactRow(
    name: String,
    phoneNumber: String,
    emoji: String?,
    imageUri: Uri?,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            // Avatar
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = emoji ?: "🙂",
                        style = TextStyle(fontSize = 28.sp),
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = phoneNumber,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            }
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Change avatar (emoji/image)
            FilledTonalButton(
                onClick = onAvatarClick,
                modifier = Modifier.size(56.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(28.dp)
                )
            }
            // Phone Call Button
            FilledTonalButton(
                onClick = onCallClick,
                modifier = Modifier.size(56.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(
                    Icons.Default.Call,
                    contentDescription = "Call",
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }
            
            // WhatsApp Button
            FilledTonalButton(
                onClick = onWhatsAppClick,
                modifier = Modifier.size(56.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF25D366)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_whatsapp),
                    contentDescription = "WhatsApp",
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactsDialog(
    contact1Name: String,
    contact1Number: String,
    contact2Name: String,
    contact2Number: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name1 by remember { mutableStateOf(contact1Name) }
    var number1 by remember { mutableStateOf(contact1Number) }
    var name2 by remember { mutableStateOf(contact2Name) }
    var number2 by remember { mutableStateOf(contact2Number) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Contacts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Contact 1
                Text("Contact 1", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = name1,
                    onValueChange = { name1 = it },
                    label = { Text("Name", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
                OutlinedTextField(
                    value = number1,
                    onValueChange = { number1 = it },
                    label = { Text("Phone Number", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Contact 2
                Text("Contact 2", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = name2,
                    onValueChange = { name2 = it },
                    label = { Text("Name", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
                OutlinedTextField(
                    value = number2,
                    onValueChange = { number2 = it },
                    label = { Text("Phone Number", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name1, number1, name2, number2) },
                modifier = Modifier.height(56.dp)
            ) {
                Text("Save", fontSize = 20.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(56.dp)
            ) {
                Text("Cancel", fontSize = 20.sp)
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvatarPickerDialog(
    currentEmoji: String?,
    currentImageUri: Uri?,
    onPickEmoji: (String) -> Unit,
    onPickImage: () -> Unit,
    onDismiss: () -> Unit
) {
    val emojis = listOf("👨", "👩", "🧓", "👴", "👵", "🧑", "👧", "👦", "❤️", "💊")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Choose Avatar", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Select an emoji:", fontSize = 18.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    emojis.forEach { e ->
                        FilledTonalButton(
                            onClick = { onPickEmoji(e) },
                            modifier = Modifier.size(48.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = e, fontSize = 22.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Or upload an image:", fontSize = 18.sp)
                Button(onClick = onPickImage, modifier = Modifier.height(56.dp)) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choose Photo", fontSize = 20.sp)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.height(56.dp)) {
                Text("Close", fontSize = 20.sp)
            }
        }
    )
}

