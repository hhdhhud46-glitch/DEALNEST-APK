package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dealnest.model.Review
import com.example.dealnest.model.User
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.AvatarImage
import com.example.dealnest.ui.components.StarRatingBar
import com.example.dealnest.ui.components.StatusChip
import com.example.dealnest.ui.components.VerifiedBadge
import com.example.ui.theme.*

@Composable
fun PublicPortfolioDialog(
    viewModel: DealNestViewModel,
    designer: User,
    currentUser: User,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val portfolioProjects by viewModel.currentPortfolioProjects.collectAsState()
    val services by viewModel.currentDesignerServices.collectAsState()
    val allReviews by viewModel.allReviews.collectAsState()

    val designerReviews = remember(allReviews, designer.id) {
        allReviews.filter { it.targetUserId == designer.id }
    }

    val slug = remember(designer) {
        designer.name.lowercase().replace(" ", "-").replace(".", "")
    }
    val portfolioUrl = "https://dealnest.com/portfolio/$slug"

    val gradients = listOf(
        Brush.horizontalGradient(listOf(DealCyanPrimary, DealIndigo)),
        Brush.horizontalGradient(listOf(DealIndigo, DealEmerald)),
        Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))),
        Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFF10B981)))
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .testTag("public_portfolio_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = DealCyanPrimary)
                        Text(
                            text = "${designer.name}'s Portfolio",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Shareable Link Strip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = portfolioUrl,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(portfolioUrl))
                                viewModel.emitSnackbar("Shareable portfolio URL copied!")
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Copy Link", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Header Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AvatarImage(
                                        name = designer.name,
                                        avatarUrl = designer.avatarUrl,
                                        size = 56.dp
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = designer.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (designer.isVerified) {
                                                VerifiedBadge(onClick = { viewModel.showVerificationInfoDialog.value = true })
                                            }
                                        }

                                        Text(
                                            text = designer.headline,
                                            fontSize = 12.sp,
                                            color = DealCyanPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            StarRatingBar(rating = designer.rating, reviewCount = designer.completedProjectsCount)
                                            Text("• ${designer.location}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = designer.about,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Starting Rate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("From $${designer.startingPrice.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = DealEmerald)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Availability", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(designer.availability, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DealCyanPrimary)
                                    }
                                }
                            }
                        }
                    }

                    // Skills Section
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Core Skills & Stack", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                designer.skills.split(",").take(6).forEach { skill ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = skill.trim(),
                                            fontSize = 11.sp,
                                            color = DealCyanPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Services Offered Section
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Services Offered", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                            if (services.isEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Custom Website Design & Development",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = designer.services,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                services.forEach { srv ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(srv.serviceName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text(srv.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                                Text("Est. ${srv.deliveryDays} Days", fontSize = 10.sp, color = DealIndigo)
                                            }
                                            Text("$${srv.startingPrice.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DealEmerald)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Portfolio Projects Showcase
                    item {
                        Text("Featured Projects (${portfolioProjects.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    if (portfolioProjects.isEmpty()) {
                        item {
                            Text("No projects showcased yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        items(portfolioProjects) { proj ->
                            val brush = gradients[proj.gradientColorIndex % gradients.size]
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .background(brush)
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = proj.category,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier
                                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                .align(Alignment.TopStart)
                                        )
                                        Text(
                                            text = proj.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            modifier = Modifier.align(Alignment.BottomStart)
                                        )
                                    }

                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(proj.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Stack: ${proj.techStack}", fontSize = 10.sp, color = DealCyanPrimary)
                                        if (proj.liveUrl.isNotBlank()) {
                                            Text("🔗 ${proj.liveUrl}", fontSize = 10.sp, color = DealIndigo)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Rating & Reviews Section
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Client Reviews (${designerReviews.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                            // Rating Breakdown Bars
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    RatingBarRow(label = "5 Stars", count = designer.ratingBreakdown5, total = (designer.reviewCount).coerceAtLeast(1))
                                    RatingBarRow(label = "4 Stars", count = designer.ratingBreakdown4, total = (designer.reviewCount).coerceAtLeast(1))
                                    RatingBarRow(label = "3 Stars", count = designer.ratingBreakdown3, total = (designer.reviewCount).coerceAtLeast(1))
                                    RatingBarRow(label = "2 Stars", count = designer.ratingBreakdown2, total = (designer.reviewCount).coerceAtLeast(1))
                                    RatingBarRow(label = "1 Star", count = designer.ratingBreakdown1, total = (designer.reviewCount).coerceAtLeast(1))
                                }
                            }

                            if (designerReviews.isEmpty()) {
                                Text("No written reviews yet.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                designerReviews.take(3).forEach { rev ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(rev.authorName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Star, contentDescription = null, tint = DealAmber, modifier = Modifier.size(12.dp))
                                                    Text(" ${rev.rating}.0", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(rev.reviewText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.emitSnackbar("Contact ${designer.name} via: ${designer.email.ifBlank { "in-app messaging" }}")
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("portfolio_contact_button")
                        ) {
                            Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Contact", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(portfolioUrl))
                                viewModel.emitSnackbar("Public Portfolio link copied: $portfolioUrl")
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("portfolio_view_link_button")
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Portfolio", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                onDismiss()
                                viewModel.startChatWithDesigner(designer)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("portfolio_chat_button")
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Chat", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                viewModel.selectedDesignerForDetail.value = designer
                                viewModel.showPostProjectDialog.value = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                            modifier = Modifier.weight(1.2f).height(44.dp).testTag("portfolio_send_project_button")
                        ) {
                            Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send Project", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBarRow(label: String, count: Int, total: Int) {
    val progress = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(42.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = DealAmber,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
        Text("$count", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(16.dp))
    }
}
