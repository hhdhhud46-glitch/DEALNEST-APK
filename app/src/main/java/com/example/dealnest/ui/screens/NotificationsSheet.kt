package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.dealnest.model.AppNotification
import com.example.dealnest.ui.DealNestViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsSheet(
    viewModel: DealNestViewModel,
    notifications: List<AppNotification>,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var showSettings by remember { mutableStateOf(false) }

    // Categories: Messages, Projects, Proposals, Deals, Milestones, Reviews, Verification, AI Matches, Payments, Disputes
    val categories = listOf(
        "All" to "All",
        "MESSAGE" to "Messages",
        "PROJECT" to "Projects",
        "PROPOSAL" to "Proposals",
        "DEAL" to "Deals",
        "MILESTONE" to "Milestones",
        "REVIEW" to "Reviews",
        "VERIFICATION" to "Verification",
        "MATCH" to "AI Matches",
        "PAYMENT" to "Payments",
        "DISPUTE" to "Disputes"
    )

    val filteredNotifications = remember(notifications, selectedCategory) {
        if (selectedCategory == "All") notifications
        else notifications.filter { it.type.equals(selectedCategory, ignoreCase = true) }
    }

    val unreadCount = remember(notifications) {
        notifications.count { !it.isRead }
    }

    val prefs by viewModel.notificationPreferences.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("notifications_dialog")
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
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Notifications", fontSize = 18.sp, fontWeight = FontWeight.Black)
                            if (unreadCount > 0) {
                                Surface(
                                    shape = CircleShape,
                                    color = DealCyanPrimary
                                ) {
                                    Text(
                                        text = "$unreadCount new",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (showSettings) "Manage your notification preferences" else "Smart alerts, deals, matches & milestones",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showSettings = !showSettings },
                            modifier = Modifier.testTag("notification_settings_btn")
                        ) {
                            Icon(
                                imageVector = if (showSettings) Icons.Default.Notifications else Icons.Default.Tune,
                                contentDescription = "Settings",
                                tint = if (showSettings) DealCyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (showSettings) {
                    // Notification Settings View
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "NOTIFICATION PREFERENCES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DealCyanPrimary,
                            letterSpacing = 1.sp
                        )

                        var mMsg by remember { mutableStateOf(prefs.messages) }
                        var mProj by remember { mutableStateOf(prefs.projects) }
                        var mProp by remember { mutableStateOf(prefs.proposals) }
                        var mDeal by remember { mutableStateOf(prefs.deals) }
                        var mMilestone by remember { mutableStateOf(prefs.milestones) }
                        var mReview by remember { mutableStateOf(prefs.reviews) }
                        var mVerif by remember { mutableStateOf(prefs.verification) }
                        var mMatch by remember { mutableStateOf(prefs.aiMatches) }
                        var mPay by remember { mutableStateOf(prefs.payments) }
                        var mDisp by remember { mutableStateOf(prefs.disputes) }

                        val settingItems = listOf(
                            Triple("Client & Designer Messages", "New chat pings and direct inquiries", mMsg) to { v: Boolean -> mMsg = v },
                            Triple("New Matching Projects", "Lead alerts matching your skills & budget", mProj) to { v: Boolean -> mProj = v },
                            Triple("Proposals & Offers", "Updates on received or submitted proposals", mProp) to { v: Boolean -> mProp = v },
                            Triple("Deals & Workspace", "Deal acceptances, status advancements & file drops", mDeal) to { v: Boolean -> mDeal = v },
                            Triple("Milestone Progress", "Submission and approval of delivery milestones", mMilestone) to { v: Boolean -> mMilestone = v },
                            Triple("Reviews & Ratings", "Feedback and ratings left on completed contracts", mReview) to { v: Boolean -> mReview = v },
                            Triple("Verification Status", "Badges and trust verification status", mVerif) to { v: Boolean -> mVerif = v },
                            Triple("AI Smart Matches", "High-compatibility project-to-designer matches", mMatch) to { v: Boolean -> mMatch = v },
                            Triple("Payments & Escrow", "Milestone deposits, escrow releases and transactions", mPay) to { v: Boolean -> mPay = v },
                            Triple("Disputes & Resolutions", "Mediation alerts and admin decisions", mDisp) to { v: Boolean -> mDisp = v }
                        )

                        settingItems.forEach { (info, toggle) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(info.first, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text(info.second, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(
                                        checked = info.third,
                                        onCheckedChange = { toggle(it) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = DealCyanPrimary)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.notificationPreferences.value = prefs.copy(
                                    messages = mMsg,
                                    projects = mProj,
                                    proposals = mProp,
                                    deals = mDeal,
                                    milestones = mMilestone,
                                    reviews = mReview,
                                    verification = mVerif,
                                    aiMatches = mMatch,
                                    payments = mPay,
                                    disputes = mDisp
                                )
                                viewModel.emitSnackbar("Notification preferences updated successfully!")
                                showSettings = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().height(42.dp).testTag("save_notif_prefs_btn")
                        ) {
                            Text("Save Notification Preferences", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Actions Strip: Mark All Read
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Filter Chips
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.forEach { (catKey, label) ->
                                FilterChip(
                                    selected = selectedCategory == catKey,
                                    onClick = { selectedCategory = catKey },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }

                        if (unreadCount > 0) {
                            TextButton(
                                onClick = { viewModel.markAllNotificationsRead() },
                                contentPadding = PaddingValues(horizontal = 6.dp),
                                modifier = Modifier.testTag("mark_all_read_btn")
                            ) {
                                Text("Mark All Read", fontSize = 11.sp, color = DealCyanPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredNotifications.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No notifications in this category", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredNotifications, key = { it.id }) { notif ->
                                val timeFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                                val formattedTime = timeFormat.format(Date(notif.timestamp))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (notif.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else DealCyanLight.copy(alpha = 0.18f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (notif.isRead) MaterialTheme.colorScheme.outline.copy(alpha = 0.25f) else DealCyanPrimary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.markNotificationRead(notif.id)
                                            when (notif.type) {
                                                "MESSAGE", "PROPOSAL" -> {
                                                    onDismiss()
                                                    viewModel.switchTab(com.example.dealnest.ui.AppTab.MESSAGES)
                                                }
                                                "DEAL", "MILESTONE", "PAYMENT", "DISPUTE" -> {
                                                    onDismiss()
                                                    viewModel.switchTab(com.example.dealnest.ui.AppTab.DEALS)
                                                }
                                                "MATCH", "PROJECT" -> {
                                                    onDismiss()
                                                    viewModel.switchTab(com.example.dealnest.ui.AppTab.PROJECTS)
                                                }
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = when (notif.type) {
                                                "PROPOSAL" -> Icons.Default.Assignment
                                                "DEAL" -> Icons.Default.Handshake
                                                "MILESTONE" -> Icons.Default.CheckCircle
                                                "MESSAGE" -> Icons.Default.Chat
                                                "MATCH" -> Icons.Default.AutoAwesome
                                                "REVIEW" -> Icons.Default.Star
                                                "VERIFICATION" -> Icons.Default.Verified
                                                "PAYMENT" -> Icons.Default.AccountBalanceWallet
                                                "DISPUTE" -> Icons.Default.Gavel
                                                else -> Icons.Default.Notifications
                                            },
                                            contentDescription = null,
                                            tint = when (notif.type) {
                                                "PROPOSAL" -> DealCyanPrimary
                                                "DEAL" -> DealEmerald
                                                "MILESTONE" -> Color(0xFF10B981)
                                                "MESSAGE" -> DealIndigo
                                                "REVIEW" -> Color(0xFFF59E0B)
                                                "VERIFICATION" -> DealCyanPrimary
                                                "PAYMENT" -> DealEmerald
                                                "DISPUTE" -> DealRose
                                                else -> DealAmber
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(notif.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                                Text(formattedTime, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(notif.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                                        }

                                        // Action Icons: Mark Read & Delete
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (!notif.isRead) {
                                                IconButton(
                                                    onClick = { viewModel.markNotificationRead(notif.id) },
                                                    modifier = Modifier.size(28.dp).testTag("mark_read_btn_${notif.id}")
                                                ) {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = "Mark as read",
                                                        tint = DealCyanPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteNotification(notif.id) },
                                                modifier = Modifier.size(28.dp).testTag("delete_notif_btn_${notif.id}")
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Delete,
                                                    contentDescription = "Delete notification",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
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
    }
}
