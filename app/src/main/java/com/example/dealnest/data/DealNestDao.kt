package com.example.dealnest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dealnest.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DealNestDao {

    // Projects
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)

    // Users
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = 'DESIGNER' ORDER BY rating DESC")
    fun getDesigners(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET isVerified = :isVerified WHERE id = :userId")
    suspend fun setVerificationStatus(userId: String, isVerified: Boolean)

    // Proposals
    @Query("SELECT * FROM proposals ORDER BY createdAt DESC")
    fun getAllProposals(): Flow<List<Proposal>>

    @Query("SELECT * FROM proposals WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getProposalsForProject(projectId: String): Flow<List<Proposal>>

    @Query("SELECT * FROM proposals WHERE designerUserId = :designerId ORDER BY createdAt DESC")
    fun getProposalsForDesigner(designerId: String): Flow<List<Proposal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProposal(proposal: Proposal)

    @Update
    suspend fun updateProposal(proposal: Proposal)

    // Deals
    @Query("SELECT * FROM deals ORDER BY lastUpdated DESC")
    fun getAllDeals(): Flow<List<Deal>>

    @Query("SELECT * FROM deals WHERE id = :id LIMIT 1")
    fun getDealById(id: String): Flow<Deal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeal(deal: Deal)

    @Update
    suspend fun updateDeal(deal: Deal)

    // Deal Milestones
    @Query("SELECT * FROM deal_milestones WHERE dealId = :dealId ORDER BY orderIndex ASC")
    fun getMilestonesForDeal(dealId: String): Flow<List<DealMilestone>>

    @Query("SELECT * FROM deal_milestones ORDER BY orderIndex ASC")
    fun getAllMilestones(): Flow<List<DealMilestone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<DealMilestone>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: DealMilestone)

    @Update
    suspend fun updateMilestone(milestone: DealMilestone)

    // Portfolio Projects
    @Query("SELECT * FROM portfolio_projects WHERE designerUserId = :designerId")
    fun getPortfolioProjectsForDesigner(designerId: String): Flow<List<PortfolioProject>>

    @Query("SELECT * FROM portfolio_projects")
    fun getAllPortfolioProjects(): Flow<List<PortfolioProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioProjects(projects: List<PortfolioProject>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioProject(project: PortfolioProject)

    @Query("DELETE FROM portfolio_projects WHERE id = :id")
    suspend fun deletePortfolioProject(id: String)

    // Designer Lead Alert Config
    @Query("SELECT * FROM lead_alert_configs WHERE designerUserId = :designerId LIMIT 1")
    fun getLeadAlertConfig(designerId: String): Flow<DesignerLeadAlertConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeadAlertConfig(config: DesignerLeadAlertConfig)

    // Blocked Users
    @Query("SELECT * FROM blocked_users WHERE blockedByUserId = :userId")
    fun getBlockedUsers(userId: String): Flow<List<BlockedUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedUser(blocked: BlockedUser)

    @Query("DELETE FROM blocked_users WHERE blockedByUserId = :userId AND blockedUserId = :targetUserId")
    suspend fun unblockUser(userId: String, targetUserId: String)

    // Conversations
    @Query("SELECT * FROM conversations ORDER BY lastMessageTimestamp DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE clientUserId = :userId OR designerUserId = :userId ORDER BY lastMessageTimestamp DESC")
    fun getConversationsForUser(userId: String): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    fun getConversationById(id: String): Flow<Conversation?>

    @Query("SELECT * FROM conversations WHERE projectId = :projectId AND designerUserId = :designerId LIMIT 1")
    suspend fun findConversationByProjectAndDesigner(projectId: String, designerId: String): Conversation?

    @Query("SELECT * FROM conversations WHERE dealId = :dealId LIMIT 1")
    suspend fun findConversationByDealId(dealId: String): Conversation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<Conversation>)

    @Update
    suspend fun updateConversation(conversation: Conversation)

    @Query("UPDATE conversations SET dealId = :dealId, dealStatus = :dealStatus, agreedPrice = :agreedPrice WHERE id = :conversationId")
    suspend fun linkDealToConversation(conversationId: String, dealId: String, dealStatus: String, agreedPrice: Double)

    @Query("UPDATE conversations SET unreadCountClient = 0 WHERE id = :conversationId")
    suspend fun markConversationReadForClient(conversationId: String)

    @Query("UPDATE conversations SET unreadCountDesigner = 0 WHERE id = :conversationId")
    suspend fun markConversationReadForDesigner(conversationId: String)

    // Messages
    @Query("SELECT * FROM messages WHERE dealOrChannelId = :dealOrChannelId ORDER BY timestamp ASC")
    fun getMessagesForDeal(dealOrChannelId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("UPDATE messages SET isRead = 1 WHERE dealOrChannelId = :channelId")
    suspend fun markMessagesAsReadForChannel(channelId: String)

    // Reviews
    @Query("SELECT * FROM reviews WHERE targetUserId = :userId ORDER BY timestamp DESC")
    fun getReviewsForUser(userId: String): Flow<List<Review>>

    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE dealId = :dealId")
    fun getReviewsForDeal(dealId: String): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteReviewById(reviewId: String)

    @Query("UPDATE reviews SET isReported = 1, reportReason = :reason WHERE id = :reviewId")
    suspend fun reportReview(reviewId: String, reason: String)

    @Query("UPDATE users SET rating = :rating, reviewCount = :reviewCount, ratingBreakdown5 = :r5, ratingBreakdown4 = :r4, ratingBreakdown3 = :r3, ratingBreakdown2 = :r2, ratingBreakdown1 = :r1 WHERE id = :userId")
    suspend fun updateUserRatingAndBreakdown(userId: String, rating: Float, reviewCount: Int, r5: Int, r4: Int, r3: Int, r2: Int, r1: Int)

    @Query("UPDATE users SET isVerified = :isVerified, verificationStatus = :status, verificationRejectionReason = :rejectionReason WHERE id = :userId")
    suspend fun updateUserVerificationStatus(userId: String, isVerified: Boolean, status: String, rejectionReason: String = "")

    // Verification Requests
    @Query("SELECT * FROM verification_requests ORDER BY submittedAt DESC")
    fun getAllVerificationRequests(): Flow<List<VerificationRequest>>

    @Query("SELECT * FROM verification_requests WHERE designerUserId = :designerUserId LIMIT 1")
    fun getVerificationRequestForDesigner(designerUserId: String): Flow<VerificationRequest?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerificationRequest(request: VerificationRequest)

    @Update
    suspend fun updateVerificationRequest(request: VerificationRequest)

    // Designer Services
    @Query("SELECT * FROM designer_services WHERE designerUserId = :designerUserId")
    fun getServicesForDesigner(designerUserId: String): Flow<List<DesignerService>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: DesignerService)

    @Query("DELETE FROM designer_services WHERE id = :serviceId")
    suspend fun deleteService(serviceId: String)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: String)

    // Favorites
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteItem)

    @Query("DELETE FROM favorites WHERE targetId = :targetId")
    suspend fun deleteFavoriteByTargetId(targetId: String)

    // Reports
    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportItem)

    @Query("DELETE FROM reports WHERE id = :reportId")
    suspend fun deleteReportById(reportId: String)

    // Disputes
    @Query("SELECT * FROM disputes ORDER BY timestamp DESC")
    fun getAllDisputes(): Flow<List<DisputeItem>>

    @Query("SELECT * FROM disputes WHERE dealId = :dealId ORDER BY timestamp DESC")
    fun getDisputesForDeal(dealId: String): Flow<List<DisputeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispute(dispute: DisputeItem)

    @Update
    suspend fun updateDispute(dispute: DisputeItem)

    @Query("UPDATE disputes SET status = :status, adminDecisionNotes = :decisionNotes, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateDisputeStatus(id: String, status: String, decisionNotes: String, updatedAt: Long = System.currentTimeMillis())

    // Payment Transactions
    @Query("SELECT * FROM payment_transactions ORDER BY timestamp DESC")
    fun getAllPaymentTransactions(): Flow<List<PaymentTransaction>>

    @Query("SELECT * FROM payment_transactions WHERE dealId = :dealId ORDER BY timestamp DESC")
    fun getPaymentsForDeal(dealId: String): Flow<List<PaymentTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentTransaction(transaction: PaymentTransaction)

    @Query("SELECT * FROM blocked_users ORDER BY timestamp DESC")
    fun getAllBlockedUsers(): Flow<List<BlockedUser>>
}
