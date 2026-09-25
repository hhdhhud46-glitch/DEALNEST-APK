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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dealnest.model.Deal
import com.example.dealnest.model.DealStatus
import com.example.dealnest.model.User
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.EmptyPlaceholder
import com.example.dealnest.ui.components.StatusChip
import com.example.ui.theme.*

@Composable
fun DealRoomScreen(
    viewModel: DealNestViewModel,
    deals: List<Deal>,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    var selectedFilterTab by remember { mutableStateOf("Active") } // Active vs Completed
    val selectedDeal by viewModel.selectedDealForDetail.collectAsState()
    val allDisputes by viewModel.allDisputes.collectAsState()

    val displayDeals = remember(deals, selectedFilterTab) {
        if (selectedFilterTab == "Active") {
            deals.filter { it.status != DealStatus.COMPLETED.name }
        } else {
            deals.filter { it.status == DealStatus.COMPLETED.name }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("deal_room_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Deal Room",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Private workspaces for active client & designer contracts",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.showPaymentsDialog.value = true },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DealCyanPrimary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp).testTag("deal_room_payments_btn")
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Payments", fontSize = 11.sp, color = DealCyanPrimary, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DealEmeraldLight.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, DealEmerald)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = DealEmerald, modifier = Modifier.size(12.dp))
                                Text("Escrow", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DealEmerald)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Active", "Completed").forEach { tab ->
                        val isSelected = selectedFilterTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DealCyanPrimary else Color.Transparent)
                                .clickable { selectedFilterTab = tab }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Deal Cards List
        if (displayDeals.isEmpty()) {
            item {
                EmptyPlaceholder(
                    icon = Icons.Outlined.Handshake,
                    title = "No $selectedFilterTab Deals",
                    subtitle = if (selectedFilterTab == "Active")
                        "When you accept a project proposal, your private deal workspace will appear here."
                    else "Completed project deals and reviews will be stored here."
                )
            }
        } else {
            items(displayDeals, key = { it.id }) { deal ->
                val canReview = deal.status == DealStatus.COMPLETED.name && viewModel.canUserReviewDeal(deal, currentUser.id)
                val isCompleted = deal.status == DealStatus.COMPLETED.name
                val isAlreadyReviewed = isCompleted && !canReview && (currentUser.id == deal.clientUserId || currentUser.id == deal.designerUserId)

                DealCard(
                    deal = deal,
                    canReview = canReview,
                    isAlreadyReviewed = isAlreadyReviewed,
                    onOpenChat = {
                        viewModel.selectDealForChat(deal.id)
                    },
                    onAdvanceMilestone = {
                        viewModel.advanceDealMilestone(deal)
                    },
                    onChangeStatus = { newStatus ->
                        viewModel.changeDealStatus(deal, newStatus)
                    },
                    onAttachFile = {
                        viewModel.addSharedFile(deal, "Mockup_Asset_rev${(1..9).random()}.zip")
                    },
                    onLeaveReview = {
                        viewModel.selectedDealForReview.value = deal
                        viewModel.showReviewDialog.value = true
                    },
                    onGenerateInvoice = {
                        viewModel.openInvoiceForDeal(deal)
                    },
                    onOpenDispute = {
                        viewModel.openDisputeForDeal(deal)
                    }
                )
            }
        }
    }
}

@Composable
fun DealCard(
    deal: Deal,
    canReview: Boolean = false,
    isAlreadyReviewed: Boolean = false,
    onOpenChat: () -> Unit,
    onAdvanceMilestone: () -> Unit,
    onChangeStatus: (DealStatus) -> Unit,
    onAttachFile: () -> Unit,
    onLeaveReview: () -> Unit,
    onGenerateInvoice: () -> Unit,
    onOpenDispute: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .testTag("deal_card_${deal.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Deal Title & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(statusText = deal.status)

                Text(
                    text = "$${deal.agreedPrice.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = DealEmerald
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = deal.projectTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Parties involved
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = DealIndigo, modifier = Modifier.size(14.dp))
                    Text("Client: ${deal.clientName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(14.dp))
                    Text("Designer: ${deal.designerName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Status Stepper: Pending -> In Progress -> Review -> Completed
            DealStatusStepper(currentStatus = deal.status, onStatusSelect = onChangeStatus)

            Spacer(modifier = Modifier.height(14.dp))

            // Milestones Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Milestones Progress",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "${deal.completedMilestonesCount} / ${deal.totalMilestonesCount} Done",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DealCyanPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val progressFraction = deal.completedMilestonesCount.toFloat() / deal.totalMilestonesCount.toFloat()
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = DealCyanPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = deal.milestonesSummary,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    if (deal.completedMilestonesCount < deal.totalMilestonesCount) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onAdvanceMilestone,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp).align(Alignment.End)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Complete Next Milestone", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Shared Files Box
            if (deal.sharedFiles.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(14.dp))
                        Text("Files: ${deal.sharedFiles}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }

                    TextButton(onClick = onAttachFile) {
                        Text("+ Add File", fontSize = 11.sp, color = DealCyanPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Actions: Invoice Generator (Feature 12) & Dispute System (Feature 14)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onGenerateInvoice,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("deal_invoice_btn_${deal.id}")
                ) {
                    Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (deal.status == DealStatus.COMPLETED.name) "Invoice (Paid)" else "Invoice Preview",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                OutlinedButton(
                    onClick = onOpenDispute,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DealRose),
                    border = BorderStroke(1.dp, DealRose.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("deal_dispute_btn_${deal.id}")
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = DealRose, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Open Dispute", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = DealRose)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Open Chat, Review, Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenChat,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DealCyanPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (canReview) {
                    Button(
                        onClick = onLeaveReview,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DealAmber, contentColor = Color.Black),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("write_review_button")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Write Review", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (isAlreadyReviewed) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DealEmeraldLight.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, DealEmerald.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DealEmerald, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reviewed ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DealEmerald)
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = onLeaveReview,
                        enabled = deal.status == DealStatus.COMPLETED.name,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Icon(Icons.Default.StarBorder, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Review Deal", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun DealStatusStepper(
    currentStatus: String,
    onStatusSelect: (DealStatus) -> Unit
) {
    val statuses = listOf(
        DealStatus.PENDING,
        DealStatus.IN_PROGRESS,
        DealStatus.REVIEW,
        DealStatus.COMPLETED
    )

    val currentIndex = statuses.indexOfFirst { it.name == currentStatus }.let { if (it == -1) 1 else it }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        statuses.forEachIndexed { index, status ->
            val isPassedOrCurrent = index <= currentIndex
            val isCurrent = index == currentIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onStatusSelect(status) }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCurrent) DealCyanPrimary
                            else if (isPassedOrCurrent) DealEmerald
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPassedOrCurrent && !isCurrent) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    } else {
                        Text(
                            text = "${index + 1}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = when (status) {
                        DealStatus.PENDING -> "Pending"
                        DealStatus.IN_PROGRESS -> "In Progress"
                        DealStatus.REVIEW -> "Review"
                        DealStatus.COMPLETED -> "Done"
                    },
                    fontSize = 9.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) DealCyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
