package com.example.dealnest.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CLIENT,
    DESIGNER,
    ADMIN
}

enum class DealStatus {
    PENDING,
    IN_PROGRESS,
    REVIEW,
    COMPLETED
}

enum class ProposalStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

enum class ProjectStatus {
    OPEN,
    IN_PROGRESS,
    COMPLETED
}

enum class MilestoneStatus {
    PENDING,
    IN_PROGRESS,
    SUBMITTED,
    REVISION,
    APPROVED,
    COMPLETED
}

enum class SupportedCurrency(val code: String, val symbol: String, val exchangeRateFromUsd: Double) {
    USD("USD", "$", 1.0),
    EUR("EUR", "€", 0.92),
    GBP("GBP", "£", 0.79),
    CAD("CAD", "CA$", 1.36),
    AUD("AUD", "A$", 1.51),
    JPY("JPY", "¥", 154.0),
    INR("INR", "₹", 83.5)
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: String, // CLIENT, DESIGNER, ADMIN
    val avatarUrl: String = "",
    val headline: String = "",
    val handle: String = "", // e.g. "marcuschen"
    val skills: String = "", // Comma-separated
    val experienceYears: Int = 0,
    val portfolioLinks: String = "",
    val startingPrice: Double = 0.0,
    val availability: String = "Available for new projects",
    val deliveryTimeDays: Int = 7,
    val languages: String = "English",
    val about: String = "",
    val services: String = "Custom Website Design, Webflow/Shopify Build, Responsive Optimization",
    val isVerified: Boolean = false,
    val verificationStatus: String = "Not Verified", // Not Verified, Verification Pending, Verified, Verification Rejected
    val verificationRejectionReason: String = "",
    val location: String = "Remote",
    val timeZone: String = "UTC-5 (EST)",
    val contactPreference: String = "In-App Chat",
    val rating: Float = 5.0f,
    val reviewCount: Int = 0,
    val completedProjectsCount: Int = 0,
    val isPro: Boolean = false,
    val ratingBreakdown5: Int = 5,
    val ratingBreakdown4: Int = 1,
    val ratingBreakdown3: Int = 0,
    val ratingBreakdown2: Int = 0,
    val ratingBreakdown1: Int = 0
)

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val id: String,
    val clientUserId: String,
    val clientName: String,
    val clientLocation: String = "United States",
    val title: String,
    val websiteType: String, // E-Commerce, SaaS, Portfolio, Landing Page, Corporate, Booking
    val description: String,
    val budgetMin: Double,
    val budgetMax: Double,
    val deadlineDays: Int,
    val requiredSkills: String, // Comma-separated: Webflow, Next.js, Figma, etc.
    val pageCount: Int,
    val requiredFeatures: String, // Comma-separated
    val referenceLinks: String = "",
    val status: String = ProjectStatus.OPEN.name,
    val createdAt: Long = System.currentTimeMillis(),
    val isFeatured: Boolean = false
)

@Entity(tableName = "proposals")
data class Proposal(
    @PrimaryKey val id: String,
    val projectId: String,
    val projectTitle: String,
    val designerUserId: String,
    val designerName: String,
    val designerHeadline: String,
    val proposedPrice: Double,
    val deliveryDays: Int,
    val coverMessage: String,
    val portfolioLink: String,
    val status: String = ProposalStatus.PENDING.name,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "deals")
data class Deal(
    @PrimaryKey val id: String,
    val projectId: String,
    val projectTitle: String,
    val clientUserId: String,
    val clientName: String,
    val designerUserId: String,
    val designerName: String,
    val agreedPrice: Double,
    val deadline: String,
    val status: String = DealStatus.IN_PROGRESS.name, // PENDING, IN_PROGRESS, REVIEW, COMPLETED
    val milestonesSummary: String = "1. Wireframes, 2. Design System, 3. Dev Build, 4. Final Review",
    val completedMilestonesCount: Int = 1,
    val totalMilestonesCount: Int = 4,
    val sharedFiles: String = "Design_Specs_v1.fig, Sitemap_Brief.pdf",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "deal_milestones")
data class DealMilestone(
    @PrimaryKey val id: String,
    val dealId: String,
    val title: String,
    val description: String,
    val amount: Double,
    val deadline: String,
    val status: String = MilestoneStatus.PENDING.name,
    val orderIndex: Int = 0
)

@Entity(tableName = "portfolio_projects")
data class PortfolioProject(
    @PrimaryKey val id: String,
    val designerUserId: String,
    val title: String,
    val category: String,
    val description: String,
    val techStack: String,
    val liveUrl: String = "",
    val gradientColorIndex: Int = 0,
    val metricsHighlight: String = "",
    val completionDate: String = "2024"
)

@Entity(tableName = "designer_services")
data class DesignerService(
    @PrimaryKey val id: String,
    val designerUserId: String,
    val serviceName: String,
    val description: String,
    val startingPrice: Double,
    val deliveryDays: Int = 7
)

@Entity(tableName = "verification_requests")
data class VerificationRequest(
    @PrimaryKey val id: String,
    val designerUserId: String,
    val designerName: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val professionalRole: String,
    val skills: String,
    val portfolioUrl: String,
    val description: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val rejectionReason: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)

@Entity(tableName = "lead_alert_configs")
data class DesignerLeadAlertConfig(
    @PrimaryKey val designerUserId: String,
    val monitoredSkills: String = "React, Next.js, WordPress, Shopify, UI/UX, E-commerce, HTML/CSS",
    val minBudget: Double = 1000.0,
    val maxBudget: Double = 10000.0,
    val preferredWebsiteTypes: String = "E-Commerce, SaaS, Corporate",
    val preferredLanguages: String = "English",
    val availabilityFilter: String = "Available for new projects",
    val locationPreference: String = "Any Location",
    val isEnabled: Boolean = true
)

@Entity(tableName = "blocked_users")
data class BlockedUser(
    @PrimaryKey val id: String,
    val blockedByUserId: String,
    val blockedUserId: String,
    val blockedUserName: String,
    val reason: String = "Unsolicited behavior",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String,
    val projectId: String,
    val projectTitle: String,
    val clientUserId: String,
    val clientName: String,
    val clientAvatarUrl: String = "",
    val designerUserId: String,
    val designerName: String,
    val designerAvatarUrl: String = "",
    val designerHeadline: String = "",
    val dealId: String? = null,
    val dealStatus: String? = null, // e.g. "IN_PROGRESS", "COMPLETED"
    val agreedPrice: Double? = null,
    val budget: String = "",
    val deadline: String = "",
    val projectStatus: String = "OPEN",
    val lastMessageText: String = "",
    val lastMessageSenderId: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCountClient: Int = 0,
    val unreadCountDesigner: Int = 0,
    val isDesignerOnline: Boolean = true,
    val isClientOnline: Boolean = true,
    val designerLastSeen: String = "Online",
    val clientLastSeen: String = "Online"
)

@Entity(tableName = "messages")
data class ChatMessage(
    @PrimaryKey val id: String,
    val dealOrChannelId: String,
    val senderUserId: String,
    val senderName: String,
    val senderRole: String,
    val text: String,
    val attachmentName: String? = null,
    val attachmentType: String? = null, // "IMAGE", "DOCUMENT", "FIGMA"
    val replyToMessageId: String? = null,
    val replyToText: String? = null,
    val replyToSenderName: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey val id: String,
    val dealId: String,
    val targetUserId: String,
    val authorUserId: String,
    val authorName: String,
    val authorRole: String,
    val rating: Int,
    val reviewText: String,
    val projectTitle: String = "Website Delivery",
    val categoryFeedback: String = "High Quality, Responsive Communication",
    val timestamp: Long = System.currentTimeMillis(),
    val isReported: Boolean = false,
    val reportReason: String = ""
)

@Entity(tableName = "notifications")
data class AppNotification(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "GENERAL" // PROPOSAL, DEAL, MESSAGE, MATCH, MILESTONE, REVIEW, VERIFICATION
)

@Entity(tableName = "favorites")
data class FavoriteItem(
    @PrimaryKey val id: String,
    val itemType: String, // DESIGNER, PROJECT
    val targetId: String
)

@Entity(tableName = "reports")
data class ReportItem(
    @PrimaryKey val id: String,
    val reportedByUserId: String,
    val targetType: String, // USER, PROJECT, REVIEW, PORTFOLIO
    val targetId: String,
    val targetName: String,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SearchType {
    ALL, PROJECTS, DESIGNERS
}

data class SearchFilterState(
    val searchQuery: String = "",
    val searchType: SearchType = SearchType.ALL,
    val minBudget: Double? = null,
    val maxBudget: Double? = null,
    val deadlineDaysMax: Int? = null,
    val websiteType: String? = null,
    val requiredSkill: String? = null,
    val location: String? = null,
    val projectStatus: String? = null,
    val minStartingPrice: Double? = null,
    val maxStartingPrice: Double? = null,
    val minExperienceYears: Int? = null,
    val minRating: Float? = null,
    val verifiedOnly: Boolean = false,
    val availableOnly: Boolean = false,
    val sortOption: String = "Relevance"
) {
    fun hasActiveFilters(): Boolean {
        return searchQuery.isNotBlank() ||
                minBudget != null ||
                maxBudget != null ||
                deadlineDaysMax != null ||
                websiteType != null ||
                requiredSkill != null ||
                location != null ||
                projectStatus != null ||
                minStartingPrice != null ||
                maxStartingPrice != null ||
                minExperienceYears != null ||
                minRating != null ||
                verifiedOnly ||
                availableOnly ||
                sortOption != "Relevance"
    }
}

// ------------------------------------
// PHASE 5+ DATA MODELS & ENTITIES
// ------------------------------------

@Entity(tableName = "disputes")
data class DisputeItem(
    @PrimaryKey val id: String,
    val dealId: String,
    val projectTitle: String,
    val raisedByUserId: String,
    val raisedByName: String,
    val raisedByRole: String, // CLIENT or DESIGNER
    val againstUserId: String,
    val againstUserName: String,
    val milestoneTitle: String = "Project Delivery & Milestones",
    val reason: String, // Scope disagreement, Missed deadline, Quality dissatisfaction, Communication breakdown, Other
    val description: String,
    val evidenceNotes: String = "",
    val status: String = "Open", // Open, Under Review, Resolved, Closed
    val adminDecisionNotes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payment_transactions")
data class PaymentTransaction(
    @PrimaryKey val id: String,
    val dealId: String,
    val milestoneId: String? = null,
    val projectTitle: String,
    val payerUserId: String,
    val payerName: String,
    val payeeUserId: String,
    val payeeName: String,
    val amount: Double,
    val currency: String = "USD",
    val status: String = "Paid", // Pending, Processing, Paid, Failed, Refunded
    val paymentMethod: String = "Escrow Demo", // Escrow Demo, Credit Card Demo, Wire Demo
    val transactionRef: String = "TXN-DN-${System.currentTimeMillis()}",
    val timestamp: Long = System.currentTimeMillis()
)

data class InvoiceMilestoneItem(
    val title: String,
    val amount: Double,
    val status: String
)

data class DealInvoice(
    val invoiceNumber: String,
    val date: String,
    val dealId: String,
    val projectTitle: String,
    val projectDescription: String,
    val clientName: String,
    val clientContact: String,
    val designerName: String,
    val designerContact: String,
    val agreedAmount: Double,
    val milestoneBreakdown: List<InvoiceMilestoneItem>,
    val totalAmount: Double,
    val status: String = "Paid", // Paid, Pending, Demo
    val currencyCode: String = "USD",
    val currencySymbol: String = "$"
)

data class AiClientRequirementBrief(
    val projectTitle: String,
    val websiteType: String,
    val businessCategory: String,
    val suggestedPages: List<String>,
    val pageCount: Int,
    val suggestedFeatures: List<String>,
    val requiredSkills: List<String>,
    val estimatedComplexity: String, // Low, Moderate, High, Enterprise
    val suggestedQuestionsForClient: List<String>,
    val suggestedDeadlineDays: Int,
    val suggestedBudgetMin: Double,
    val suggestedBudgetMax: Double,
    val generatedDescription: String
)

data class AiConversationSummary(
    val requirements: List<String>,
    val price: String, // Discussed price or "Not specified in conversation."
    val deadline: String, // Discussed deadline or "Not specified in conversation."
    val pendingQuestions: List<String>,
    val nextSteps: List<String>
)

data class ClientAnalyticsData(
    val projectsPosted: Int = 3,
    val proposalsReceived: Int = 14,
    val designersContacted: Int = 6,
    val activeDeals: Int = 1,
    val completedProjects: Int = 4,
    val totalProjectSpendingUsd: Double = 8400.0,
    val monthlySpendingUsd: List<Double> = listOf(1400.0, 2100.0, 1600.0, 1900.0, 1400.0)
)

data class ReferralEntry(
    val userName: String,
    val date: String,
    val status: String, // "Signed Up", "Deal Completed", "Reward Credited"
    val rewardEarned: String
)

data class ReferralStats(
    val referralCode: String,
    val totalReferrals: Int = 4,
    val activeReferrals: Int = 3,
    val rewardCredits: Double = 150.0,
    val referralHistory: List<ReferralEntry> = listOf(
        ReferralEntry("Alex Turner", "Sep 18, 2026", "Deal Completed", "+$50 Credit"),
        ReferralEntry("Maya Lin", "Sep 21, 2026", "Deal Completed", "+$50 Credit"),
        ReferralEntry("Liam Vance", "Sep 23, 2026", "Deal Completed", "+$50 Credit"),
        ReferralEntry("Jordan Reed", "Sep 24, 2026", "Signed Up", "Pending First Deal")
    )
)

data class NotificationPreferences(
    val messages: Boolean = true,
    val projects: Boolean = true,
    val proposals: Boolean = true,
    val deals: Boolean = true,
    val milestones: Boolean = true,
    val reviews: Boolean = true,
    val verification: Boolean = true,
    val aiMatches: Boolean = true,
    val payments: Boolean = true,
    val disputes: Boolean = true
)

enum class SupportedLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी"),
    SPANISH("es", "Spanish", "Español"),
    FRENCH("fr", "French", "Français"),
    ARABIC("ar", "Arabic", "العربية"),
    GERMAN("de", "German", "Deutsch")
}

