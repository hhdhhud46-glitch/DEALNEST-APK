package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.AvatarImage
import com.example.dealnest.ui.components.StarRatingBar
import com.example.dealnest.ui.components.VerifiedBadge
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealEmerald
import com.example.ui.theme.DealIndigo

@Composable
fun ClientComparisonDialog(
    viewModel: DealNestViewModel,
    onDismiss: () -> Unit
) {
    val comparisonList by viewModel.comparisonDesigners.collectAsState()
    val allDesigners by viewModel.allDesigners.collectAsState()

    // If empty, offer top 3 designers as default preview
    val displayList = if (comparisonList.isEmpty()) allDesigners.take(3) else comparisonList

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("client_comparison_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
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
                                Icon(Icons.Default.CompareArrows, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text(
                                text = "Compare Designers",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Side-by-side objective evaluation (${displayList.size}/3)",
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

                // Objective comparison notice
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Objective Comparison: DealNest presents unbiased credentials without declaring an automated 'winner' so you choose the right fit for your project.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Comparison Matrix (Horizontal Scroll for multi-columns)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        displayList.forEach { designer ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .width(220.dp)
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Remove button if comparing user-selected
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        AvatarImage(
                                            name = designer.name,
                                            avatarUrl = designer.avatarUrl,
                                            size = 48.dp
                                        )

                                        if (comparisonList.any { it.id == designer.id }) {
                                            IconButton(
                                                onClick = { viewModel.toggleComparison(designer) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = designer.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (designer.isVerified) {
                                                VerifiedBadge(onClick = { viewModel.showVerificationInfoDialog.value = true })
                                            }
                                        }
                                        Text(
                                            text = designer.headline,
                                            fontSize = 11.sp,
                                            color = DealCyanPrimary,
                                            maxLines = 1
                                        )
                                    }

                                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                    // Metric 1: Starting Price
                                    Column {
                                        Text("STARTING PRICE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(viewModel.formatPrice(designer.startingPrice), fontSize = 16.sp, fontWeight = FontWeight.Black, color = DealEmerald)
                                    }

                                    // Metric 2: Experience & Delivery
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("EXPERIENCE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${designer.experienceYears} Years", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Column {
                                            Text("AVG DELIVERY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${designer.deliveryTimeDays} Days", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Metric 3: Rating & Deals
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("RATING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            StarRatingBar(rating = designer.rating, reviewCount = designer.reviewCount)
                                        }
                                        Column {
                                            Text("COMPLETED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${designer.completedProjectsCount} Deals", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DealEmerald)
                                        }
                                    }

                                    // Metric 4: Availability & Languages
                                    Column {
                                        Text("AVAILABILITY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(designer.availability, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("Languages: ${designer.languages}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    // Metric 5: Core Skills
                                    Column {
                                        Text("CORE SKILLS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            designer.skills.split(",").take(2).forEach { skill ->
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = MaterialTheme.colorScheme.surfaceVariant
                                                ) {
                                                    Text(skill.trim(), fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    Button(
                                        onClick = {
                                            viewModel.selectedDesignerForDetail.value = designer
                                            onDismiss()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DealCyanPrimary,
                                            contentColor = Color.Black
                                        ),
                                        modifier = Modifier.fillMaxWidth().height(36.dp)
                                    ) {
                                        Text("View Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (comparisonList.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { viewModel.clearComparison() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Text("Clear Selection", fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DealCyanPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
