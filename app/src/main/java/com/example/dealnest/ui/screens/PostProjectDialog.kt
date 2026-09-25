package com.example.dealnest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.dealnest.ui.DealNestViewModel
import com.example.ui.theme.DealCyanPrimary

@Composable
fun PostProjectDialog(
    viewModel: DealNestViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var websiteType by remember { mutableStateOf("E-Commerce") }
    var description by remember { mutableStateOf("") }
    var budgetMinText by remember { mutableStateOf("1500") }
    var budgetMaxText by remember { mutableStateOf("3000") }
    var deadlineDaysText by remember { mutableStateOf("14") }
    var requiredSkills by remember { mutableStateOf("Shopify, Figma, Tailwind CSS") }
    var pageCountText by remember { mutableStateOf("5") }
    var requiredFeatures by remember { mutableStateOf("Responsive Mobile UI, Cart, SEO, Fast Speed") }
    var referenceLinks by remember { mutableStateOf("https://example.com") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAiBuilderDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .testTag("post_project_dialog")
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
                            text = "Post a Website Project",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Connect with qualified designers within hours",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AI Client Requirement Builder Button (Feature 5)
                OutlinedButton(
                    onClick = { showAiBuilderDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DealCyanPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DealCyanPrimary.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_project_with_ai_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = DealCyanPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Project with AI",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DealCyanPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Project Title") },
                        placeholder = { Text("e.g. Modern E-Commerce Store for Organic Skincare") },
                        modifier = Modifier.fillMaxWidth().testTag("project_title_input")
                    )

                    // Website Type Dropdown / Row
                    Text("Website Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    val types = listOf("E-Commerce", "SaaS", "Portfolio", "Landing Page", "Booking", "Corporate")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        types.take(3).forEach { t ->
                            FilterChip(
                                selected = websiteType == t,
                                onClick = { websiteType = t },
                                label = { Text(t, fontSize = 11.sp) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        types.drop(3).forEach { t ->
                            FilterChip(
                                selected = websiteType == t,
                                onClick = { websiteType = t },
                                label = { Text(t, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Scope & Description") },
                        placeholder = { Text("Explain your goals, target audience, and what makes your business unique...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("project_desc_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = budgetMinText,
                            onValueChange = { budgetMinText = it },
                            label = { Text("Min Budget ($)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = budgetMaxText,
                            onValueChange = { budgetMaxText = it },
                            label = { Text("Max Budget ($)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = deadlineDaysText,
                            onValueChange = { deadlineDaysText = it },
                            label = { Text("Deadline (Days)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pageCountText,
                            onValueChange = { pageCountText = it },
                            label = { Text("Number of Pages") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = requiredSkills,
                        onValueChange = { requiredSkills = it },
                        label = { Text("Required Skills (comma separated)") },
                        placeholder = { Text("e.g. Next.js, Webflow, Figma, Shopify") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = requiredFeatures,
                        onValueChange = { requiredFeatures = it },
                        label = { Text("Key Features") },
                        placeholder = { Text("e.g. Booking widget, Dark mode, Animation") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = referenceLinks,
                        onValueChange = { referenceLinks = it },
                        label = { Text("Inspiration / Reference Links") },
                        placeholder = { Text("https://dribbble.com/example") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (title.isBlank() || description.isBlank()) {
                            errorMessage = "Please enter a title and description."
                            return@Button
                        }
                        val min = budgetMinText.toDoubleOrNull() ?: 500.0
                        val max = budgetMaxText.toDoubleOrNull() ?: 2000.0
                        val days = deadlineDaysText.toIntOrNull() ?: 14
                        val pages = pageCountText.toIntOrNull() ?: 5

                        viewModel.postProject(
                            title = title,
                            websiteType = websiteType,
                            description = description,
                            budgetMin = min,
                            budgetMax = max,
                            deadlineDays = days,
                            requiredSkills = requiredSkills,
                            pageCount = pages,
                            requiredFeatures = requiredFeatures,
                            referenceLinks = referenceLinks
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DealCyanPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_post_project_button")
                ) {
                    Text("Publish Project to Marketplace", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showAiBuilderDialog) {
        AiRequirementBuilderDialog(
            onDismiss = { showAiBuilderDialog = false },
            onApplyBrief = { bTitle, bType, bDesc, bMin, bMax, bDays, bSkills, bPages, bFeatures ->
                title = bTitle
                websiteType = bType
                description = bDesc
                budgetMinText = bMin.toInt().toString()
                budgetMaxText = bMax.toInt().toString()
                deadlineDaysText = bDays.toString()
                requiredSkills = bSkills
                pageCountText = bPages.toString()
                requiredFeatures = bFeatures
                showAiBuilderDialog = false
            }
        )
    }
}
