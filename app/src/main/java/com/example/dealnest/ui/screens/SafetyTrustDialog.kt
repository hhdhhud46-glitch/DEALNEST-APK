package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealEmerald

@Composable
fun SafetyTrustDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.GppGood, contentDescription = null, tint = DealEmerald, modifier = Modifier.size(24.dp))
                        Text("Safety & Trust Guidelines", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Scam Warning Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DealAmber.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DealAmber.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = DealAmber, modifier = Modifier.size(24.dp))
                            Column {
                                Text("Scam Prevention Notice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DealAmber)
                                Text(
                                    "Never exchange direct wire transfers or passwords outside DealNest. Keep discussions and milestones inside the platform.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Text("1. Milestone Escrow Protection", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Clients fund milestones before work begins. Payments are only released once the client approves each deliverable.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text("2. Verified Designer Standards", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "The 'Verified' badge indicates manual portfolio review and verified work history by DealNest admins. We actively remove fraudulent profiles.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text("3. Dispute & Flagging System", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "If a client or designer violates scope or behaves inappropriately, use the Flag/Report button on the project or profile. Our moderation team reviews all reports within 24 hours.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text("4. Terms & Privacy", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "DealNest never stores raw credit card details or unencrypted secrets. Intellectual property for custom website code transfers to the client upon final milestone approval.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = androidx.compose.ui.graphics.Color.Black),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("I Understand", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
