package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
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
import com.example.dealnest.ui.DealNestViewModel
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DealRose

@Composable
fun OpenDisputeDialog(
    viewModel: DealNestViewModel,
    deal: Deal,
    onDismiss: () -> Unit
) {
    val reasons = listOf(
        "Scope disagreement",
        "Missed deadline",
        "Quality dissatisfaction",
        "Communication breakdown",
        "Other"
    )

    var selectedReason by remember { mutableStateOf(reasons[0]) }
    var milestoneTitle by remember { mutableStateOf("Entire Project Scope") }
    var description by remember { mutableStateOf("") }
    var evidenceNotes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .testTag("open_dispute_dialog")
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
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DealRose.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Gavel, contentDescription = null, tint = DealRose, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text("Open a Deal Dispute", fontSize = 17.sp, fontWeight = FontWeight.Black)
                            Text("Neutral mediation by DealNest admin team", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Important policy banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DealAmber.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, DealAmber.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = DealAmber, modifier = Modifier.size(16.dp))
                        Text(
                            text = "DealNest mediation team will review project specs, milestones, and chat messages. Disputes are never decided automatically using AI.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Deal: ${deal.projectTitle}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                    Text("Reason for Dispute", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        reasons.forEach { r ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = selectedReason == r,
                                    onClick = { selectedReason = r }
                                )
                                Text(r, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = milestoneTitle,
                        onValueChange = { milestoneTitle = it },
                        label = { Text("Related Milestone / Scope Item") },
                        placeholder = { Text("e.g. Milestone 2: Frontend Implementation") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Description of the Issue") },
                        placeholder = { Text("Explain clearly what occurred and what resolution you expect...") },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth().testTag("dispute_desc_input")
                    )

                    OutlinedTextField(
                        value = evidenceNotes,
                        onValueChange = { evidenceNotes = it },
                        label = { Text("Evidence & Chat References (Optional)") },
                        placeholder = { Text("Mention specific dates, shared files, or agreed parameters...") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (description.isBlank()) {
                            errorMessage = "Please provide a detailed description of the dispute."
                            return@Button
                        }
                        viewModel.submitDispute(
                            deal = deal,
                            milestoneTitle = milestoneTitle.ifBlank { "Project Scope" },
                            reason = selectedReason,
                            description = description,
                            evidenceNotes = evidenceNotes
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DealRose, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("submit_dispute_button")
                ) {
                    Text("Submit Dispute to Mediation", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
