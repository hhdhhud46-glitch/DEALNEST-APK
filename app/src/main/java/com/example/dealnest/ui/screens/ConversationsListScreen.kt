package com.example.dealnest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dealnest.model.Conversation
import com.example.dealnest.model.Project
import com.example.dealnest.model.User
import com.example.dealnest.model.UserRole
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.AvatarImage
import com.example.dealnest.ui.components.EmptyPlaceholder
import com.example.dealnest.ui.components.StatusChip
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConversationsListScreen(
    viewModel: DealNestViewModel,
    conversations: List<Conversation>,
    currentUser: User,
    onSelectConversation: (Conversation) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, UNREAD, DEALS, PROPOSALS
    var showNewChatDialog by remember { mutableStateOf(false) }

    val designers by viewModel.allDesigners.collectAsState()
    val projects by viewModel.allProjects.collectAsState()

    // Filter conversations
    val filteredConversations = remember(conversations, searchQuery, selectedFilter, currentUser) {
        conversations.filter { conv ->
            val otherUserName = if (currentUser.id == conv.clientUserId) conv.designerName else conv.clientName
            val matchesSearch = searchQuery.isBlank() ||
                otherUserName.contains(searchQuery, ignoreCase = true) ||
                conv.projectTitle.contains(searchQuery, ignoreCase = true) ||
                conv.lastMessageText.contains(searchQuery, ignoreCase = true)

            val unreadCount = if (currentUser.id == conv.clientUserId) conv.unreadCountClient else conv.unreadCountDesigner

            val matchesFilter = when (selectedFilter) {
                "UNREAD" -> unreadCount > 0
                "DEALS" -> conv.dealId != null
                "PROPOSALS" -> conv.dealId == null
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    val totalUnread = remember(conversations, currentUser) {
        conversations.sumOf { if (currentUser.id == it.clientUserId) it.unreadCountClient else it.unreadCountDesigner }
    }

    Box(modifier = modifier.fillMaxSize().testTag("conversations_list_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Strip
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Messages & Chats",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (totalUnread > 0) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DealCyanPrimary,
                                    modifier = Modifier.testTag("unread_total_pill")
                                ) {
                                    Text(
                                        text = "$totalUnread unread",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { showNewChatDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DealCyanPrimary)
                                .testTag("btn_new_chat")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddComment,
                                contentDescription = "New Chat",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name, project, or message...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DealCyanPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("conversations_search_field")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Chips
                    val filters = listOf(
                        "ALL" to "All Chats",
                        "UNREAD" to "Unread ($totalUnread)",
                        "DEALS" to "Active Deals",
                        "PROPOSALS" to "Proposals"
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { (key, label) ->
                            val isSelected = selectedFilter == key
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = key },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DealCyanPrimary,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.testTag("chat_filter_$key")
                            )
                        }
                    }
                }
            }

            // Conversations List
            if (filteredConversations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyPlaceholder(
                        icon = Icons.Outlined.Forum,
                        title = if (searchQuery.isNotBlank()) "No matching conversations" else "No messages yet",
                        subtitle = if (searchQuery.isNotBlank()) "Try a different search query or filter." else "Reach out to top website designers or submit a proposal to start chatting."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 76.dp)
                        .testTag("conversations_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredConversations, key = { it.id }) { conv ->
                        ConversationItemCard(
                            conversation = conv,
                            currentUser = currentUser,
                            onClick = { onSelectConversation(conv) }
                        )
                    }
                }
            }
        }
    }

    // New Chat Dialog
    if (showNewChatDialog) {
        NewChatDialog(
            designers = designers,
            projects = projects,
            currentUser = currentUser,
            onStartChat = { designer, project ->
                viewModel.startChatWithDesigner(designer, project)
                showNewChatDialog = false
            },
            onDismiss = { showNewChatDialog = false }
        )
    }
}

@Composable
fun ConversationItemCard(
    conversation: Conversation,
    currentUser: User,
    onClick: () -> Unit
) {
    val isClient = currentUser.id == conversation.clientUserId
    val otherUserName = if (isClient) conversation.designerName else conversation.clientName
    val otherUserHeadline = if (isClient) conversation.designerHeadline else "Project Client"
    val otherUserRole = if (isClient) "Designer" else "Client"
    val unreadCount = if (isClient) conversation.unreadCountClient else conversation.unreadCountDesigner
    val isOnline = if (isClient) conversation.isDesignerOnline else conversation.isClientOnline
    val lastSeen = if (isClient) conversation.designerLastSeen else conversation.clientLastSeen

    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val formattedTime = timeFormat.format(Date(conversation.lastMessageTimestamp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unreadCount > 0) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (unreadCount > 0) 3.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("conversation_item_${conversation.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Photo with Online Indicator
            Box(contentAlignment = Alignment.BottomEnd) {
                AvatarImage(
                    name = otherUserName,
                    size = 48.dp
                )

                // Online indicator dot
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) DealEmerald else Color.Gray)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Name and Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = otherUserName,
                            fontSize = 14.sp,
                            fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isClient) DealCyanLight.copy(alpha = 0.2f) else DealIndigoLight.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = otherUserRole,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isClient) DealCyanPrimary else DealIndigo,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (unreadCount > 0) DealCyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Project Tag & Deal Status Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = conversation.projectTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DealCyanPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (conversation.dealId != null) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DealEmeraldLight.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "Deal Active",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = DealEmerald,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Last Message & Unread Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (conversation.lastMessageSenderId == currentUser.id) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Sent",
                                tint = DealCyanPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = conversation.lastMessageText.ifBlank { "Conversation started" },
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(DealCyanPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unreadCount.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewChatDialog(
    designers: List<User>,
    projects: List<Project>,
    currentUser: User,
    onStartChat: (User, Project?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDesigner by remember { mutableStateOf<User?>(designers.firstOrNull()) }
    var selectedProject by remember { mutableStateOf<Project?>(projects.firstOrNull()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .testTag("new_chat_dialog")
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
                    Text("Start Private Chat", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Select Collaborator", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(designers) { designer ->
                        val isSelected = selectedDesigner?.id == designer.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) DealCyanPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, DealCyanPrimary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDesigner = designer }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AvatarImage(name = designer.name, size = 36.dp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(designer.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(designer.headline, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val des = selectedDesigner
                        if (des != null) {
                            onStartChat(des, selectedProject)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    enabled = selectedDesigner != null,
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Start Conversation", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
