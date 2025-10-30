package `in`.bewithdhanu.medicinetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.bewithdhanu.medicinetracker.R
import `in`.bewithdhanu.medicinetracker.utils.CommunicationHelper

/**
 * Communication shortcuts card for Dashboard
 */
@Composable
fun CommunicationCard() {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    
    // TODO: Store these in DataStore preferences
    var phoneNumber1 by remember { mutableStateOf("+919876543210") }
    var contactName1 by remember { mutableStateOf("Father") }
    var phoneNumber2 by remember { mutableStateOf("+919876543211") }
    var contactName2 by remember { mutableStateOf("Mother") }
    
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
                onCallClick = { CommunicationHelper.makePhoneCall(context, phoneNumber1) },
                onWhatsAppClick = { CommunicationHelper.openWhatsAppChat(context, phoneNumber1) }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Contact 2
            ContactRow(
                name = contactName2,
                phoneNumber = phoneNumber2,
                onCallClick = { CommunicationHelper.makePhoneCall(context, phoneNumber2) },
                onWhatsAppClick = { CommunicationHelper.openWhatsAppChat(context, phoneNumber2) }
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
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ContactRow(
    name: String,
    phoneNumber: String,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

