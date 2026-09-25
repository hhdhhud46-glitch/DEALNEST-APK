package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealEmerald
import com.example.ui.theme.DealRose

@Composable
fun CommunityGuidelinesDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .testTag("community_guidelines_dialog")
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
                            imageVector = Icons.Default.GppGood,
                            contentDescription = null,
                            tint = DealEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Safety & Community",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Scam Warning Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DealRose.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, DealRose.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = DealRose, modifier = Modifier.size(20.dp))
                            Column {
                                Text(
                                    text = "Anti-Scam Alert: Keep Payments on DealNest",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DealRose
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Never send wire transfers, cryptocurrency, PayPal Friends & Family, or off-platform checks. DealNest Milestone Escrow protects your money until deliverables are inspected and approved.",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // 5 Key Rules
                    val rules = listOf(
                        "1. Protected Escrow Only" to "All project milestones must be funded and released within the Deal Room. Off-platform agreements void all dispute protection.",
                        "2. Professional Collaboration" to "Maintain respectful, punctual communication. Clear briefs and constructive design feedback yield the best websites.",
                        "3. Intellectual Property Rights" to "Clients receive full commercial rights upon final milestone completion. Designers retain right to showcase live URLs in their portfolio unless covered by an NDA.",
                        "4. Prompt Milestone Approvals" to "Clients have 7 days to request reasonable revisions per milestone before automatic release.",
                        "5. Zero Tolerance for Spam & Harassment" to "Unsolicited promotional messaging, external contact scraping, and deceptive proposals will result in immediate permanent account suspension."
                    )

                    rules.forEach { (rule, desc) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(rule, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(desc, fontSize = 11.sp, lineHeight = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("I Understand & Agree", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
