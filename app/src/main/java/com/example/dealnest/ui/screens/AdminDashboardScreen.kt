package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dealnest.model.*
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.StatusChip
import com.example.dealnest.ui.components.VerifiedBadge
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    viewModel: DealNestViewModel,
    users: List<User>,
    projects: List<Project>,
    proposals: List<Proposal>,
    deals: List<Deal>,
    reports: List<ReportItem>,
    reviews: List<Review>,
    onDismiss: () -> Unit
) {
    var adminTab by remember { mutableStateOf("Analytics") } // Analytics, Disputes, Reports, Verification, Users
    val verificationRequests by viewModel.allVerificationRequests.collectAsState()
    val disputes by viewModel.allDisputes.collectAsState()
    val blockedUsers by viewModel.allBlockedUsers.collectAsState()

    var rejectingRequestId by remember { mutableStateOf<String?>(null) }
    var rejectionReasonInput by remember { mutableStateOf("") }

    var updatingDisputeId by remember { mutableStateOf<String?>(null) }
    var disputeNotesInput by remember { mutableStateOf("") }
    var targetDisputeStatus by remember { mutableStateOf("RESOLVED") }

    // Report filter category
    var reportCategoryFilter by remember { mutableStateOf("All") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .testTag("admin_dashboard_dialog")
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DealAmber.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = DealAmber, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text("DealNest Admin Console", fontSize = 17.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                            Text("Marketplace governance, disputes & analytics", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs Navigation Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tabs = listOf(
                        "Analytics" to "Analytics",
                        "Disputes" to "Disputes (${disputes.count { it.status == "OPEN" || it.status == "UNDER_REVIEW" }})",
                        "Reports" to "Reports (${reports.size})",
                        "Verification" to "Verification (${verificationRequests.count { it.status == "PENDING" }})",
                        "Users" to "Users (${users.size})"
                    )
                    tabs.forEach { (tabKey, label) ->
                        val isSelected = adminTab == tabKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { adminTab = tabKey },
                            label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DealAmber,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content Views
                when (adminTab) {
                    "Analytics" -> {
                        // 17. ADMIN ANALYTICS
                        val clientsCount = users.count { it.role == UserRole.CLIENT.name }
                        val designersCount = users.count { it.role == UserRole.DESIGNER.name }
                        val verifiedDesignersCount = users.count { it.role == UserRole.DESIGNER.name && it.isVerified }
                        val activeProjectsCount = projects.count { it.status == ProjectStatus.OPEN.name }
                        val completedProjectsCount = projects.count { it.status == "COMPLETED" || it.status == "CLOSED" }
                        val completedDealsCount = deals.count { it.status == DealStatus.COMPLETED.name }

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "USER & COMMUNITY METRICS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DealCyanPrimary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AdminKpiCard("Total Users", "${users.size}", DealCyanPrimary, Modifier.weight(1f))
                                    AdminKpiCard("Clients", "$clientsCount", DealIndigo, Modifier.weight(1f))
                                    AdminKpiCard("Designers", "$designersCount", DealEmerald, Modifier.weight(1f))
                                    AdminKpiCard("Verified Pros", "$verifiedDesignersCount", DealAmber, Modifier.weight(1f))
                                }
                            }

                            item {
                                Text(
                                    text = "PROJECTS & CONTRACTS PIPELINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DealEmerald,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AdminKpiCard("Total Projects", "${projects.size}", DealCyanPrimary, Modifier.weight(1f))
                                    AdminKpiCard("Active Open", "$activeProjectsCount", DealEmerald, Modifier.weight(1f))
                                    AdminKpiCard("Proposals", "${proposals.size}", DealIndigo, Modifier.weight(1f))
                                    AdminKpiCard("Completed", "$completedProjectsCount", Color(0xFF10B981), Modifier.weight(1f))
                                }
                            }

                            item {
                                Text(
                                    text = "DEALS, REVIEWS & INTEGRITY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DealAmber,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AdminKpiCard("All Deals", "${deals.size}", DealCyanPrimary, Modifier.weight(1f))
                                    AdminKpiCard("Completed Deals", "$completedDealsCount", DealEmerald, Modifier.weight(1f))
                                    AdminKpiCard("Reviews", "${reviews.size}", DealAmber, Modifier.weight(1f))
                                    AdminKpiCard("Disputes", "${disputes.size}", DealRose, Modifier.weight(1f))
                                }
                            }

                            // User Growth Chart
                            item {
                                AdminChartCard(
                                    title = "User Growth",
                                    subtitle = "Monthly new account registrations",
                                    labels = listOf("May", "Jun", "Jul", "Aug", "Sep"),
                                    values = listOf(42, 68, 95, 134, 182),
                                    gradient = listOf(DealCyanPrimary, DealIndigo)
                                )
                            }

                            // Project Growth Chart
                            item {
                                AdminChartCard(
                                    title = "Project Growth",
                                    subtitle = "Client website postings per month",
                                    labels = listOf("May", "Jun", "Jul", "Aug", "Sep"),
                                    values = listOf(14, 25, 38, 52, 74),
                                    gradient = listOf(DealEmerald, DealCyanPrimary)
                                )
                            }

                            // Deal Activity & Completed Projects Chart
                            item {
                                AdminChartCard(
                                    title = "Deal Activity & Completed Deliveries",
                                    subtitle = "Escrow agreements concluded",
                                    labels = listOf("May", "Jun", "Jul", "Aug", "Sep"),
                                    values = listOf(9, 18, 27, 41, 56),
                                    gradient = listOf(DealAmber, DealEmerald)
                                )
                            }
                        }
                    }

                    "Disputes" -> {
                        // 14. DISPUTE & RESOLUTION SYSTEM (Manual Admin review, NO AI auto-decisions)
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DealRose.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, DealRose.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Gavel, contentDescription = null, tint = DealRose, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = "Admin Dispute Mediation: Manually inspect project deliverables, milestones, and conversation logs. Automated AI resolution is disabled by safety policy.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            if (disputes.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                        Text("No disputes recorded. Platform operations healthy.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            } else {
                                items(disputes) { dispute ->
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                        border = BorderStroke(
                                            1.dp,
                                            when (dispute.status) {
                                                "OPEN" -> DealRose.copy(alpha = 0.6f)
                                                "UNDER_REVIEW" -> DealAmber.copy(alpha = 0.6f)
                                                "RESOLVED" -> DealEmerald.copy(alpha = 0.6f)
                                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            }
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("dispute_item_${dispute.id}")
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(dispute.projectTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                    Text("Dispute ID: ${dispute.id} • Deal: ${dispute.dealId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = when (dispute.status) {
                                                        "OPEN" -> DealRose.copy(alpha = 0.15f)
                                                        "UNDER_REVIEW" -> DealAmber.copy(alpha = 0.15f)
                                                        "RESOLVED" -> DealEmerald.copy(alpha = 0.15f)
                                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                                    }
                                                ) {
                                                    Text(
                                                        text = dispute.status.replace("_", " "),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = when (dispute.status) {
                                                            "OPEN" -> DealRose
                                                            "UNDER_REVIEW" -> DealAmber
                                                            "RESOLVED" -> DealEmerald
                                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                        },
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text("Raised by: ${dispute.raisedByName} (${dispute.raisedByRole}) ➔ Against: ${dispute.againstUserName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("Reason: ${dispute.reason} • Milestone: ${dispute.milestoneTitle}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(dispute.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 15.sp)

                                            if (dispute.evidenceNotes.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Evidence Notes: ${dispute.evidenceNotes}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }

                                            if (dispute.adminDecisionNotes.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = DealEmerald.copy(alpha = 0.12f),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = "Admin Decision: ${dispute.adminDecisionNotes}",
                                                        fontSize = 10.sp,
                                                        color = DealEmerald,
                                                        modifier = Modifier.padding(6.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Action Buttons for Admin Decision
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                if (dispute.status != "UNDER_REVIEW" && dispute.status != "RESOLVED" && dispute.status != "CLOSED") {
                                                    OutlinedButton(
                                                        onClick = {
                                                            viewModel.updateDisputeStatus(dispute.id, "UNDER_REVIEW", "Admin started milestone inspection.")
                                                        },
                                                        shape = RoundedCornerShape(8.dp),
                                                        modifier = Modifier.weight(1f).height(32.dp)
                                                    ) {
                                                        Text("Review", fontSize = 10.sp)
                                                    }
                                                }

                                                if (dispute.status != "RESOLVED") {
                                                    Button(
                                                        onClick = {
                                                            updatingDisputeId = dispute.id
                                                            targetDisputeStatus = "RESOLVED"
                                                            disputeNotesInput = "Milestone scope reconciled. Escrow distributed per contract terms."
                                                        },
                                                        shape = RoundedCornerShape(8.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = DealEmerald, contentColor = Color.Black),
                                                        modifier = Modifier.weight(1f).height(32.dp)
                                                    ) {
                                                        Text("Resolve", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                if (dispute.status != "CLOSED") {
                                                    OutlinedButton(
                                                        onClick = {
                                                            updatingDisputeId = dispute.id
                                                            targetDisputeStatus = "CLOSED"
                                                            disputeNotesInput = "Dispute closed following mediation confirmation."
                                                        },
                                                        shape = RoundedCornerShape(8.dp),
                                                        modifier = Modifier.weight(1f).height(32.dp)
                                                    ) {
                                                        Text("Close", fontSize = 10.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Reports" -> {
                        // 16. USER REPORTING & SAFETY (Users, Projects, Messages, Reviews, Portfolios, Blocked users)
                        val filterCategories = listOf("All", "USER", "PROJECT", "MESSAGE", "REVIEW", "PORTFOLIO")
                        val filteredReports = if (reportCategoryFilter == "All") reports
                        else reports.filter { it.targetType.equals(reportCategoryFilter, ignoreCase = true) }

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Filter chips
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    filterCategories.forEach { cat ->
                                        FilterChip(
                                            selected = reportCategoryFilter == cat,
                                            onClick = { reportCategoryFilter = cat },
                                            label = { Text(cat, fontSize = 10.sp) }
                                        )
                                    }
                                }
                            }

                            // Blocked users section
                            item {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Blocked Users Roster (${blockedUsers.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        if (blockedUsers.isEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("No blocked accounts currently.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        } else {
                                            blockedUsers.forEach { b ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(b.blockedUserName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        Text("Reason: ${b.reason}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                    TextButton(
                                                        onClick = { viewModel.unblockUser(b.blockedUserId) }
                                                    ) {
                                                        Text("Unblock", fontSize = 10.sp, color = DealCyanPrimary)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (filteredReports.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        Text("No reported items in this category.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            } else {
                                items(filteredReports) { rep ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = DealRoseLight.copy(alpha = 0.18f)),
                                        border = BorderStroke(1.dp, DealRose.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Reported: ${rep.targetName} (${rep.targetType})",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DealRose
                                                )
                                                Text("ID: ${rep.targetId.takeLast(6)}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Reason: ${rep.reason}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = { viewModel.dismissReport(rep.id) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f).height(30.dp)
                                                ) {
                                                    Text("Dismiss", fontSize = 10.sp)
                                                }

                                                Button(
                                                    onClick = {
                                                        viewModel.blockUser(rep.targetId, rep.targetName, rep.reason)
                                                        viewModel.dismissReport(rep.id)
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = DealRose, contentColor = Color.White),
                                                    modifier = Modifier.weight(1f).height(30.dp)
                                                ) {
                                                    Text("Block & Enforce", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Verification" -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DealEmeraldLight.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, DealEmerald.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = DealEmerald, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = "Verification Queue: Inspect professional credentials, portfolio links, and review history.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            if (verificationRequests.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                        Text("No pending verification requests.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            } else {
                                items(verificationRequests) { req ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(req.fullName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                StatusChip(statusText = req.status)
                                            }
                                            Text("${req.professionalRole} • ${req.email}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("Skills: ${req.skills}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                            Text("Portfolio: ${req.portfolioUrl}", fontSize = 10.sp, color = DealCyanPrimary)

                                            if (req.status == "PENDING") {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Button(
                                                        onClick = { viewModel.approveVerification(req.id, req.designerUserId) },
                                                        shape = RoundedCornerShape(8.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = DealEmerald, contentColor = Color.Black),
                                                        modifier = Modifier.weight(1f).height(32.dp)
                                                    ) {
                                                        Text("Approve", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    OutlinedButton(
                                                        onClick = {
                                                            rejectingRequestId = req.id
                                                            rejectionReasonInput = "Please upload additional client portfolio live URLs."
                                                        },
                                                        shape = RoundedCornerShape(8.dp),
                                                        modifier = Modifier.weight(1f).height(32.dp)
                                                    ) {
                                                        Text("Reject", fontSize = 10.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Users" -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(users) { user ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(user.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                if (user.isVerified) VerifiedBadge(onClick = { viewModel.showVerificationInfoDialog.value = true })
                                            }
                                            Text("${user.role} • ${user.headline.ifBlank { user.email }}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                        }

                                        Button(
                                            onClick = { viewModel.toggleVerification(user.id, user.isVerified) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (user.isVerified) DealRose else DealEmerald,
                                                contentColor = Color.White
                                            ),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(if (user.isVerified) "Unverify" else "Verify", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to add decision for dispute
    if (updatingDisputeId != null) {
        AlertDialog(
            onDismissRequest = { updatingDisputeId = null },
            title = { Text("Update Dispute: $targetDisputeStatus") },
            text = {
                Column {
                    Text("Provide manual administrative decision notes explaining the milestone resolution:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = disputeNotesInput,
                        onValueChange = { disputeNotesInput = it },
                        label = { Text("Decision Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dId = updatingDisputeId ?: return@Button
                        viewModel.updateDisputeStatus(dId, targetDisputeStatus, disputeNotesInput)
                        updatingDisputeId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black)
                ) {
                    Text("Submit Decision", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { updatingDisputeId = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal to reject verification
    if (rejectingRequestId != null) {
        val req = verificationRequests.firstOrNull { it.id == rejectingRequestId }
        AlertDialog(
            onDismissRequest = { rejectingRequestId = null },
            title = { Text("Reject Verification") },
            text = {
                Column {
                    Text("Please explain why this verification request was not approved:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectionReasonInput,
                        onValueChange = { rejectionReasonInput = it },
                        label = { Text("Reason for Rejection") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rId = rejectingRequestId ?: return@Button
                        val desId = req?.designerUserId ?: ""
                        viewModel.rejectVerification(rId, desId, rejectionReasonInput)
                        rejectingRequestId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DealRose, contentColor = Color.White)
                ) {
                    Text("Reject Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectingRequestId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminKpiCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
fun AdminChartCard(
    title: String,
    subtitle: String,
    labels: List<String>,
    values: List<Int>,
    gradient: List<Color>
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Total: ${values.sum()}", fontSize = 11.sp, color = gradient.first(), fontWeight = FontWeight.Bold)
            }
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(10.dp))

            val maxVal = values.maxOrNull()?.toFloat() ?: 100f

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { i, count ->
                    val fraction = (count / maxVal).coerceIn(0.15f, 1f)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$count",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(Brush.verticalGradient(gradient))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = labels.getOrElse(i) { "" },
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
