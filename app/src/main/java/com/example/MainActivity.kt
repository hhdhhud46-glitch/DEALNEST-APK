package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dealnest.model.UserRole
import com.example.dealnest.ui.AppTab
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.DealNestHeader
import com.example.dealnest.ui.screens.*
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealNestTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkThemeEnabled by remember { mutableStateOf(false) }

            DealNestTheme(darkTheme = darkThemeEnabled) {
                DealNestAppRoot(
                    onToggleTheme = { darkThemeEnabled = !darkThemeEnabled },
                    isDarkTheme = darkThemeEnabled
                )
            }
        }
    }
}

@Composable
fun DealNestAppRoot(
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean,
    viewModel: DealNestViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val projects by viewModel.allProjects.collectAsState()
    val designers by viewModel.allDesigners.collectAsState()
    val deals by viewModel.allDeals.collectAsState()
    val proposals by viewModel.allProposals.collectAsState()
    val conversations by viewModel.allConversations.collectAsState()
    val activeConversationId by viewModel.activeConversationId.collectAsState()
    val totalUnreadChatCount by viewModel.totalUnreadChatCount.collectAsState()
    val messages by viewModel.currentChatMessages.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()
    val favorites by viewModel.allFavorites.collectAsState()
    val reviews by viewModel.allReviews.collectAsState()
    val reports by viewModel.allReports.collectAsState()

    // Dialog state collectors
    val showPostProj by viewModel.showPostProjectDialog.collectAsState()
    val showSendProp by viewModel.showSendProposalDialog.collectAsState()
    val showReview by viewModel.showReviewDialog.collectAsState()
    val showSafety by viewModel.showSafetyDialog.collectAsState()
    val showMonetization by viewModel.showMonetizationDialog.collectAsState()
    val showNotifs by viewModel.showNotificationsSheet.collectAsState()
    val showAdmin by viewModel.showAdminDashboard.collectAsState()
    val showEditProfile by viewModel.showEditProfileDialog.collectAsState()

    val showAiMatch by viewModel.showAiMatchDetailsDialog.collectAsState()
    val matchProj by viewModel.selectedMatchProject.collectAsState()
    val matchDes by viewModel.selectedMatchDesigner.collectAsState()
    val showPortfolio by viewModel.showPortfolioBuilderDialog.collectAsState()
    val showAnalytics by viewModel.showDesignerAnalyticsDialog.collectAsState()
    val showComparison by viewModel.showClientComparisonDialog.collectAsState()
    val showLeadAlerts by viewModel.showLeadAlertsDialog.collectAsState()
    val showVerificationRequest by viewModel.showVerificationRequestDialog.collectAsState()
    val showVerificationInfo by viewModel.showVerificationInfoDialog.collectAsState()
    val showPublicPortfolio by viewModel.showPublicPortfolioDialog.collectAsState()
    val selectedPortfolioDesigner by viewModel.selectedPortfolioDesigner.collectAsState()
    val showAdvancedSearch by viewModel.showAdvancedSearchSheet.collectAsState()

    val showInvoice by viewModel.showInvoiceDialog.collectAsState()
    val invoiceDeal by viewModel.selectedDealForInvoice.collectAsState()
    val showDispute by viewModel.showOpenDisputeDialog.collectAsState()
    val disputeDeal by viewModel.selectedDealForDispute.collectAsState()
    val showPayments by viewModel.showPaymentsDialog.collectAsState()
    val showReferral by viewModel.showReferralDialog.collectAsState()

    val selectedProj by viewModel.selectedProjectForDetail.collectAsState()
    val selectedDes by viewModel.selectedDesignerForDetail.collectAsState()
    val selectedDeal by viewModel.selectedDealForDetail.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val unreadNotifs = remember(notifications) {
        notifications.count { !it.isRead }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dealnest_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            DealNestHeader(
                currentRole = currentRole,
                unreadNotifCount = unreadNotifs,
                unreadChatCount = totalUnreadChatCount,
                onRoleClick = {
                    val nextRole = when (currentRole) {
                        UserRole.CLIENT -> UserRole.DESIGNER
                        UserRole.DESIGNER -> UserRole.ADMIN
                        UserRole.ADMIN -> UserRole.CLIENT
                    }
                    viewModel.switchRole(nextRole)
                },
                onNotifClick = { viewModel.showNotificationsSheet.value = true },
                onSafetyClick = { viewModel.showSafetyDialog.value = true },
                onChatClick = { viewModel.switchTab(AppTab.MESSAGES) }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("dealnest_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.switchTab(tab) },
                        icon = {
                            if (tab == AppTab.MESSAGES) {
                                BadgedBox(
                                    badge = {
                                        if (totalUnreadChatCount > 0) {
                                            Badge(
                                                containerColor = DealCyanPrimary,
                                                contentColor = Color.Black
                                            ) {
                                                Text(
                                                    text = "$totalUnreadChatCount",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.Chat else Icons.Outlined.Chat,
                                        contentDescription = tab.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = when (tab) {
                                        AppTab.HOME -> if (isSelected) Icons.Default.Home else Icons.Outlined.Home
                                        AppTab.PROJECTS -> if (isSelected) Icons.Default.Explore else Icons.Outlined.Explore
                                        AppTab.DEALS -> if (isSelected) Icons.Default.Handshake else Icons.Outlined.Handshake
                                        AppTab.PROFILE -> if (isSelected) Icons.Default.Person else Icons.Outlined.Person
                                        else -> Icons.Default.Home
                                    },
                                    contentDescription = tab.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            indicatorColor = DealCyanPrimary,
                            selectedTextColor = DealCyanPrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentTab == AppTab.HOME || currentTab == AppTab.PROJECTS) {
                if (currentRole == UserRole.CLIENT) {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.showPostProjectDialog.value = true },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Post Project", fontWeight = FontWeight.Bold) },
                        containerColor = DealCyanPrimary,
                        contentColor = Color.Black,
                        modifier = Modifier
                            .padding(bottom = 70.dp)
                            .testTag("fab_post_project")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        projects = projects,
                        designers = designers,
                        currentUser = currentUser,
                        currentRole = currentRole
                    )
                }
                AppTab.PROJECTS -> {
                    ProjectsFeedScreen(
                        viewModel = viewModel,
                        projects = projects,
                        currentUser = currentUser,
                        currentRole = currentRole
                    )
                }
                AppTab.MESSAGES -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val isWideScreen = maxWidth >= 720.dp
                        if (isWideScreen) {
                            // Adaptive Tablet / Foldable view: List on left, Chat on right
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .width(360.dp)
                                        .fillMaxHeight()
                                ) {
                                    ConversationsListScreen(
                                        viewModel = viewModel,
                                        conversations = conversations,
                                        currentUser = currentUser,
                                        onSelectConversation = { conv ->
                                            viewModel.selectConversation(conv.id)
                                        }
                                    )
                                }
                                VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    if (activeConversationId != null) {
                                        ChatScreen(
                                            viewModel = viewModel,
                                            currentUser = currentUser,
                                            onBack = { viewModel.clearActiveConversation() }
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            com.example.dealnest.ui.components.EmptyPlaceholder(
                                                icon = Icons.Outlined.Forum,
                                                title = "Select a conversation",
                                                subtitle = "Choose a chat from the conversation list on the left."
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Mobile view: Full-screen chat if active, or conversation list
                            if (activeConversationId != null) {
                                ChatScreen(
                                    viewModel = viewModel,
                                    currentUser = currentUser,
                                    onBack = { viewModel.clearActiveConversation() }
                                )
                            } else {
                                ConversationsListScreen(
                                    viewModel = viewModel,
                                    conversations = conversations,
                                    currentUser = currentUser,
                                    onSelectConversation = { conv ->
                                        viewModel.selectConversation(conv.id)
                                    }
                                )
                            }
                        }
                    }
                }
                AppTab.DEALS -> {
                    DealRoomScreen(
                        viewModel = viewModel,
                        deals = deals,
                        currentUser = currentUser
                    )
                }
                AppTab.PROFILE -> {
                    ProfileDashboardScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        currentRole = currentRole,
                        projects = projects,
                        proposals = proposals,
                        deals = deals,
                        favorites = favorites
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showPostProj) {
        PostProjectDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showPostProjectDialog.value = false }
        )
    }

    if (showSendProp) {
        val targetProject = selectedProj ?: projects.firstOrNull()
        if (targetProject != null) {
            SendProposalDialog(
                viewModel = viewModel,
                project = targetProject,
                onDismiss = { viewModel.showSendProposalDialog.value = false }
            )
        }
    }

    if (selectedProj != null && !showSendProp) {
        ProjectDetailDialog(
            viewModel = viewModel,
            project = selectedProj!!,
            currentUser = currentUser,
            onDismiss = { viewModel.selectedProjectForDetail.value = null }
        )
    }

    if (selectedDes != null) {
        DesignerDetailDialog(
            viewModel = viewModel,
            designer = selectedDes!!,
            onDismiss = { viewModel.selectedDesignerForDetail.value = null }
        )
    }

    if (showAdmin || currentRole == UserRole.ADMIN) {
        if (showAdmin) {
            AdminDashboardScreen(
                viewModel = viewModel,
                users = designers + currentUser,
                projects = projects,
                proposals = proposals,
                deals = deals,
                reports = reports,
                reviews = reviews,
                onDismiss = { viewModel.showAdminDashboard.value = false }
            )
        }
    }

    if (showReview) {
        val dealForReview = selectedDeal ?: deals.firstOrNull()
        if (dealForReview != null) {
            ReviewDialog(
                viewModel = viewModel,
                deal = dealForReview,
                currentUser = currentUser,
                onDismiss = { viewModel.showReviewDialog.value = false }
            )
        }
    }

    if (showSafety) {
        SafetyTrustDialog(
            onDismiss = { viewModel.showSafetyDialog.value = false }
        )
    }

    if (showMonetization) {
        MonetizationDialog(
            viewModel = viewModel,
            currentUser = currentUser,
            onDismiss = { viewModel.showMonetizationDialog.value = false }
        )
    }

    if (showNotifs) {
        NotificationsSheet(
            viewModel = viewModel,
            notifications = notifications,
            onDismiss = { viewModel.showNotificationsSheet.value = false }
        )
    }

    if (showEditProfile) {
        EditProfileDialog(
            viewModel = viewModel,
            currentUser = currentUser,
            onDismiss = { viewModel.showEditProfileDialog.value = false }
        )
    }

    if (showAiMatch && matchProj != null && matchDes != null) {
        AiMatchExplanationDialog(
            project = matchProj!!,
            designer = matchDes!!,
            onDismiss = { viewModel.showAiMatchDetailsDialog.value = false }
        )
    }

    if (showPortfolio) {
        val targetDesigner = selectedDes ?: designers.firstOrNull() ?: currentUser
        PortfolioBuilderDialog(
            viewModel = viewModel,
            designer = targetDesigner,
            currentUser = currentUser,
            onDismiss = { viewModel.showPortfolioBuilderDialog.value = false }
        )
    }

    if (showAnalytics) {
        DesignerAnalyticsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showDesignerAnalyticsDialog.value = false }
        )
    }

    if (showComparison) {
        ClientComparisonDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showClientComparisonDialog.value = false }
        )
    }

    if (showLeadAlerts) {
        LeadAlertsConfigDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showLeadAlertsDialog.value = false }
        )
    }

    if (showVerificationRequest) {
        VerificationRequestDialog(
            viewModel = viewModel,
            currentUser = currentUser,
            onDismiss = { viewModel.showVerificationRequestDialog.value = false }
        )
    }

    if (showVerificationInfo) {
        VerificationInfoDialog(
            onDismiss = { viewModel.showVerificationInfoDialog.value = false }
        )
    }

    if (showPublicPortfolio) {
        val targetDesigner = selectedPortfolioDesigner ?: selectedDes ?: designers.firstOrNull() ?: currentUser
        PublicPortfolioDialog(
            viewModel = viewModel,
            designer = targetDesigner,
            currentUser = currentUser,
            onDismiss = { viewModel.showPublicPortfolioDialog.value = false }
        )
    }

    if (showAdvancedSearch) {
        AdvancedSearchSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.showAdvancedSearchSheet.value = false }
        )
    }

    if (showInvoice && invoiceDeal != null) {
        InvoiceDialog(
            viewModel = viewModel,
            deal = invoiceDeal!!,
            onDismiss = { viewModel.showInvoiceDialog.value = false }
        )
    }

    if (showDispute && disputeDeal != null) {
        OpenDisputeDialog(
            viewModel = viewModel,
            deal = disputeDeal!!,
            onDismiss = { viewModel.showOpenDisputeDialog.value = false }
        )
    }

    if (showPayments) {
        PaymentsSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.showPaymentsDialog.value = false }
        )
    }

    if (showReferral) {
        ReferralDialog(
            viewModel = viewModel,
            currentUser = currentUser,
            onDismiss = { viewModel.showReferralDialog.value = false }
        )
    }
}
