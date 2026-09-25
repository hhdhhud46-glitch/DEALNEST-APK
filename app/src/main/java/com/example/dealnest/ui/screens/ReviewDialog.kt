package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
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
import com.example.dealnest.model.Deal
import com.example.dealnest.model.DealStatus
import com.example.dealnest.model.User
import com.example.dealnest.ui.DealNestViewModel
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealEmerald

@Composable
fun ReviewDialog(
    viewModel: DealNestViewModel,
    deal: Deal,
    currentUser: User,
    onDismiss: () -> Unit
) {
    val isClient = currentUser.id == deal.clientUserId
    val targetUserId = if (isClient) deal.designerUserId else deal.clientUserId
    val targetUserName = if (isClient) deal.designerName else deal.clientName
    val targetRoleLabel = if (isClient) "Designer" else "Client"

    var rating by remember { mutableStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    val feedbackOptions = listOf("Timely Delivery", "Clean Code & Specs", "Fast Revisions", "Clear Communication", "Milestone Adherence")
    val selectedFeedback = remember { mutableStateListOf("Timely Delivery", "Clear Communication") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val ratingLabels = mapOf(
        5 to "Exceptional ★★★★★",
        4 to "Very Good ★★★★☆",
        3 to "Good / Satisfactory ★★★☆☆",
        2 to "Fair ★★☆☆☆",
        1 to "Needs Improvement ★☆☆☆☆"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .testTag("review_dialog")
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
                    Column {
                        Text(
                            text = "Leave Completed Deal Review",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Review for $targetUserName ($targetRoleLabel)",
                            fontSize = 12.sp,
                            color = DealCyanPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Project Context Strip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Project: ${deal.projectTitle}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Agreed Price: $${deal.agreedPrice.toInt()} • Status: ${deal.status}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Star Rating Picker
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Rating", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "$star Stars",
                                    tint = DealAmber,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clickable { rating = star }
                                        .padding(2.dp)
                                )
                            }
                        }
                        Text(
                            text = ratingLabels[rating] ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DealAmber
                        )
                    }

                    // Optional Project Feedback Tags
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Project Strengths (Optional)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            feedbackOptions.take(3).forEach { option ->
                                val isSelected = selectedFeedback.contains(option)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) selectedFeedback.remove(option)
                                        else selectedFeedback.add(option)
                                    },
                                    label = { Text(option, fontSize = 10.sp) }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            feedbackOptions.drop(3).forEach { option ->
                                val isSelected = selectedFeedback.contains(option)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) selectedFeedback.remove(option)
                                        else selectedFeedback.add(option)
                                    },
                                    label = { Text(option, fontSize = 10.sp) }
                                )
                            }
                        }
                    }

                    // Written Review Field
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Written Review Feedback") },
                        placeholder = { Text("Describe your experience collaborating with $targetUserName...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("review_text_input")
                    )

                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (targetUserId == currentUser.id) {
                            errorMessage = "You cannot review yourself."
                            return@Button
                        }
                        if (deal.status != DealStatus.COMPLETED.name) {
                            errorMessage = "Reviews can only be submitted for completed deals."
                            return@Button
                        }
                        if (reviewText.isBlank()) {
                            errorMessage = "Please enter some written feedback before submitting."
                            return@Button
                        }
                        isSubmitting = true
                        viewModel.submitReview(
                            dealId = deal.id,
                            targetUserId = targetUserId,
                            rating = rating,
                            reviewText = reviewText,
                            categoryFeedback = selectedFeedback.joinToString(", "),
                            projectTitle = deal.projectTitle
                        )
                    },
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DealCyanPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_review_button")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black)
                    } else {
                        Text("Publish Public Review", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
