package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dealnest.model.ChatMessage
import com.example.dealnest.model.Conversation
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.util.AiChatHelper
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealEmerald
import com.example.ui.theme.DealIndigo

@Composable
fun AiConversationSummaryDialog(
    conversation: Conversation,
    messages: List<ChatMessage>,
    onDismiss: () -> Unit
) {
    val summary = remember(conversation, messages) {
        AiChatHelper.generateConversationSummary(conversation, messages)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .testTag("ai_conversation_summary_dialog")
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
                            color = DealCyanPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text("AI Conversation Summary", fontSize = 17.sp, fontWeight = FontWeight.Black)
                            Text("Extracted strictly from explicit chat dialogue", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Strict no-invention notice
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(15.dp))
                        Text(
                            text = "AI only summarizes explicit statements. Unagreed points are strictly noted as 'Not specified'.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Summary Sections
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. PROJECT REQUIREMENTS
                    SummarySectionCard(
                        title = "PROJECT REQUIREMENTS",
                        icon = Icons.Outlined.Checklist,
                        accentColor = DealCyanPrimary
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            summary.requirements.forEach { req ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("•", color = DealCyanPrimary, fontWeight = FontWeight.Bold)
                                    Text(req, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                                }
                            }
                        }
                    }

                    // 2. PRICE & DEADLINE CARDS (Side by side)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // PRICE
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("PRICE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DealEmerald)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = summary.price,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (summary.price.contains("Not specified")) MaterialTheme.colorScheme.onSurfaceVariant else DealEmerald
                                )
                            }
                        }

                        // DEADLINE
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("DEADLINE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DealIndigo)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = summary.deadline,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (summary.deadline.contains("Not specified")) MaterialTheme.colorScheme.onSurfaceVariant else DealIndigo
                                )
                            }
                        }
                    }

                    // 3. PENDING QUESTIONS
                    SummarySectionCard(
                        title = "PENDING QUESTIONS",
                        icon = Icons.Outlined.HelpOutline,
                        accentColor = DealAmber
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            summary.pendingQuestions.forEach { q ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("?", color = DealAmber, fontWeight = FontWeight.Bold)
                                    Text(q, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                                }
                            }
                        }
                    }

                    // 4. NEXT STEPS
                    SummarySectionCard(
                        title = "NEXT STEPS",
                        icon = Icons.Outlined.TrendingUp,
                        accentColor = DealEmerald
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            summary.nextSteps.forEachIndexed { idx, step ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("${idx + 1}.", color = DealEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text(step, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("Close Summary", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SummarySectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(15.dp))
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Black, color = accentColor)
            }
            content()
        }
    }
}
