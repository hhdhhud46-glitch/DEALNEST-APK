package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Verified
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
import com.example.ui.theme.DealAmberLight
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealEmerald

@Composable
fun VerificationRequestDialog(
    viewModel: DealNestViewModel,
    currentUser: User,
    onDismiss: () -> Unit
) {
    var fullName by remember { mutableStateOf(currentUser.name) }
    var email by remember { mutableStateOf(currentUser.email) }
    var phone by remember { mutableStateOf("+1 555-0199") }
    var role by remember { mutableStateOf(currentUser.headline.ifBlank { "Senior Website Designer" }) }
    var skills by remember { mutableStateOf(currentUser.skills) }
    var portfolioUrl by remember { mutableStateOf(currentUser.portfolioLinks.ifBlank { "https://${currentUser.handle.ifBlank { "portfolio" }}.design" }) }
    var description by remember { mutableStateOf(currentUser.about.ifBlank { "Experienced in client web deliverables with proven track record of on-time milestone delivery." }) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .testTag("verification_request_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = DealEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Request Verification",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Admin reviewed platform badge",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // MVP Notice Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DealAmberLight.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notice: Verification is reviewed and approved manually by DealNest platform administrators. Private details (phone, email) are kept strictly confidential.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Legal Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("ver_fullname_input")
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Professional Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("ver_email_input")
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number (Confidential)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("ver_phone_input")
                    )

                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Professional Role Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("ver_role_input")
                    )

                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Primary Skills (e.g. Next.js, Webflow, Shopify)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("ver_skills_input")
                    )

                    OutlinedTextField(
                        value = portfolioUrl,
                        onValueChange = { portfolioUrl = it },
                        label = { Text("Live Portfolio / GitHub / Dribbble URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("ver_portfolio_input")
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Short Professional Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("ver_description_input")
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || portfolioUrl.isBlank()) {
                            errorMessage = "Please fill in all required verification fields."
                            return@Button
                        }
                        isSubmitting = true
                        viewModel.submitVerificationRequest(
                            fullName = fullName,
                            email = email,
                            phone = phone,
                            role = role,
                            skills = skills,
                            portfolioUrl = portfolioUrl,
                            description = description
                        )
                    },
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DealCyanPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("submit_verification_button")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                    } else {
                        Text("Submit Verification Application", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
