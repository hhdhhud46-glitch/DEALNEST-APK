package com.example.dealnest.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.dealnest.model.PortfolioProject
import com.example.dealnest.model.User
import com.example.dealnest.model.UserRole
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.AvatarImage
import com.example.dealnest.ui.components.StarRatingBar
import com.example.dealnest.ui.components.StatusChip
import com.example.dealnest.ui.components.VerifiedBadge
import com.example.ui.theme.*

@Composable
fun PortfolioBuilderDialog(
    viewModel: DealNestViewModel,
    designer: User,
    currentUser: User,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val portfolioProjects by viewModel.currentPortfolioProjects.collectAsState()
    val isOwner = currentUser.id == designer.id || currentUser.role == UserRole.ADMIN.name

    val shareableSlug = remember(designer) {
        designer.name.lowercase().replace(" ", "-").replace(".", "")
    }
    val shareableUrl = "https://dealnest.com/p/$shareableSlug"

    var showAddProjectSheet by remember { mutableStateOf(false) }
    var newProjTitle by remember { mutableStateOf("") }
    var newProjCategory by remember { mutableStateOf("SaaS Website") }
    var newProjDescription by remember { mutableStateOf("") }
    var newProjTechStack by remember { mutableStateOf("Next.js, Tailwind, Stripe") }
    var newProjLiveUrl by remember { mutableStateOf("https://example.com") }
    var newProjMetrics by remember { mutableStateOf("+120% Conversion Lift") }

    val gradientList = listOf(
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
                .fillMaxHeight(0.92f)
                .testTag("portfolio_builder_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header Row
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
                            text = if (isOwner) "Your Public Portfolio" else "${designer.name}'s Portfolio",
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
                                text = shareableUrl,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(shareableUrl))
                                viewModel.emitSnackbar("Portfolio link copied to clipboard!")
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DealCyanPrimary,
                                contentColor = Color.Black
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Copy Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Header Card
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    AvatarImage(
                                        name = designer.name,
                                        avatarUrl = designer.avatarUrl,
                                        size = 64.dp
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = designer.name,
                                                fontSize = 17.sp,
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

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            StarRatingBar(rating = designer.rating, reviewCount = designer.reviewCount)
                                            Text(
                                                text = "• ${designer.completedProjectsCount} deals done",
                                                fontSize = 11.sp,
                                                color = DealEmerald,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = designer.about,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Rates & Availability Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("STARTING AT", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                        Text(viewModel.formatPrice(designer.startingPrice), fontSize = 14.sp, fontWeight = FontWeight.Black, color = DealEmerald)
                                    }
                                    Column {
                                        Text("AVAILABILITY", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                        Text(designer.availability, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary)
                                    }
                                    Column {
                                        Text("DELIVERY", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                        Text("${designer.deliveryTimeDays} Days Avg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }

                    // Services Offered
                    item {
                        Text(
                            text = "OFFERED SERVICES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val services = listOf(
                            "Full Website Design & Build" to "From wireframes in Figma to production code with CMS integration.",
                            "Design System & UI Components" to "Scalable component libraries, typography tokens & design assets.",
                            "Speed & SEO Optimization" to "Sub-second load times, 95+ Google Lighthouse scores, and semantic schema.",
                            "E-Commerce & Stripe Billing" to "Custom checkout funnels, product management, and payment gateways."
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            services.forEach { (title, desc) ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(18.dp))
                                        Column {
                                            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Portfolio Projects Header & Add Button
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FEATURED CASE STUDIES (${portfolioProjects.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )

                            if (isOwner) {
                                TextButton(
                                    onClick = { showAddProjectSheet = !showAddProjectSheet },
                                    modifier = Modifier.testTag("add_portfolio_project_btn")
                                ) {
                                    Icon(if (showAddProjectSheet) Icons.Default.ExpandLess else Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (showAddProjectSheet) "Close Form" else "Add Project", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary)
                                }
                            }
                        }
                    }

                    // Add Project Form (Inline accordion)
                    if (isOwner && showAddProjectSheet) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                border = BorderStroke(1.dp, DealCyanPrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Add New Showcase Case Study", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                                    OutlinedTextField(
                                        value = newProjTitle,
                                        onValueChange = { newProjTitle = it },
                                        label = { Text("Project Title") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = newProjCategory,
                                            onValueChange = { newProjCategory = it },
                                            label = { Text("Category") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = newProjMetrics,
                                            onValueChange = { newProjMetrics = it },
                                            label = { Text("Metric / Result") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    OutlinedTextField(
                                        value = newProjTechStack,
                                        onValueChange = { newProjTechStack = it },
                                        label = { Text("Tech Stack (comma separated)") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = newProjDescription,
                                        onValueChange = { newProjDescription = it },
                                        label = { Text("Description & Outcome") },
                                        minLines = 2,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Button(
                                        onClick = {
                                            if (newProjTitle.isNotBlank()) {
                                                viewModel.addPortfolioProject(
                                                    title = newProjTitle,
                                                    category = newProjCategory,
                                                    description = newProjDescription.ifBlank { "Modern high-converting web presence with clean code and design tokens." },
                                                    techStack = newProjTechStack,
                                                    liveUrl = newProjLiveUrl,
                                                    metrics = newProjMetrics
                                                )
                                                showAddProjectSheet = false
                                                newProjTitle = ""
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Publish Project to Portfolio", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // List of Showcase Projects
                    items(portfolioProjects, key = { it.id }) { project ->
                        val brush = gradientList[project.gradientColorIndex % gradientList.size]

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                // Visual Banner Screenshot Mockup
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .background(brush)
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.Black.copy(alpha = 0.6f)
                                        ) {
                                            Text(
                                                text = project.category,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = DealEmerald.copy(alpha = 0.85f)
                                        ) {
                                            Text(
                                                text = project.metricsHighlight,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.Black,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = project.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.align(Alignment.BottomStart)
                                    )
                                }

                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = project.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Tech chips
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        project.techStack.split(",").take(3).forEach { tech ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = tech.trim(),
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (isOwner) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.deletePortfolioProject(project.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Button: Contact/Deal Button
                if (!isOwner) {
                    Button(
                        onClick = {
                            viewModel.selectedDesignerForDetail.value = designer
                            onDismiss()
                            // If user is client, trigger message/deal flow
                            viewModel.emitSnackbar("Opened direct inquiry channel with ${designer.name}!")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DealCyanPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.Handshake, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Deal / Contact ${designer.name.split(" ").first()}", fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
