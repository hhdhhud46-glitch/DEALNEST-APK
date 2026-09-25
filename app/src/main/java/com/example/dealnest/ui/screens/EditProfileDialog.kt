package com.example.dealnest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dealnest.model.User
import com.example.dealnest.ui.DealNestViewModel
import com.example.ui.theme.DealCyanPrimary

@Composable
fun EditProfileDialog(
    viewModel: DealNestViewModel,
    currentUser: User,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentUser.name) }
    var headline by remember { mutableStateOf(currentUser.headline) }
    var skills by remember { mutableStateOf(currentUser.skills) }
    var startingPriceText by remember { mutableStateOf(currentUser.startingPrice.toInt().toString()) }
    var availability by remember { mutableStateOf(currentUser.availability) }
    var location by remember { mutableStateOf(currentUser.location) }
    var about by remember { mutableStateOf(currentUser.about) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .testTag("edit_profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edit Profile Details", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name / Business") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = headline,
                        onValueChange = { headline = it },
                        label = { Text("Professional Headline") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Skills (Comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = startingPriceText,
                        onValueChange = { startingPriceText = it },
                        label = { Text("Starting Price ($)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = availability,
                        onValueChange = { availability = it },
                        label = { Text("Availability") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = about,
                        onValueChange = { about = it },
                        label = { Text("About / Bio") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val price = startingPriceText.toDoubleOrNull() ?: 0.0
                        viewModel.updateProfile(
                            name = name,
                            headline = headline,
                            skills = skills,
                            startingPrice = price,
                            availability = availability,
                            location = location,
                            about = about
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
