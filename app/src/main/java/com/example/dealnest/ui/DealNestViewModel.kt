package com.example.dealnest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dealnest.data.DealNestDatabase
import com.example.dealnest.data.DealNestRepository
import com.example.dealnest.data.MatchCalculation
import com.example.dealnest.data.SeedData
import com.example.dealnest.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    HOME("Home"),
    PROJECTS("Projects"),
    MESSAGES("Messages"),
    DEALS("Deals"),
    PROFILE("Profile")
}

data class FilterState(
    val searchQuery: String = "",
    val websiteType: String = "All",
    val maxBudget: Double = 10000.0,
    val selectedSkill: String = "All"
)

data class DesignerAnalyticsData(
    val profileViews: Int = 1420,
    val portfolioViews: Int = 890,
    val proposalViews: Int = 145,
    val proposalsSent: Int = 18,
    val proposalsAccepted: Int = 12,
    val activeDeals: Int = 2,
    val completedProjects: Int = 38,
    val totalEarningsUsd: Double = 48500.0,
    val weeklyViews: List<Int> = listOf(140, 190, 220, 210, 280, 310, 340)
)

class DealNestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DealNestRepository

    init {
        val database = DealNestDatabase.getDatabase(application)
        repository = DealNestRepository(database.dealNestDao())
    }

    // Current User & Active Role
    private val _currentUser = MutableStateFlow<User>(SeedData.demoClient)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.CLIENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Navigation State
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Currency Setting
    private val _selectedCurrency = MutableStateFlow(SupportedCurrency.USD)
    val selectedCurrency: StateFlow<SupportedCurrency> = _selectedCurrency.asStateFlow()

    fun selectCurrency(currency: SupportedCurrency) {
        _selectedCurrency.value = currency
        viewModelScope.launch {
            _snackbarMessage.emit("Currency set to ${currency.code} (${currency.symbol})")
        }
    }

    fun formatPrice(amountUsd: Double): String {
        val curr = _selectedCurrency.value
        val converted = amountUsd * curr.exchangeRateFromUsd
        return if (curr == SupportedCurrency.JPY) {
            "${curr.symbol}${converted.toInt()}"
        } else {
            "${curr.symbol}${String.format("%,.0f", converted)}"
        }
    }

    // Data from Repository
    val allProjects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDesigners: StateFlow<List<User>> = repository.allDesigners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDeals: StateFlow<List<Deal>> = repository.allDeals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProposals: StateFlow<List<Proposal>> = repository.allProposals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReviews: StateFlow<List<Review>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<AppNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFavorites: StateFlow<List<FavoriteItem>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<ReportItem>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDisputes: StateFlow<List<DisputeItem>> = repository.allDisputes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPaymentTransactions: StateFlow<List<PaymentTransaction>> = repository.allPaymentTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBlockedUsers: StateFlow<List<BlockedUser>> = repository.allBlockedUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Global Marketplace State
    private val _selectedLanguage = MutableStateFlow(SupportedLanguage.ENGLISH)
    val selectedLanguage: StateFlow<SupportedLanguage> = _selectedLanguage.asStateFlow()

    private val _selectedTimeZone = MutableStateFlow("UTC-5 (EST)")
    val selectedTimeZone: StateFlow<String> = _selectedTimeZone.asStateFlow()

    fun selectLanguage(language: SupportedLanguage) {
        _selectedLanguage.value = language
        viewModelScope.launch {
            _snackbarMessage.emit("Language changed to ${language.displayName} (${language.nativeName})")
        }
    }

    fun selectTimeZone(tz: String) {
        _selectedTimeZone.value = tz
        viewModelScope.launch {
            _snackbarMessage.emit("Timezone set to $tz")
        }
    }

    // Client Analytics & Referral System
    val clientAnalytics = MutableStateFlow(ClientAnalyticsData())
    val referralStats = MutableStateFlow(ReferralStats(referralCode = "DN-ALEX2026"))
    val notificationPreferences = MutableStateFlow(NotificationPreferences())

    // Phase 5 Dialog States
    val showInvoiceDialog = MutableStateFlow(false)
    val selectedDealForInvoice = MutableStateFlow<Deal?>(null)
    val showOpenDisputeDialog = MutableStateFlow(false)
    val selectedDealForDispute = MutableStateFlow<Deal?>(null)
    val showAiSummaryDialog = MutableStateFlow(false)
    val showAiRequirementBuilderDialog = MutableStateFlow(false)
    val showPaymentsDialog = MutableStateFlow(false)
    val showReferralDialog = MutableStateFlow(false)

    fun openInvoiceForDeal(deal: Deal) {
        selectedDealForInvoice.value = deal
        showInvoiceDialog.value = true
    }

    fun openDisputeForDeal(deal: Deal) {
        selectedDealForDispute.value = deal
        showOpenDisputeDialog.value = true
    }

    fun submitDispute(
        deal: Deal,
        milestoneTitle: String,
        reason: String,
        description: String,
        evidenceNotes: String
    ) {
        viewModelScope.launch {
            repository.openDispute(
                deal = deal,
                raisedByUser = _currentUser.value,
                milestoneTitle = milestoneTitle,
                reason = reason,
                description = description,
                evidenceNotes = evidenceNotes
            )
            showOpenDisputeDialog.value = false
            _snackbarMessage.emit("Dispute opened. DealNest safety mediation team notified.")
        }
    }

    fun updateDisputeStatus(disputeId: String, newStatus: String, adminNotes: String) {
        viewModelScope.launch {
            repository.updateDisputeStatus(disputeId, newStatus, adminNotes)
            _snackbarMessage.emit("Dispute $disputeId updated to '$newStatus'.")
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            repository.deleteNotification(id)
            _snackbarMessage.emit("Notification removed.")
        }
    }

    fun updateNotificationPreferences(prefs: NotificationPreferences) {
        notificationPreferences.value = prefs
        viewModelScope.launch {
            _snackbarMessage.emit("Notification preferences saved.")
        }
    }

    fun generateInvoiceForDeal(deal: Deal): DealInvoice {
        val curr = _selectedCurrency.value
        val milestones = SeedData.demoMilestones.filter { it.dealId == deal.id }.ifEmpty {
            listOf(
                DealMilestone("m_1", deal.id, "Milestone 1: Wireframes & Discovery", "UX layout", deal.agreedPrice * 0.25, "Completed", MilestoneStatus.COMPLETED.name, 0),
                DealMilestone("m_2", deal.id, "Milestone 2: High-Fi Design System", "Figma UI", deal.agreedPrice * 0.25, "Completed", MilestoneStatus.COMPLETED.name, 1),
                DealMilestone("m_3", deal.id, "Milestone 3: Core Code Development", "Build", deal.agreedPrice * 0.25, "Completed", MilestoneStatus.COMPLETED.name, 2),
                DealMilestone("m_4", deal.id, "Milestone 4: QA, Polish & Deployment", "Launch", deal.agreedPrice * 0.25, "Completed", MilestoneStatus.COMPLETED.name, 3)
            )
        }
        val items = milestones.map {
            InvoiceMilestoneItem(
                title = it.title,
                amount = it.amount,
                status = if (deal.status == DealStatus.COMPLETED.name) "Paid" else "Pending"
            )
        }
        val invNumber = "INV-DN-${deal.id.takeLast(4).uppercase()}-${SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())}"
        val dateStr = SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(Date())

        return DealInvoice(
            invoiceNumber = invNumber,
            date = dateStr,
            dealId = deal.id,
            projectTitle = deal.projectTitle,
            projectDescription = "Professional website design and development delivered via DealNest platform milestones.",
            clientName = deal.clientName,
            clientContact = "${deal.clientName.lowercase().replace(" ", ".")}@clientmail.com",
            designerName = deal.designerName,
            designerContact = "${deal.designerName.lowercase().replace(" ", ".")}@designstudio.io",
            agreedAmount = deal.agreedPrice,
            milestoneBreakdown = items,
            totalAmount = deal.agreedPrice,
            status = if (deal.status == DealStatus.COMPLETED.name) "Paid" else "Demo Escrow Pending",
            currencyCode = curr.code,
            currencySymbol = curr.symbol
        )
    }

    // Conversations & Chat
    val allConversations: StateFlow<List<Conversation>> = _currentUser
        .flatMapLatest { user -> repository.getConversationsForUser(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalUnreadChatCount: StateFlow<Int> = combine(allConversations, _currentUser) { convs, user ->
        convs.sumOf { conv ->
            if (user.id == conv.clientUserId) conv.unreadCountClient else conv.unreadCountDesigner
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _activeConversationId = MutableStateFlow<String?>("conv_1")
    val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

    val activeDealIdForChat: StateFlow<String> = _activeConversationId
        .map { it ?: "deal_1" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "deal_1")

    val activeConversation: StateFlow<Conversation?> = _activeConversationId
        .flatMapLatest { id ->
            if (id != null) repository.getConversationById(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentChatMessages: StateFlow<List<ChatMessage>> = _activeConversationId
        .flatMapLatest { convId ->
            if (convId != null) {
                repository.getMessagesForDeal(convId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Deal Milestones
    val currentDealMilestones: StateFlow<List<DealMilestone>> = _activeConversationId
        .flatMapLatest { convId ->
            val dealId = allConversations.value.firstOrNull { it.id == convId }?.dealId ?: convId ?: "deal_1"
            repository.getMilestonesForDeal(dealId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // In-chat state
    val chatSearchQuery = MutableStateFlow("")
    val replyingToMessage = MutableStateFlow<ChatMessage?>(null)
    val showAiAssistantSheet = MutableStateFlow(false)
    val showCommunityGuidelinesDialog = MutableStateFlow(false)

    // Conversation List Search & Filter
    val conversationSearchQuery = MutableStateFlow("")
    val conversationFilter = MutableStateFlow("ALL") // ALL, UNREAD, ACTIVE_DEALS, PROPOSALS

    // Active Designer Portfolio Projects & Services
    private val _activeDesignerPortfolioId = MutableStateFlow("des_2")
    val activeDesignerPortfolioId: StateFlow<String> = _activeDesignerPortfolioId.asStateFlow()

    val currentPortfolioProjects: StateFlow<List<PortfolioProject>> = _activeDesignerPortfolioId
        .flatMapLatest { designerId -> repository.getPortfolioProjectsForDesigner(designerId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentDesignerServices: StateFlow<List<DesignerService>> = _activeDesignerPortfolioId
        .flatMapLatest { designerId -> repository.getServicesForDesigner(designerId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getReviewsForUser(userId: String): Flow<List<Review>> = repository.getReviewsForUser(userId)
    fun getReviewsForDeal(dealId: String): Flow<List<Review>> = repository.getReviewsForDeal(dealId)

    // Verification Requests
    val allVerificationRequests: StateFlow<List<VerificationRequest>> = repository.allVerificationRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentVerificationRequest: StateFlow<VerificationRequest?> = _currentUser
        .flatMapLatest { user -> repository.getVerificationRequestForDesigner(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Lead Alert Config
    val currentLeadAlertConfig: StateFlow<DesignerLeadAlertConfig?> = _currentUser
        .flatMapLatest { user -> repository.getLeadAlertConfig(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Blocked Users
    val blockedUsers: StateFlow<List<BlockedUser>> = _currentUser
        .flatMapLatest { user -> repository.getBlockedUsers(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Client Comparison List (up to 3 designers)
    private val _comparisonDesigners = MutableStateFlow<List<User>>(emptyList())
    val comparisonDesigners: StateFlow<List<User>> = _comparisonDesigners.asStateFlow()

    // Filter State
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    // Advanced Search & Filter System
    val searchFilterState = MutableStateFlow(SearchFilterState())
    val recentSearches = MutableStateFlow(listOf("Shopify", "Webflow SaaS", "Next.js", "Figma", "WordPress"))
    val searchSuggestions = listOf("Shopify", "Webflow", "Next.js", "Figma", "WordPress", "E-Commerce", "SaaS", "Brand Identity", "FinTech", "Mobile App UI")
    val showAdvancedSearchSheet = MutableStateFlow(false)

    val filteredProjects: StateFlow<List<Project>> = combine(allProjects, searchFilterState) { projs, filter ->
        projs.filter { proj ->
            val matchesQuery = filter.searchQuery.isBlank() ||
                    proj.title.contains(filter.searchQuery, ignoreCase = true) ||
                    proj.websiteType.contains(filter.searchQuery, ignoreCase = true) ||
                    proj.requiredSkills.contains(filter.searchQuery, ignoreCase = true) ||
                    proj.description.contains(filter.searchQuery, ignoreCase = true)

            val matchesMinBudget = filter.minBudget == null || proj.budgetMax >= filter.minBudget
            val matchesMaxBudget = filter.maxBudget == null || proj.budgetMin <= filter.maxBudget
            val matchesDeadline = filter.deadlineDaysMax == null || proj.deadlineDays <= filter.deadlineDaysMax
            val matchesType = filter.websiteType == null || proj.websiteType.equals(filter.websiteType, ignoreCase = true)
            val matchesSkill = filter.requiredSkill == null || proj.requiredSkills.contains(filter.requiredSkill, ignoreCase = true)
            val matchesStatus = filter.projectStatus == null || proj.status.equals(filter.projectStatus, ignoreCase = true)

            matchesQuery && matchesMinBudget && matchesMaxBudget && matchesDeadline && matchesType && matchesSkill && matchesStatus
        }.let { list ->
            when (filter.sortOption) {
                "Budget: High to Low" -> list.sortedByDescending { it.budgetMax }
                "Budget: Low to High" -> list.sortedBy { it.budgetMin }
                "Fastest Deadline" -> list.sortedBy { it.deadlineDays }
                else -> list.sortedByDescending { it.createdAt }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredDesigners: StateFlow<List<User>> = combine(allDesigners, searchFilterState) { desList, filter ->
        desList.filter { designer ->
            val matchesQuery = filter.searchQuery.isBlank() ||
                    designer.name.contains(filter.searchQuery, ignoreCase = true) ||
                    designer.skills.contains(filter.searchQuery, ignoreCase = true) ||
                    designer.headline.contains(filter.searchQuery, ignoreCase = true) ||
                    designer.services.contains(filter.searchQuery, ignoreCase = true) ||
                    designer.about.contains(filter.searchQuery, ignoreCase = true)

            val matchesSkill = filter.requiredSkill == null || designer.skills.contains(filter.requiredSkill, ignoreCase = true)
            val matchesExp = filter.minExperienceYears == null || designer.experienceYears >= filter.minExperienceYears
            val matchesMinPrice = filter.minStartingPrice == null || designer.startingPrice >= filter.minStartingPrice
            val matchesMaxPrice = filter.maxStartingPrice == null || designer.startingPrice <= filter.maxStartingPrice
            val matchesRating = filter.minRating == null || designer.rating >= filter.minRating
            val matchesVerified = !filter.verifiedOnly || designer.isVerified
            val matchesAvailable = !filter.availableOnly || designer.availability.lowercase().contains("available") || designer.availability.lowercase().contains("immediate")

            matchesQuery && matchesSkill && matchesExp && matchesMinPrice && matchesMaxPrice && matchesRating && matchesVerified && matchesAvailable
        }.let { list ->
            when (filter.sortOption) {
                "Rating" -> list.sortedByDescending { it.rating }
                "Experience" -> list.sortedByDescending { it.experienceYears }
                "Starting Price: Low" -> list.sortedBy { it.startingPrice }
                "Starting Price: High" -> list.sortedByDescending { it.startingPrice }
                else -> list.sortedByDescending { it.completedProjectsCount }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Dialogs & Selected entities
    var selectedProjectForDetail = MutableStateFlow<Project?>(null)
    var selectedDesignerForDetail = MutableStateFlow<User?>(null)
    var selectedDealForDetail = MutableStateFlow<Deal?>(null)
    var selectedDealForReview = MutableStateFlow<Deal?>(null)

    var showPostProjectDialog = MutableStateFlow(false)
    var showSendProposalDialog = MutableStateFlow(false)
    var showReviewDialog = MutableStateFlow(false)
    var showReportDialog = MutableStateFlow(false)
    var showMonetizationDialog = MutableStateFlow(false)
    var showSafetyDialog = MutableStateFlow(false)
    var showNotificationsSheet = MutableStateFlow(false)
    var showAdminDashboard = MutableStateFlow(false)
    var showEditProfileDialog = MutableStateFlow(false)
    var showVerificationRequestDialog = MutableStateFlow(false)
    var showVerificationInfoDialog = MutableStateFlow(false)
    var showPublicPortfolioDialog = MutableStateFlow(false)
    var selectedPortfolioDesigner = MutableStateFlow<User?>(null)

    // New Advanced Feature Dialogs
    var showAiMatchDetailsDialog = MutableStateFlow(false)
    var selectedMatchProject = MutableStateFlow<Project?>(null)
    var selectedMatchDesigner = MutableStateFlow<User?>(null)
    var showPortfolioBuilderDialog = MutableStateFlow(false)
    var showDesignerAnalyticsDialog = MutableStateFlow(false)
    var showClientComparisonDialog = MutableStateFlow(false)
    var showLeadAlertsDialog = MutableStateFlow(false)

    fun openAiMatchExplanation(project: Project, designer: User) {
        selectedMatchProject.value = project
        selectedMatchDesigner.value = designer
        showAiMatchDetailsDialog.value = true
    }

    // Designer Analytics Mock Model
    val designerAnalytics = MutableStateFlow(DesignerAnalyticsData())

    // User Feedback Toast / Snackbar
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // Actions
    fun switchTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
        if (role == UserRole.DESIGNER) {
            _currentUser.value = SeedData.demoDesigners.first()
            _activeDesignerPortfolioId.value = SeedData.demoDesigners.first().id
        } else if (role == UserRole.CLIENT) {
            _currentUser.value = SeedData.demoClient
        }
        viewModelScope.launch {
            _snackbarMessage.emit("Switched view to ${role.name.lowercase().replaceFirstChar { it.uppercase() }}")
        }
    }

    fun openDesignerPortfolio(designer: User) {
        _activeDesignerPortfolioId.value = designer.id
        selectedDesignerForDetail.value = designer
        showPortfolioBuilderDialog.value = true
    }

    fun toggleComparison(designer: User) {
        val currentList = _comparisonDesigners.value
        if (currentList.any { it.id == designer.id }) {
            _comparisonDesigners.value = currentList.filterNot { it.id == designer.id }
            viewModelScope.launch {
                _snackbarMessage.emit("${designer.name} removed from comparison.")
            }
        } else {
            if (currentList.size >= 3) {
                viewModelScope.launch {
                    _snackbarMessage.emit("You can compare up to 3 designers at a time.")
                }
            } else {
                _comparisonDesigners.value = currentList + designer
                viewModelScope.launch {
                    _snackbarMessage.emit("${designer.name} added to comparison (${_comparisonDesigners.value.size}/3).")
                }
            }
        }
    }

    fun clearComparison() {
        _comparisonDesigners.value = emptyList()
    }

    fun updateWebsiteTypeFilter(type: String) {
        _filterState.value = _filterState.value.copy(websiteType = type)
        searchFilterState.value = searchFilterState.value.copy(websiteType = if (type == "All") null else type)
    }

    fun updateSkillFilter(skill: String) {
        _filterState.value = _filterState.value.copy(selectedSkill = skill)
        searchFilterState.value = searchFilterState.value.copy(requiredSkill = if (skill == "All") null else skill)
    }

    fun selectConversation(conversationId: String) {
        _activeConversationId.value = conversationId
        _currentTab.value = AppTab.MESSAGES
        viewModelScope.launch {
            repository.markConversationRead(conversationId, _currentUser.value.id)
        }
    }

    fun clearActiveConversation() {
        _activeConversationId.value = null
    }

    fun selectDealForChat(dealId: String) {
        val deal = allDeals.value.firstOrNull { it.id == dealId }
        val matchingConv = allConversations.value.firstOrNull { it.dealId == dealId }
        val convId = matchingConv?.id ?: "conv_$dealId"
        _activeConversationId.value = convId
        if (deal != null) {
            selectedDealForDetail.value = deal
        }
        _currentTab.value = AppTab.MESSAGES
        viewModelScope.launch {
            repository.markConversationRead(convId, _currentUser.value.id)
        }
    }

    fun startChatWithDesigner(designer: User, project: Project? = null) {
        viewModelScope.launch {
            val proj = project ?: allProjects.value.firstOrNull() ?: Project(
                id = "proj_inquiry_${System.currentTimeMillis()}",
                clientUserId = _currentUser.value.id,
                clientName = _currentUser.value.name,
                title = "Design Collaboration Inquiry",
                websiteType = "Custom Design",
                description = "General inquiry and project discussion with ${designer.name}.",
                budgetMin = designer.startingPrice,
                budgetMax = designer.startingPrice * 1.5,
                deadlineDays = designer.deliveryTimeDays,
                requiredSkills = designer.skills,
                pageCount = 5,
                requiredFeatures = "Responsive Layout, Interactive Prototypes"
            )

            val conv = repository.startConversation(
                projectId = proj.id,
                projectTitle = proj.title,
                clientUser = _currentUser.value,
                designerUser = designer,
                budget = "$${proj.budgetMin.toInt()} - $${proj.budgetMax.toInt()}",
                deadline = "${proj.deadlineDays} days",
                initialMessageText = "Hi ${designer.name}! I'm interested in discussing a project for '${proj.title}'."
            )
            _activeConversationId.value = conv.id
            _currentTab.value = AppTab.MESSAGES
            _snackbarMessage.emit("Opened chat with ${designer.name}")
        }
    }

    fun startChatWithClient(project: Project) {
        viewModelScope.launch {
            val clientUser = repository.getUserById(project.clientUserId).firstOrNull() ?: User(
                id = project.clientUserId,
                name = project.clientName,
                email = "",
                role = UserRole.CLIENT.name
            )
            val conv = repository.startConversation(
                projectId = project.id,
                projectTitle = project.title,
                clientUser = clientUser,
                designerUser = _currentUser.value,
                budget = "$${project.budgetMin.toInt()} - $${project.budgetMax.toInt()}",
                deadline = "${project.deadlineDays} days",
                initialMessageText = "Hi ${project.clientName}! I saw your project '${project.title}' and would love to collaborate."
            )
            _activeConversationId.value = conv.id
            _currentTab.value = AppTab.MESSAGES
            _snackbarMessage.emit("Opened chat with ${project.clientName}")
        }
    }

    fun startChatForProposal(proposal: Proposal) {
        viewModelScope.launch {
            val existing = allConversations.value.firstOrNull {
                it.projectId == proposal.projectId &&
                (it.designerUserId == proposal.designerUserId || it.clientUserId == proposal.designerUserId)
            }
            if (existing != null) {
                selectConversation(existing.id)
            } else {
                val designer = repository.getUserById(proposal.designerUserId).firstOrNull() ?: User(
                    id = proposal.designerUserId,
                    name = proposal.designerName,
                    headline = proposal.designerHeadline,
                    avatarUrl = "",
                    email = "",
                    role = UserRole.DESIGNER.name
                )
                startChatWithDesigner(designer, allProjects.value.firstOrNull { it.id == proposal.projectId })
            }
        }
    }

    fun openDealRoomFromChat(dealId: String) {
        val deal = allDeals.value.firstOrNull { it.id == dealId }
        if (deal != null) {
            selectedDealForDetail.value = deal
        }
        _currentTab.value = AppTab.DEALS
    }

    fun openProjectFromChat(projectId: String) {
        val project = allProjects.value.firstOrNull { it.id == projectId }
        if (project != null) {
            selectedProjectForDetail.value = project
        }
    }

    fun toggleFavorite(itemType: String, targetId: String) {
        val isSaved = allFavorites.value.any { it.itemType == itemType && it.targetId == targetId }
        viewModelScope.launch {
            repository.toggleFavorite(itemType, targetId, isSaved)
            _snackbarMessage.emit(if (isSaved) "Removed from saved" else "Saved to favorites")
        }
    }

    fun isFavorite(itemType: String, targetId: String): Boolean {
        return allFavorites.value.any { it.itemType == itemType && it.targetId == targetId }
    }

    fun postProject(
        title: String,
        websiteType: String,
        description: String,
        budgetMin: Double,
        budgetMax: Double,
        deadlineDays: Int,
        requiredSkills: String,
        pageCount: Int,
        requiredFeatures: String,
        referenceLinks: String
    ) {
        viewModelScope.launch {
            repository.createProject(
                title = title,
                websiteType = websiteType,
                description = description,
                budgetMin = budgetMin,
                budgetMax = budgetMax,
                deadlineDays = deadlineDays,
                requiredSkills = requiredSkills,
                pageCount = pageCount,
                requiredFeatures = requiredFeatures,
                referenceLinks = referenceLinks,
                clientUser = _currentUser.value
            )
            showPostProjectDialog.value = false
            _snackbarMessage.emit("Project '$title' posted! Designers are notified.")
            _currentTab.value = AppTab.PROJECTS
        }
    }

    fun sendProposal(
        project: Project,
        proposedPrice: Double,
        deliveryDays: Int,
        coverMessage: String,
        portfolioLink: String
    ) {
        viewModelScope.launch {
            repository.submitProposal(
                projectId = project.id,
                projectTitle = project.title,
                designer = _currentUser.value,
                proposedPrice = proposedPrice,
                deliveryDays = deliveryDays,
                coverMessage = coverMessage,
                portfolioLink = portfolioLink
            )
            showSendProposalDialog.value = false
            _snackbarMessage.emit("Proposal sent to ${project.clientName}!")
        }
    }

    fun acceptProposal(proposal: Proposal) {
        viewModelScope.launch {
            val deal = repository.acceptProposal(proposal, _currentUser.value)
            _snackbarMessage.emit("Proposal accepted! Deal Room & Milestones initialized.")
            selectedDealForDetail.value = deal
            _activeConversationId.value = "conv_${proposal.projectId}_${proposal.designerUserId}"
            _currentTab.value = AppTab.DEALS
        }
    }

    fun rejectProposal(proposal: Proposal) {
        viewModelScope.launch {
            repository.rejectProposal(proposal)
            _snackbarMessage.emit("Proposal declined.")
        }
    }

    fun updateMilestoneStatus(deal: Deal, milestone: DealMilestone, newStatus: MilestoneStatus) {
        viewModelScope.launch {
            repository.updateMilestoneStatus(deal, milestone, newStatus)
            _snackbarMessage.emit("Milestone status updated to ${newStatus.name.replace("_", " ")}.")
        }
    }

    fun advanceDealMilestone(deal: Deal) {
        viewModelScope.launch {
            val milestones = repository.getMilestonesForDeal(deal.id).first()
            val nextIncomplete = milestones.firstOrNull { 
                it.status == MilestoneStatus.IN_PROGRESS.name || it.status == MilestoneStatus.PENDING.name || it.status == MilestoneStatus.SUBMITTED.name || it.status == MilestoneStatus.REVISION.name
            }
            if (nextIncomplete != null) {
                val nextStatus = when (nextIncomplete.status) {
                    MilestoneStatus.PENDING.name -> MilestoneStatus.IN_PROGRESS
                    MilestoneStatus.IN_PROGRESS.name -> MilestoneStatus.SUBMITTED
                    MilestoneStatus.SUBMITTED.name -> MilestoneStatus.APPROVED
                    MilestoneStatus.REVISION.name -> MilestoneStatus.SUBMITTED
                    else -> MilestoneStatus.COMPLETED
                }
                repository.updateMilestoneStatus(deal, nextIncomplete, nextStatus)
                _snackbarMessage.emit("Milestone '${nextIncomplete.title}' advanced to ${nextStatus.name.replace("_", " ")}!")
            } else {
                changeDealStatus(deal, DealStatus.COMPLETED)
                _snackbarMessage.emit("All milestones completed! Deal marked as Done.")
            }
        }
    }

    fun changeDealStatus(deal: Deal, newStatus: DealStatus) {
        viewModelScope.launch {
            repository.updateDealStatus(deal, newStatus)
            selectedDealForDetail.value = deal.copy(status = newStatus.name)
            _snackbarMessage.emit("Deal marked as ${newStatus.name.replace("_", " ")}")
        }
    }

    fun addSharedFile(deal: Deal, fileName: String) {
        viewModelScope.launch {
            repository.addSharedFileToDeal(deal, fileName)
            val updated = deal.copy(
                sharedFiles = if (deal.sharedFiles.isBlank()) fileName else "${deal.sharedFiles}, $fileName"
            )
            selectedDealForDetail.value = updated
            _snackbarMessage.emit("File attached to Deal Room: $fileName")
        }
    }

    fun sendChatMessage(
        text: String,
        attachmentName: String? = null,
        attachmentType: String? = null
    ) {
        if (text.isBlank() && attachmentName == null) return
        val convId = _activeConversationId.value ?: "conv_1"
        val reply = replyingToMessage.value
        viewModelScope.launch {
            repository.sendChatMessage(
                conversationId = convId,
                senderUser = _currentUser.value,
                text = text,
                attachmentName = attachmentName,
                attachmentType = attachmentType,
                replyToMessage = reply
            )
            replyingToMessage.value = null
        }
    }

    fun deleteChatMessage(message: ChatMessage) {
        viewModelScope.launch {
            repository.deleteChatMessage(message.id)
            _snackbarMessage.emit("Message deleted")
        }
    }

    fun openReviewDialog(deal: Deal) {
        selectedDealForReview.value = deal
        showReviewDialog.value = true
    }

    fun submitReview(
        dealId: String,
        targetUserId: String,
        rating: Int,
        reviewText: String,
        categoryFeedback: String = "Quality Delivery",
        projectTitle: String = ""
    ) {
        viewModelScope.launch {
            val result = repository.submitReview(
                dealId = dealId,
                targetUserId = targetUserId,
                authorUser = _currentUser.value,
                rating = rating,
                reviewText = reviewText,
                projectTitle = projectTitle,
                categoryFeedback = categoryFeedback
            )
            if (result.isSuccess) {
                showReviewDialog.value = false
                selectedDealForReview.value = null
                _snackbarMessage.emit("Review published successfully! Thank you for your feedback.")
            } else {
                _snackbarMessage.emit(result.exceptionOrNull()?.message ?: "Review submission failed.")
            }
        }
    }

    fun reportReview(reviewId: String, reason: String) {
        viewModelScope.launch {
            repository.reportReview(reviewId, _currentUser.value.id, reason)
            _snackbarMessage.emit("Review reported. Our admin team will inspect it.")
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
            _snackbarMessage.emit("Review removed.")
        }
    }

    fun dismissReport(reportId: String) {
        viewModelScope.launch {
            repository.dismissReport(reportId)
            _snackbarMessage.emit("Report marked as resolved.")
        }
    }

    // Verification methods
    fun submitVerificationRequest(
        fullName: String,
        email: String,
        phone: String,
        role: String,
        skills: String,
        portfolioUrl: String,
        description: String
    ) {
        viewModelScope.launch {
            repository.submitVerificationRequest(
                designerUser = _currentUser.value,
                fullName = fullName,
                email = email,
                phone = phone,
                professionalRole = role,
                skills = skills,
                portfolioUrl = portfolioUrl,
                description = description
            )
            showVerificationRequestDialog.value = false
            _snackbarMessage.emit("Verification request submitted! Admin will review within 24-48 hours.")
        }
    }

    fun approveVerification(requestId: String, designerUserId: String) {
        viewModelScope.launch {
            repository.approveVerificationRequest(requestId, designerUserId)
            _snackbarMessage.emit("Designer verification approved!")
        }
    }

    fun rejectVerification(requestId: String, designerUserId: String, reason: String) {
        viewModelScope.launch {
            repository.rejectVerificationRequest(requestId, designerUserId, reason)
            _snackbarMessage.emit("Designer verification request rejected.")
        }
    }

    // Services
    fun addDesignerService(name: String, desc: String, price: Double, days: Int) {
        viewModelScope.launch {
            repository.addDesignerService(_currentUser.value.id, name, desc, price, days)
            _snackbarMessage.emit("Service '$name' added to your portfolio!")
        }
    }

    fun deleteDesignerService(serviceId: String) {
        viewModelScope.launch {
            repository.deleteDesignerService(serviceId)
            _snackbarMessage.emit("Service removed.")
        }
    }

    fun openPublicPortfolio(designer: User) {
        selectedPortfolioDesigner.value = designer
        _activeDesignerPortfolioId.value = designer.id
        showPublicPortfolioDialog.value = true
    }

    // Search and Filters
    fun updateSearchQuery(query: String) {
        searchFilterState.value = searchFilterState.value.copy(searchQuery = query)
        _filterState.value = _filterState.value.copy(searchQuery = query)
        if (query.isNotBlank() && !recentSearches.value.contains(query)) {
            recentSearches.value = (listOf(query) + recentSearches.value).take(8)
        }
    }

    fun updateSearchFilter(filter: SearchFilterState) {
        searchFilterState.value = filter
        _filterState.value = _filterState.value.copy(
            searchQuery = filter.searchQuery,
            websiteType = filter.websiteType ?: "All",
            maxBudget = filter.maxBudget ?: 10000.0,
            selectedSkill = filter.requiredSkill ?: "All"
        )
    }

    fun clearSearchFilters() {
        searchFilterState.value = SearchFilterState()
        _filterState.value = FilterState()
        _snackbarMessage.tryEmit("Search filters cleared.")
    }

    fun addRecentSearch(query: String) {
        if (query.isNotBlank()) {
            val list = recentSearches.value.toMutableList()
            list.remove(query)
            list.add(0, query)
            recentSearches.value = list.take(8)
        }
    }

    fun clearRecentSearches() {
        recentSearches.value = emptyList()
    }

    fun calculateSmartMatch(project: Project, designer: User): MatchCalculation {
        return repository.calculateSmartMatch(project, designer)
    }

    fun canUserReviewDeal(deal: Deal, userId: String): Boolean {
        if (deal.status != DealStatus.COMPLETED.name) return false
        if (userId != deal.clientUserId && userId != deal.designerUserId) return false
        val reviews = allReviews.value.filter { it.dealId == deal.id }
        return reviews.all { it.authorUserId != userId }
    }

    fun submitReport(targetType: String, targetId: String, targetName: String, reason: String) {
        viewModelScope.launch {
            repository.submitReport(
                reportedByUserId = _currentUser.value.id,
                targetType = targetType,
                targetId = targetId,
                targetName = targetName,
                reason = reason
            )
            showReportDialog.value = false
            _snackbarMessage.emit("Report submitted. Our safety team will review it.")
        }
    }

    fun blockUser(targetUserId: String, targetUserName: String, reason: String) {
        viewModelScope.launch {
            repository.blockUser(_currentUser.value.id, targetUserId, targetUserName, reason)
            _snackbarMessage.emit("Blocked $targetUserName.")
        }
    }

    fun unblockUser(targetUserId: String) {
        viewModelScope.launch {
            repository.unblockUser(_currentUser.value.id, targetUserId)
            _snackbarMessage.emit("User unblocked.")
        }
    }

    fun saveLeadAlertConfig(monitoredSkills: String, minBudget: Double, preferredTypes: String, isEnabled: Boolean) {
        viewModelScope.launch {
            val config = DesignerLeadAlertConfig(
                designerUserId = _currentUser.value.id,
                monitoredSkills = monitoredSkills,
                minBudget = minBudget,
                preferredWebsiteTypes = preferredTypes,
                isEnabled = isEnabled
            )
            repository.saveLeadAlertConfig(config)
            showLeadAlertsDialog.value = false
            _snackbarMessage.emit("Smart Lead Radar preferences saved!")
        }
    }

    fun addPortfolioProject(title: String, category: String, description: String, techStack: String, liveUrl: String, metrics: String) {
        viewModelScope.launch {
            val project = PortfolioProject(
                id = "port_${System.currentTimeMillis()}",
                designerUserId = _currentUser.value.id,
                title = title,
                category = category,
                description = description,
                techStack = techStack,
                liveUrl = liveUrl,
                gradientColorIndex = (0..4).random(),
                metricsHighlight = metrics
            )
            repository.addPortfolioProject(project)
            _snackbarMessage.emit("Project '$title' added to your portfolio!")
        }
    }

    fun deletePortfolioProject(id: String) {
        viewModelScope.launch {
            repository.deletePortfolioProject(id)
            _snackbarMessage.emit("Portfolio project removed.")
        }
    }

    fun toggleVerification(userId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleVerification(userId, currentStatus)
            _snackbarMessage.emit("Verification status updated for user.")
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
            _snackbarMessage.emit("All notifications marked as read.")
        }
    }

    fun updateProfile(
        name: String,
        headline: String,
        skills: String,
        startingPrice: Double,
        availability: String,
        location: String,
        about: String
    ) {
        viewModelScope.launch {
            val updated = _currentUser.value.copy(
                name = name,
                headline = headline,
                skills = skills,
                startingPrice = startingPrice,
                availability = availability,
                location = location,
                about = about
            )
            repository.updateUser(updated)
            _currentUser.value = updated
            showEditProfileDialog.value = false
            _snackbarMessage.emit("Profile updated successfully!")
        }
    }

    fun emitSnackbar(message: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(message)
        }
    }
}
