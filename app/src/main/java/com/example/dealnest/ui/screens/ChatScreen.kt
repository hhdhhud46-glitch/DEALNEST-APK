package com.example.dealnest.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dealnest.model.*
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.AvatarImage
import com.example.dealnest.ui.components.EmptyPlaceholder
import com.example.dealnest.ui.components.StatusChip
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: DealNestViewModel,
    currentUser: User,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val conversation by viewModel.activeConversation.collectAsState()
    val messages by viewModel.currentChatMessages.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val allDeals by viewModel.allDeals.collectAsState()

    // Active project & deal matching conversation
    val project = remember(conversation, allProjects) {
        allProjects.firstOrNull { it.id == conversation?.projectId }
    }
    val deal = remember(conversation, allDeals) {
        allDeals.firstOrNull { it.id == conversation?.dealId }
    }

    var inputText by remember { mutableStateOf("") }
    var selectedAttachmentName by remember { mutableStateOf<String?>(null) }
    var selectedAttachmentType by remember { mutableStateOf<String?>(null) } // "FILE", "IMAGE", "FIGMA"

    // Search inside chat
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Message action sheet
    var selectedActionMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var showMessageActionSheet by remember { mutableStateOf(false) }

    // Emoji Bar Toggle
    var showEmojiBar by remember { mutableStateOf(false) }

    // Attachment Picker Dialog
    var showAttachmentPicker by remember { mutableStateOf(false) }

    // Safety & Guidelines Sheet
    var showCommunityGuidelines by remember { mutableStateOf(false) }
    var showSafetyScamWarning by remember { mutableStateOf(true) }

    // Milestones Quick Dialog
    var showMilestonesDialog by remember { mutableStateOf(false) }

    // AI Assistant Bottom Sheet
    var showAiAssistant by remember { mutableStateOf(false) }

    // AI Summary Dialog (Feature 8)
    var showAiSummaryDialog by remember { mutableStateOf(false) }

    // AI Translation (Feature 9)
    var targetTranslateLanguage by remember { mutableStateOf(SupportedLanguage.HINDI) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    val translatedMessages = remember { mutableStateMapOf<String, String>() }
    val hideOriginalMap = remember { mutableStateMapOf<String, Boolean>() }

    // Replying message state
    val replyingTo by viewModel.replyingToMessage.collectAsState()

    // Scroll to bottom on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (conversation == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EmptyPlaceholder(
                icon = Icons.Outlined.Forum,
                title = "No conversation selected",
                subtitle = "Choose a conversation from your Messages tab to start chatting."
            )
        }
        return
    }

    val conv = conversation!!
    val isClient = currentUser.id == conv.clientUserId
    val otherUserName = if (isClient) conv.designerName else conv.clientName
    val otherUserRole = if (isClient) "Designer" else "Client"
    val otherUserId = if (isClient) conv.designerUserId else conv.clientUserId
    val isOtherUserBlocked = remember(blockedUsers, otherUserId) {
        blockedUsers.any { it.blockedUserId == otherUserId }
    }
    val isOnline = if (isClient) conv.isDesignerOnline else conv.isClientOnline
    val lastSeenText = if (isClient) conv.designerLastSeen else conv.clientLastSeen

    // Filter messages for search
    val displayedMessages = remember(messages, searchQuery) {
        if (searchQuery.isBlank()) messages
        else messages.filter { it.text.contains(searchQuery, ignoreCase = true) || it.senderName.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_screen")
    ) {
        // 1. WHATSAPP-STYLE APP BAR
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(38.dp).testTag("chat_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }

                    // Avatar with online dot
                    Box(
                        modifier = Modifier.padding(end = 10.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        AvatarImage(name = otherUserName, size = 38.dp)
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) DealEmerald else Color.Gray)
                                .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        )
                    }

                    // Name, Role & Status
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (project != null) {
                                    viewModel.openProjectFromChat(project.id)
                                }
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = otherUserName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isClient) DealCyanLight.copy(alpha = 0.2f) else DealIndigoLight.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = otherUserRole,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isClient) DealCyanPrimary else DealIndigo,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isOnline) "Online" else lastSeenText,
                            fontSize = 11.sp,
                            color = if (isOnline) DealEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Top Action Icons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Search in chat
                        IconButton(
                            onClick = { isSearchActive = !isSearchActive },
                            modifier = Modifier.size(36.dp).testTag("chat_toggle_search_btn")
                        ) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search Messages",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // AI Summary Button (Feature 8)
                        IconButton(
                            onClick = { showAiSummaryDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("chat_ai_summary_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Summarize,
                                contentDescription = "AI Summary",
                                tint = DealCyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // AI Assistant Sparkle Button
                        IconButton(
                            onClick = { showAiAssistant = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("chat_ai_assistant_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Assistant",
                                tint = DealCyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // More options / safety menu
                        var showMoreMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(
                                onClick = { showMoreMenu = true },
                                modifier = Modifier.size(36.dp).testTag("chat_more_menu_btn")
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(20.dp))
                            }

                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("View Project Details") },
                                    leadingIcon = { Icon(Icons.Outlined.Visibility, contentDescription = null) },
                                    onClick = {
                                        showMoreMenu = false
                                        if (project != null) {
                                            viewModel.openProjectFromChat(project.id)
                                        } else {
                                            viewModel.emitSnackbar("Project details: ${conv.projectTitle}")
                                        }
                                    }
                                )

                                if (conv.dealId != null) {
                                    DropdownMenuItem(
                                        text = { Text("View Deal Room") },
                                        leadingIcon = { Icon(Icons.Outlined.Handshake, contentDescription = null) },
                                        onClick = {
                                            showMoreMenu = false
                                            viewModel.openDealRoomFromChat(conv.dealId)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Deal Milestones") },
                                        leadingIcon = { Icon(Icons.Outlined.Checklist, contentDescription = null) },
                                        onClick = {
                                            showMoreMenu = false
                                            showMilestonesDialog = true
                                        }
                                    )
                                }

                                HorizontalDivider()

                                DropdownMenuItem(
                                    text = { Text("Community Guidelines") },
                                    leadingIcon = { Icon(Icons.Outlined.GppGood, contentDescription = null) },
                                    onClick = {
                                        showMoreMenu = false
                                        showCommunityGuidelines = true
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text(if (isOtherUserBlocked) "Unblock User" else "Block User") },
                                    leadingIcon = { Icon(Icons.Outlined.Block, contentDescription = null, tint = DealRose) },
                                    onClick = {
                                        showMoreMenu = false
                                        if (isOtherUserBlocked) {
                                            viewModel.unblockUser(otherUserId)
                                        } else {
                                            viewModel.blockUser(otherUserId, otherUserName, "Inappropriate chat behavior")
                                        }
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Report User") },
                                    leadingIcon = { Icon(Icons.Outlined.Flag, contentDescription = null, tint = DealRose) },
                                    onClick = {
                                        showMoreMenu = false
                                        viewModel.submitReport("USER", otherUserId, otherUserName, "Chat conduct violation")
                                    }
                                )
                            }
                        }
                    }
                }

                // In-Chat Search Bar if active
                AnimatedVisibility(visible = isSearchActive) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(16.dp))
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Find in conversation...", fontSize = 12.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f).height(42.dp)
                            )
                            Text(
                                text = "${displayedMessages.size} found",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 2. PROJECT CONTEXT BANNER
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = conv.projectTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (conv.dealId != null) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DealEmeraldLight.copy(alpha = 0.25f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(DealEmerald))
                                        Text("Deal Active", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DealEmerald)
                                    }
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text("Budget: ${conv.budget}", fontSize = 11.sp, color = DealEmerald, fontWeight = FontWeight.SemiBold)
                            Text("•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Deadline: ${conv.deadline}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // "View Project" Button
                        FilledTonalButton(
                            onClick = {
                                if (project != null) {
                                    viewModel.openProjectFromChat(project.id)
                                } else {
                                    viewModel.emitSnackbar("Viewing project details for '${conv.projectTitle}'")
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("chat_view_project_btn")
                        ) {
                            Text("Project", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        // "View Deal" Button (if Deal active)
                        if (conv.dealId != null) {
                            Button(
                                onClick = { viewModel.openDealRoomFromChat(conv.dealId) },
                                colors = ButtonDefaults.buttonColors(containerColor = DealEmerald, contentColor = Color.Black),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("chat_view_deal_btn")
                            ) {
                                Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Deal", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. SCAM WARNING BANNER (Dismissible)
        if (showSafetyScamWarning) {
            Surface(
                color = DealAmberLight.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = DealAmber, modifier = Modifier.size(14.dp))
                        Text(
                            text = "DealNest Escrow Protection: Never pay or transfer money off-platform.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = { showSafetyScamWarning = false },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(13.dp))
                    }
                }
            }
        }

        // 4. QUICK ACTIONS ROW
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    AssistChip(
                        onClick = { showAiSummaryDialog = true },
                        leadingIcon = { Icon(Icons.Outlined.Summarize, contentDescription = null, modifier = Modifier.size(13.dp), tint = DealCyanPrimary) },
                        label = { Text("AI Summary", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary) },
                        modifier = Modifier.testTag("chip_ai_summary")
                    )
                }
                item {
                    Box {
                        AssistChip(
                            onClick = { showLanguageMenu = true },
                            leadingIcon = { Icon(Icons.Outlined.Translate, contentDescription = null, modifier = Modifier.size(13.dp), tint = DealEmerald) },
                            label = { Text("Translate: ${targetTranslateLanguage.displayName}", fontSize = 11.sp) },
                            modifier = Modifier.testTag("chip_translate_language")
                        )

                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            Text(
                                text = "Translate to:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                            SupportedLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.displayName} (${lang.nativeName})", fontSize = 12.sp) },
                                    onClick = {
                                        targetTranslateLanguage = lang
                                        showLanguageMenu = false
                                        viewModel.emitSnackbar("Translation target set to ${lang.displayName}")
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    AssistChip(
                        onClick = { inputText = "Hi! Can we discuss the budget and payment milestones for ${conv.projectTitle}?" },
                        leadingIcon = { Icon(Icons.Outlined.Payments, contentDescription = null, modifier = Modifier.size(13.dp), tint = DealEmerald) },
                        label = { Text("Discuss Price", fontSize = 11.sp) }
                    )
                }
                item {
                    AssistChip(
                        onClick = { inputText = "Could we clarify the core requirements, page structure, and design style you're looking for?" },
                        leadingIcon = { Icon(Icons.Outlined.FormatListBulleted, contentDescription = null, modifier = Modifier.size(13.dp), tint = DealCyanPrimary) },
                        label = { Text("Discuss Requirements", fontSize = 11.sp) }
                    )
                }
                item {
                    AssistChip(
                        onClick = { inputText = "Let's review the timeline and milestone delivery dates for this build." },
                        leadingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null, modifier = Modifier.size(13.dp), tint = DealAmber) },
                        label = { Text("Discuss Deadline", fontSize = 11.sp) }
                    )
                }
                if (currentUser.role == UserRole.DESIGNER.name && conv.dealId == null) {
                    item {
                        AssistChip(
                            onClick = {
                                if (project != null) {
                                    viewModel.selectedProjectForDetail.value = project
                                    viewModel.showSendProposalDialog.value = true
                                }
                            },
                            leadingIcon = { Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(13.dp), tint = DealCyanPrimary) },
                            label = { Text("Send Proposal", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
                if (conv.dealId != null) {
                    item {
                        AssistChip(
                            onClick = { showMilestonesDialog = true },
                            leadingIcon = { Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(13.dp), tint = DealEmerald) },
                            label = { Text("Milestones", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
                item {
                    AssistChip(
                        onClick = {
                            val des = viewModel.allDesigners.value.firstOrNull { it.id == conv.designerUserId }
                            if (des != null) {
                                viewModel.openDesignerPortfolio(des)
                            } else {
                                viewModel.emitSnackbar("Opening designer portfolio showcase")
                            }
                        },
                        leadingIcon = { Icon(Icons.Outlined.Palette, contentDescription = null, modifier = Modifier.size(13.dp), tint = DealIndigo) },
                        label = { Text("View Portfolio", fontSize = 11.sp) }
                    )
                }
            }
        }

        // 5. MESSAGES FEED
        if (displayedMessages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                EmptyPlaceholder(
                    icon = Icons.Outlined.Chat,
                    title = if (searchQuery.isNotBlank()) "No messages match '$searchQuery'" else "Conversation Started",
                    subtitle = if (searchQuery.isNotBlank()) "Try another search term." else "Say hello to $otherUserName! Discuss requirements, budgets, or share files."
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(displayedMessages, key = { it.id }) { message ->
                    val isMe = message.senderUserId == currentUser.id
                    ChatMessageBubble(
                        message = message,
                        isMe = isMe,
                        highlightQuery = searchQuery,
                        translatedText = translatedMessages[message.id],
                        targetLanguageName = targetTranslateLanguage.displayName,
                        hideOriginal = hideOriginalMap[message.id] ?: false,
                        onToggleOriginal = {
                            hideOriginalMap[message.id] = !(hideOriginalMap[message.id] ?: false)
                        },
                        onTranslateQuickClick = {
                            val translated = com.example.dealnest.util.AiChatHelper.translateText(message.text, targetTranslateLanguage)
                            translatedMessages[message.id] = translated
                            viewModel.emitSnackbar("Translated to ${targetTranslateLanguage.displayName}")
                        },
                        onLongClick = {
                            selectedActionMessage = message
                            showMessageActionSheet = true
                        }
                    )
                }
            }
        }

        // Blocked User Notice if blocked
        if (isOtherUserBlocked) {
            Surface(
                color = DealRose.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "You have blocked $otherUserName.",
                        fontSize = 12.sp,
                        color = DealRose,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.unblockUser(otherUserId) }) {
                        Text("Unblock", color = DealRose, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 6. QUICK EMOJI BAR
        if (showEmojiBar) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                val quickEmojis = listOf("👍", "🚀", "💡", "🔥", "🙌", "🎨", "👏", "✅", "🎉", "🤝")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    quickEmojis.forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .clickable {
                                    inputText += emoji
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        // 7. QUOTED REPLY PREVIEW
        if (replyingTo != null) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, DealCyanPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(16.dp))
                        Column {
                            Text(
                                text = "Replying to ${replyingTo!!.senderName}:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DealCyanPrimary
                            )
                            Text(
                                text = replyingTo!!.text,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.replyingToMessage.value = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel Reply", modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // 8. ATTACHMENT PREVIEW TAG
        if (selectedAttachmentName != null) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = if (selectedAttachmentType == "IMAGE") Icons.Default.Image else Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            tint = DealCyanPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = selectedAttachmentName ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DealCyanPrimary
                        )
                    }
                    IconButton(
                        onClick = {
                            selectedAttachmentName = null
                            selectedAttachmentType = null
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // 9. STICKY INPUT BAR (WhatsApp style, keyboard friendly)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 76.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Emoji Toggle Button
                IconButton(
                    onClick = { showEmojiBar = !showEmojiBar },
                    modifier = Modifier.size(38.dp).testTag("chat_emoji_btn")
                ) {
                    Icon(
                        imageVector = if (showEmojiBar) Icons.Default.Keyboard else Icons.Outlined.SentimentSatisfiedAlt,
                        contentDescription = "Emojis",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Attach File / Image Button
                IconButton(
                    onClick = { showAttachmentPicker = true },
                    modifier = Modifier.size(38.dp).testTag("chat_attach_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach File or Image",
                        tint = DealCyanPrimary
                    )
                }

                // Message Text Field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Type a message or proposal...", fontSize = 13.sp) },
                    singleLine = false,
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DealCyanPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                // Send Button
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() || selectedAttachmentName != null) {
                            viewModel.sendChatMessage(
                                text = inputText,
                                attachmentName = selectedAttachmentName,
                                attachmentType = selectedAttachmentType
                            )
                            inputText = ""
                            selectedAttachmentName = null
                            selectedAttachmentType = null
                        }
                    },
                    enabled = (inputText.isNotBlank() || selectedAttachmentName != null) && !isOtherUserBlocked,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if ((inputText.isNotBlank() || selectedAttachmentName != null) && !isOtherUserBlocked)
                                DealCyanPrimary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if ((inputText.isNotBlank() || selectedAttachmentName != null) && !isOtherUserBlocked)
                            Color.Black
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // Attachment Picker Dialog
    if (showAttachmentPicker) {
        AttachmentPickerDialog(
            onSelect = { name, type ->
                selectedAttachmentName = name
                selectedAttachmentType = type
                showAttachmentPicker = false
            },
            onDismiss = { showAttachmentPicker = false }
        )
    }

    // Message Actions Bottom Sheet (Copy, Reply, Delete, Report)
    if (showMessageActionSheet && selectedActionMessage != null) {
        val msg = selectedActionMessage!!
        val isMe = msg.senderUserId == currentUser.id

        ModalBottomSheet(
            onDismissRequest = {
                showMessageActionSheet = false
                selectedActionMessage = null
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Message Options",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg.text,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Copy
                ListItem(
                    headlineContent = { Text("Copy Text", fontWeight = FontWeight.SemiBold) },
                    leadingContent = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = DealCyanPrimary) },
                    modifier = Modifier.clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("DealNest Message", msg.text)
                        clipboard.setPrimaryClip(clip)
                        viewModel.emitSnackbar("Message text copied to clipboard")
                        showMessageActionSheet = false
                        selectedActionMessage = null
                    }
                )

                // Translate Message (Feature 9)
                ListItem(
                    headlineContent = { Text("Translate Message", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Translate to ${targetTranslateLanguage.displayName}") },
                    leadingContent = { Icon(Icons.Outlined.Translate, contentDescription = null, tint = DealEmerald) },
                    modifier = Modifier.clickable {
                        val translated = com.example.dealnest.util.AiChatHelper.translateText(msg.text, targetTranslateLanguage)
                        translatedMessages[msg.id] = translated
                        viewModel.emitSnackbar("Message translated to ${targetTranslateLanguage.displayName}")
                        showMessageActionSheet = false
                        selectedActionMessage = null
                    }
                )

                // Reply
                ListItem(
                    headlineContent = { Text("Reply", fontWeight = FontWeight.SemiBold) },
                    leadingContent = { Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = null, tint = DealCyanPrimary) },
                    modifier = Modifier.clickable {
                        viewModel.replyingToMessage.value = msg
                        showMessageActionSheet = false
                        selectedActionMessage = null
                    }
                )

                // Delete my message (only if sender is me)
                if (isMe) {
                    ListItem(
                        headlineContent = { Text("Delete Message", color = DealRose, fontWeight = FontWeight.SemiBold) },
                        leadingContent = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = DealRose) },
                        modifier = Modifier.clickable {
                            viewModel.deleteChatMessage(msg)
                            showMessageActionSheet = false
                            selectedActionMessage = null
                        }
                    )
                }

                // Report message
                ListItem(
                    headlineContent = { Text("Report Message", color = DealRose, fontWeight = FontWeight.SemiBold) },
                    leadingContent = { Icon(Icons.Outlined.Flag, contentDescription = null, tint = DealRose) },
                    modifier = Modifier.clickable {
                        viewModel.submitReport("MESSAGE", msg.id, "Message from ${msg.senderName}", "Inappropriate or scam content")
                        showMessageActionSheet = false
                        selectedActionMessage = null
                    }
                )
            }
        }
    }

    // AI Assistant Sheet
    if (showAiAssistant) {
        AiChatAssistantSheet(
            conversation = conv,
            project = project,
            messages = messages,
            onInsertTextToChat = { textToInsert ->
                inputText = if (inputText.isBlank()) textToInsert else "$inputText\n$textToInsert"
            },
            onDismiss = { showAiAssistant = false }
        )
    }

    // Community Guidelines
    if (showCommunityGuidelines) {
        CommunityGuidelinesDialog(onDismiss = { showCommunityGuidelines = false })
    }

    // Milestones Quick Dialog
    if (showMilestonesDialog && deal != null) {
        MilestonesQuickDialog(
            deal = deal,
            viewModel = viewModel,
            onDismiss = { showMilestonesDialog = false }
        )
    }

    // AI Conversation Summary Dialog (Feature 8)
    if (showAiSummaryDialog) {
        AiConversationSummaryDialog(
            conversation = conv,
            messages = messages,
            onDismiss = { showAiSummaryDialog = false }
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isMe: Boolean,
    highlightQuery: String,
    translatedText: String? = null,
    targetLanguageName: String? = null,
    hideOriginal: Boolean = false,
    onToggleOriginal: (() -> Unit)? = null,
    onTranslateQuickClick: (() -> Unit)? = null,
    onLongClick: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick() }
                )
            },
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        // Sender name & role for incoming messages
        if (!isMe) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            ) {
                Text(
                    text = message.senderName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "(${message.senderRole})",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = if (isMe) DealCyanPrimary else MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 1.dp,
            tonalElevation = 1.dp,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Quoted reply snippet if this message is a reply
                if (message.replyToText != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = (if (isMe) Color.Black.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(28.dp)
                                    .background(if (isMe) Color.Black else DealCyanPrimary, RoundedCornerShape(2.dp))
                            )
                            Column {
                                Text(
                                    text = message.replyToSenderName ?: "Reply",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMe) Color.Black else DealCyanPrimary
                                )
                                Text(
                                    text = message.replyToText ?: "",
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (isMe) Color.Black.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Image Attachment Card
                if (message.attachmentName != null && message.attachmentType == "IMAGE") {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = (if (isMe) Color.Black.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .background(
                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                            listOf(
                                                DealCyanPrimary.copy(alpha = 0.8f),
                                                DealIndigo.copy(alpha = 0.9f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Image Preview", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = message.attachmentName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMe) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Document / File / Figma Attachment Card
                if (message.attachmentName != null && message.attachmentType != "IMAGE") {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = (if (isMe) Color.Black.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = when (message.attachmentType) {
                                    "FIGMA" -> Icons.Default.DesignServices
                                    else -> Icons.Default.InsertDriveFile
                                },
                                contentDescription = null,
                                tint = if (isMe) Color.Black else DealCyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = message.attachmentName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMe) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = when (message.attachmentType) {
                                        "FIGMA" -> "Figma Component Specs"
                                        "DOCUMENT" -> "Project Brief PDF"
                                        else -> "Shared File"
                                    },
                                    fontSize = 9.sp,
                                    color = if (isMe) Color.Black.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = if (isMe) Color.Black else DealCyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Message Text (Original message preserved)
                if (!hideOriginal) {
                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = if (isMe) Color.Black else MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }

                // AI Translated Message Display (Feature 9)
                if (translatedText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isMe) Color.Black.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, if (isMe) Color.Black.copy(alpha = 0.25f) else DealCyanPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.Translate,
                                        contentDescription = null,
                                        tint = if (isMe) Color.Black else DealCyanPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Translated ($targetLanguageName)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMe) Color.Black else DealCyanPrimary
                                    )
                                }

                                if (onToggleOriginal != null) {
                                    Text(
                                        text = if (hideOriginal) "Show Original" else "Hide Original",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMe) Color.Black else DealCyanPrimary,
                                        modifier = Modifier.clickable { onToggleOriginal() }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = translatedText,
                                fontSize = 12.sp,
                                color = if (isMe) Color.Black else MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }
                    }
                } else if (!isMe && onTranslateQuickClick != null) {
                    // Quick translate action trigger for incoming messages
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier
                            .clickable { onTranslateQuickClick() }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(Icons.Outlined.Translate, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(11.dp))
                        Text("Translate", fontSize = 9.sp, color = DealCyanPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Time & WhatsApp-style Delivery/Read Checkmarks
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 9.sp,
                        color = if (isMe) Color.Black.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isMe) {
                        // Double check: Cyan if read, dark/gray if sent
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = if (message.isRead) "Read" else "Sent",
                            tint = if (message.isRead) (if (isMe) Color.Black else DealCyanPrimary) else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentPickerDialog(
    onSelect: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Project File", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val files = listOf(
                    Triple("Storefront_Desktop_Mockup.png", "IMAGE", "PNG High-Res Mockup"),
                    Triple("Mobile_Checkout_Flow.jpg", "IMAGE", "JPG Mobile UX Layout"),
                    Triple("DealNest_Design_System.pdf", "DOCUMENT", "PDF Specification Guide"),
                    Triple("Figma_Tokens_v2.fig", "FIGMA", "Figma Interactive File"),
                    Triple("Source_Assets_Export.zip", "DOCUMENT", "Compressed Code & Icons")
                )

                files.forEach { (name, type, desc) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(name, type) }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = when (type) {
                                    "IMAGE" -> Icons.Default.Image
                                    "FIGMA" -> Icons.Default.DesignServices
                                    else -> Icons.Default.InsertDriveFile
                                },
                                contentDescription = null,
                                tint = DealCyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun MilestonesQuickDialog(
    deal: Deal,
    viewModel: DealNestViewModel,
    onDismiss: () -> Unit
) {
    val milestones by viewModel.currentDealMilestones.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Handshake, contentDescription = null, tint = DealEmerald)
                Text("Deal Room Milestones", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${deal.projectTitle} • $${deal.agreedPrice.toInt()}",
                    fontSize = 12.sp,
                    color = DealCyanPrimary,
                    fontWeight = FontWeight.Bold
                )

                if (milestones.isEmpty()) {
                    Text("4 Structured Milestones initialized in Escrow.", fontSize = 12.sp)
                } else {
                    milestones.forEach { ms ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ms.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text("$${ms.amount.toInt()} • Due ${ms.deadline}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                StatusChip(statusText = ms.status)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    viewModel.openDealRoomFromChat(deal.id)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DealEmerald, contentColor = Color.Black)
            ) {
                Text("Open Deal Room", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
