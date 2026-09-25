package com.example.dealnest.data

import com.example.dealnest.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class DealNestRepository(private val dao: DealNestDao) {

    val chatService: ChatService = RoomChatService(dao)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingUsers = dao.getAllUsers().first()
        if (existingUsers.isEmpty()) {
            dao.insertUsers(SeedData.demoDesigners + SeedData.demoClient)
            SeedData.demoProjects.forEach { dao.insertProject(it) }
            SeedData.demoProposals.forEach { dao.insertProposal(it) }
            SeedData.demoDeals.forEach { dao.insertDeal(it) }
            dao.insertMilestones(SeedData.demoMilestones)
            dao.insertPortfolioProjects(SeedData.demoPortfolioProjects)
            SeedData.demoLeadAlertConfigs.forEach { dao.insertLeadAlertConfig(it) }
            dao.insertConversations(SeedData.demoConversations)
            SeedData.demoMessages.forEach { dao.insertMessage(it) }
            SeedData.demoReviews.forEach { dao.insertReview(it) }
            SeedData.demoNotifications.forEach { dao.insertNotification(it) }
            SeedData.demoVerificationRequests.forEach { dao.insertVerificationRequest(it) }
            SeedData.demoDesignerServices.forEach { dao.insertService(it) }
            SeedData.demoDisputes.forEach { dao.insertDispute(it) }
            SeedData.demoPaymentTransactions.forEach { dao.insertPaymentTransaction(it) }
            dao.insertFavorite(FavoriteItem(id = "fav_1", itemType = "DESIGNER", targetId = "des_1"))
            dao.insertFavorite(FavoriteItem(id = "fav_2", itemType = "PROJECT", targetId = "proj_1"))
        } else {
            // Ensure milestones and portfolio projects are populated if added in migration
            val existingMilestones = dao.getAllMilestones().first()
            if (existingMilestones.isEmpty()) {
                dao.insertMilestones(SeedData.demoMilestones)
            }
            val existingPortfolio = dao.getAllPortfolioProjects().first()
            if (existingPortfolio.isEmpty()) {
                dao.insertPortfolioProjects(SeedData.demoPortfolioProjects)
            }
            val existingConversations = dao.getAllConversations().first()
            if (existingConversations.isEmpty()) {
                dao.insertConversations(SeedData.demoConversations)
                SeedData.demoMessages.forEach { dao.insertMessage(it) }
            }
            val existingVerReqs = dao.getAllVerificationRequests().first()
            if (existingVerReqs.isEmpty()) {
                SeedData.demoVerificationRequests.forEach { dao.insertVerificationRequest(it) }
            }
            val existingServices = dao.getServicesForDesigner("des_2").first()
            if (existingServices.isEmpty()) {
                SeedData.demoDesignerServices.forEach { dao.insertService(it) }
            }
            val existingDisputes = dao.getAllDisputes().first()
            if (existingDisputes.isEmpty()) {
                SeedData.demoDisputes.forEach { dao.insertDispute(it) }
            }
            val existingPayments = dao.getAllPaymentTransactions().first()
            if (existingPayments.isEmpty()) {
                SeedData.demoPaymentTransactions.forEach { dao.insertPaymentTransaction(it) }
            }
            SeedData.demoLeadAlertConfigs.forEach { dao.insertLeadAlertConfig(it) }
        }
    }

    // Projects
    val allProjects: Flow<List<Project>> = dao.getAllProjects()

    suspend fun createProject(
        title: String,
        websiteType: String,
        description: String,
        budgetMin: Double,
        budgetMax: Double,
        deadlineDays: Int,
        requiredSkills: String,
        pageCount: Int,
        requiredFeatures: String,
        referenceLinks: String,
        clientUser: User
    ): Project {
        val newProj = Project(
            id = "proj_${System.currentTimeMillis()}",
            clientUserId = clientUser.id,
            clientName = clientUser.name,
            clientLocation = clientUser.location,
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
            status = ProjectStatus.OPEN.name,
            createdAt = System.currentTimeMillis()
        )
        dao.insertProject(newProj)

        // General notification
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Project Posted!",
                description = "Your project '$title' is now live for top designers to submit proposals.",
                type = "MATCH"
            )
        )

        // Check against designer lead alert configs for smart lead alerts
        checkAndDispatchLeadAlerts(newProj)

        return newProj
    }

    private suspend fun checkAndDispatchLeadAlerts(project: Project) {
        val designers = dao.getDesigners().first()
        for (designer in designers) {
            val alertConfig = dao.getLeadAlertConfig(designer.id).first()
            if (alertConfig != null && alertConfig.isEnabled) {
                val matchesBudget = project.budgetMax >= alertConfig.minBudget && project.budgetMin <= alertConfig.maxBudget
                val skills = alertConfig.monitoredSkills.lowercase(Locale.ROOT).split(",").map { it.trim() }
                val projectSkills = project.requiredSkills.lowercase(Locale.ROOT)
                val matchesSkill = skills.any { it.isNotBlank() && projectSkills.contains(it) }

                if (matchesBudget && matchesSkill) {
                    val matchCalc = calculateSmartMatch(project, designer)
                    dao.insertNotification(
                        AppNotification(
                            id = "notif_${UUID.randomUUID()}",
                            title = "🔥 New project matches your skills",
                            description = "${project.title} • Budget: $${project.budgetMin.toInt()}-$${project.budgetMax.toInt()} • ${project.deadlineDays}d • ${matchCalc.percentage}% Match • Skills: ${project.requiredSkills}",
                            type = "MATCH"
                        )
                    )
                }
            }
        }
    }

    // Designers & Users
    val allDesigners: Flow<List<User>> = dao.getDesigners()
    val allUsers: Flow<List<User>> = dao.getAllUsers()

    fun getUserById(id: String): Flow<User?> = dao.getUserById(id)

    suspend fun updateUser(user: User) = dao.updateUser(user)

    suspend fun toggleVerification(userId: String, currentStatus: Boolean) {
        dao.setVerificationStatus(userId, !currentStatus)
    }

    // Proposals
    val allProposals: Flow<List<Proposal>> = dao.getAllProposals()

    fun getProposalsForProject(projectId: String): Flow<List<Proposal>> =
        dao.getProposalsForProject(projectId)

    fun getProposalsForDesigner(designerId: String): Flow<List<Proposal>> =
        dao.getProposalsForDesigner(designerId)

    suspend fun submitProposal(
        projectId: String,
        projectTitle: String,
        designer: User,
        proposedPrice: Double,
        deliveryDays: Int,
        coverMessage: String,
        portfolioLink: String
    ): Proposal {
        val proposal = Proposal(
            id = "prop_${UUID.randomUUID()}",
            projectId = projectId,
            projectTitle = projectTitle,
            designerUserId = designer.id,
            designerName = designer.name,
            designerHeadline = designer.headline,
            proposedPrice = proposedPrice,
            deliveryDays = deliveryDays,
            coverMessage = coverMessage,
            portfolioLink = portfolioLink,
            status = ProposalStatus.PENDING.name,
            createdAt = System.currentTimeMillis()
        )
        dao.insertProposal(proposal)

        // 1. Automatically create/connect private conversation between client & designer for this project
        val project = dao.getAllProjects().first().firstOrNull { it.id == projectId }
        val clientUser = if (project != null) {
            dao.getUserById(project.clientUserId).first() ?: User(
                id = project.clientUserId,
                name = project.clientName,
                email = "",
                role = UserRole.CLIENT.name
            )
        } else {
            User(id = "client_me", name = "Client", email = "", role = UserRole.CLIENT.name)
        }

        val conv = chatService.getOrCreateConversation(
            projectId = projectId,
            projectTitle = projectTitle,
            clientUser = clientUser,
            designerUser = designer,
            budget = if (project != null) "$${project.budgetMin.toInt()} - $${project.budgetMax.toInt()}" else "$$proposedPrice",
            deadline = "$deliveryDays days"
        )

        chatService.sendMessage(
            conversationId = conv.id,
            senderUser = designer,
            text = "📄 Submitted Proposal: $$proposedPrice ($deliveryDays days turnaround)\n\n$coverMessage\n\nPortfolio: $portfolioLink",
            attachmentName = "Proposal_Summary.pdf",
            attachmentType = "DOCUMENT"
        )

        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Proposal Sent!",
                description = "You offered $$proposedPrice for '$projectTitle'. Private chat created with client.",
                type = "PROPOSAL"
            )
        )
        return proposal
    }

    suspend fun acceptProposal(proposal: Proposal, clientUser: User): Deal {
        val updatedProposal = proposal.copy(status = ProposalStatus.ACCEPTED.name)
        dao.updateProposal(updatedProposal)

        // Create deal room entry
        val dealId = "deal_${UUID.randomUUID().toString().take(8)}"
        val newDeal = Deal(
            id = dealId,
            projectId = proposal.projectId,
            projectTitle = proposal.projectTitle,
            clientUserId = clientUser.id,
            clientName = clientUser.name,
            designerUserId = proposal.designerUserId,
            designerName = proposal.designerName,
            agreedPrice = proposal.proposedPrice,
            deadline = "${proposal.deliveryDays} days from agreement",
            status = DealStatus.IN_PROGRESS.name,
            milestonesSummary = "4 Structured Milestones Initialized",
            completedMilestonesCount = 0,
            totalMilestonesCount = 4,
            sharedFiles = "Project_Requirements.pdf",
            lastUpdated = System.currentTimeMillis()
        )
        dao.insertDeal(newDeal)

        // Link conversation to Deal Room & post Deal Active message
        val conv = dao.findConversationByProjectAndDesigner(proposal.projectId, proposal.designerUserId)
        val convId = conv?.id ?: "conv_${proposal.projectId}_${proposal.designerUserId}"
        dao.linkDealToConversation(convId, dealId, "DEAL_ACTIVE", proposal.proposedPrice)

        chatService.sendMessage(
            conversationId = convId,
            senderUser = clientUser,
            text = "🎉 Deal Active! Proposal accepted at $${proposal.proposedPrice.toInt()}. 4 Milestones initialized. Milestone 1 (Wireframes) has been kicked off.",
            attachmentName = "Project_Requirements.pdf",
            attachmentType = "DOCUMENT"
        )

        // Create the 4 structured milestones
        val price = proposal.proposedPrice
        val m1 = (price * 0.20).toInt().toDouble()
        val m2 = (price * 0.30).toInt().toDouble()
        val m3 = (price * 0.35).toInt().toDouble()
        val m4 = (price - (m1 + m2 + m3))

        val milestones = listOf(
            DealMilestone(
                id = "ms_${dealId}_1",
                dealId = dealId,
                title = "1. Information Architecture & UX Wireframes",
                description = "Sitemap, user flows, and wireframes for core layouts.",
                amount = m1,
                deadline = "Day 4",
                status = MilestoneStatus.IN_PROGRESS.name,
                orderIndex = 1
            ),
            DealMilestone(
                id = "ms_${dealId}_2",
                dealId = dealId,
                title = "2. High-Fidelity UI Design & Design System",
                description = "Figma prototypes, typography system, responsive cards, and component tokens.",
                amount = m2,
                deadline = "Day 8",
                status = MilestoneStatus.PENDING.name,
                orderIndex = 2
            ),
            DealMilestone(
                id = "ms_${dealId}_3",
                dealId = dealId,
                title = "3. Frontend & CMS/Backend Development",
                description = "Complete clean code implementation, CMS binding, and animations.",
                amount = m3,
                deadline = "Day ${proposal.deliveryDays - 3}",
                status = MilestoneStatus.PENDING.name,
                orderIndex = 3
            ),
            DealMilestone(
                id = "ms_${dealId}_4",
                dealId = dealId,
                title = "4. QA Cross-Device Testing & Final Handoff",
                description = "Speed audit 95+, domain SSL connection, and assets handoff.",
                amount = m4,
                deadline = "Day ${proposal.deliveryDays}",
                status = MilestoneStatus.PENDING.name,
                orderIndex = 4
            )
        )
        dao.insertMilestones(milestones)

        // Initial welcome message in Deal Room chat
        dao.insertMessage(
            ChatMessage(
                id = "msg_${UUID.randomUUID()}",
                dealOrChannelId = newDeal.id,
                senderUserId = clientUser.id,
                senderName = clientUser.name,
                senderRole = UserRole.CLIENT.name,
                text = "Welcome to the Deal Room! Proposal accepted at $${proposal.proposedPrice}. Milestone 1 (Wireframes) has been kicked off.",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Proposal Accepted!",
                description = "Deal Room & 4 Milestones opened for '${proposal.projectTitle}'.",
                type = "DEAL"
            )
        )
        return newDeal
    }

    suspend fun rejectProposal(proposal: Proposal) {
        dao.updateProposal(proposal.copy(status = ProposalStatus.REJECTED.name))
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Proposal Status",
                description = "Proposal for '${proposal.projectTitle}' was declined.",
                type = "PROPOSAL"
            )
        )
    }

    // Deals & Milestones
    val allDeals: Flow<List<Deal>> = dao.getAllDeals()

    fun getDealById(id: String): Flow<Deal?> = dao.getDealById(id)

    fun getMilestonesForDeal(dealId: String): Flow<List<DealMilestone>> =
        dao.getMilestonesForDeal(dealId)

    suspend fun updateDealStatus(deal: Deal, newStatus: DealStatus) {
        val updated = deal.copy(
            status = newStatus.name,
            lastUpdated = System.currentTimeMillis()
        )
        dao.updateDeal(updated)
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Deal Status Updated",
                description = "'${deal.projectTitle}' is now marked as ${newStatus.name.replace("_", " ")}.",
                type = "DEAL"
            )
        )
    }

    suspend fun updateMilestoneStatus(
        deal: Deal,
        milestone: DealMilestone,
        newStatus: MilestoneStatus
    ) {
        val updatedMilestone = milestone.copy(status = newStatus.name)
        dao.updateMilestone(updatedMilestone)

        // Fetch all milestones for this deal to sync completion count
        val allDealMilestones = dao.getMilestonesForDeal(deal.id).first()
        val completedCount = allDealMilestones.count {
            it.id == milestone.id && (newStatus == MilestoneStatus.APPROVED || newStatus == MilestoneStatus.COMPLETED) ||
            it.id != milestone.id && (it.status == MilestoneStatus.APPROVED.name || it.status == MilestoneStatus.COMPLETED.name)
        }

        val dealStatus = if (completedCount >= deal.totalMilestonesCount) {
            DealStatus.COMPLETED.name
        } else {
            DealStatus.IN_PROGRESS.name
        }

        val updatedDeal = deal.copy(
            completedMilestonesCount = completedCount,
            status = dealStatus,
            lastUpdated = System.currentTimeMillis()
        )
        dao.updateDeal(updatedDeal)

        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Milestone ${newStatus.name.replace("_", " ")}",
                description = "${milestone.title} is now marked as ${newStatus.name.replace("_", " ")} in '${deal.projectTitle}'.",
                type = "MILESTONE"
            )
        )
    }

    suspend fun addSharedFileToDeal(deal: Deal, fileName: String) {
        val currentFiles = if (deal.sharedFiles.isBlank()) fileName else "${deal.sharedFiles}, $fileName"
        dao.updateDeal(deal.copy(sharedFiles = currentFiles, lastUpdated = System.currentTimeMillis()))
    }

    // Portfolio Projects
    fun getPortfolioProjectsForDesigner(designerId: String): Flow<List<PortfolioProject>> =
        dao.getPortfolioProjectsForDesigner(designerId)

    suspend fun addPortfolioProject(project: PortfolioProject) =
        dao.insertPortfolioProject(project)

    suspend fun deletePortfolioProject(id: String) =
        dao.deletePortfolioProject(id)

    // Lead Alert Config
    fun getLeadAlertConfig(designerId: String): Flow<DesignerLeadAlertConfig?> =
        dao.getLeadAlertConfig(designerId)

    suspend fun saveLeadAlertConfig(config: DesignerLeadAlertConfig) {
        dao.insertLeadAlertConfig(config)
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Lead Radar Updated",
                description = "Alerts configured for min budget $${config.minBudget.toInt()} and ${config.monitoredSkills}.",
                type = "MATCH"
            )
        )
    }

    // Blocked Users
    fun getBlockedUsers(userId: String): Flow<List<BlockedUser>> =
        dao.getBlockedUsers(userId)

    suspend fun blockUser(blockedByUserId: String, targetUserId: String, targetUserName: String, reason: String) {
        dao.insertBlockedUser(
            BlockedUser(
                id = "blk_${UUID.randomUUID()}",
                blockedByUserId = blockedByUserId,
                blockedUserId = targetUserId,
                blockedUserName = targetUserName,
                reason = reason
            )
        )
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "User Blocked",
                description = "You have blocked $targetUserName. Their messages and proposals will be hidden.",
                type = "GENERAL"
            )
        )
    }

    suspend fun unblockUser(userId: String, targetUserId: String) {
        dao.unblockUser(userId, targetUserId)
    }

    // Conversations & Messages (via ChatService)
    val allConversations: Flow<List<Conversation>> = dao.getAllConversations()

    fun getConversationsForUser(userId: String): Flow<List<Conversation>> =
        dao.getConversationsForUser(userId)

    fun getConversationById(id: String): Flow<Conversation?> =
        dao.getConversationById(id)

    fun getMessagesForDeal(dealId: String): Flow<List<ChatMessage>> =
        dao.getMessagesForDeal(dealId)

    suspend fun sendChatMessage(
        conversationId: String,
        senderUser: User,
        text: String,
        attachmentName: String? = null,
        attachmentType: String? = null,
        replyToMessage: ChatMessage? = null
    ): ChatMessage {
        return chatService.sendMessage(
            conversationId = conversationId,
            senderUser = senderUser,
            text = text,
            attachmentName = attachmentName,
            attachmentType = attachmentType,
            replyToMessage = replyToMessage
        )
    }

    suspend fun markConversationRead(conversationId: String, userId: String) {
        chatService.markConversationAsRead(conversationId, userId)
    }

    suspend fun deleteChatMessage(messageId: String) {
        chatService.deleteMessage(messageId)
    }

    suspend fun startConversation(
        projectId: String,
        projectTitle: String,
        clientUser: User,
        designerUser: User,
        budget: String,
        deadline: String,
        dealId: String? = null,
        initialMessageText: String? = null
    ): Conversation {
        return chatService.getOrCreateConversation(
            projectId = projectId,
            projectTitle = projectTitle,
            clientUser = clientUser,
            designerUser = designerUser,
            budget = budget,
            deadline = deadline,
            dealId = dealId,
            initialMessageText = initialMessageText
        )
    }

    suspend fun sendMessage(
        dealId: String,
        senderUser: User,
        text: String,
        attachmentName: String? = null
    ) {
        chatService.sendMessage(
            conversationId = dealId,
            senderUser = senderUser,
            text = text,
            attachmentName = attachmentName
        )
    }

    // Reviews
    val allReviews: Flow<List<Review>> = dao.getAllReviews()
    fun getReviewsForUser(userId: String): Flow<List<Review>> = dao.getReviewsForUser(userId)
    fun getReviewsForDeal(dealId: String): Flow<List<Review>> = dao.getReviewsForDeal(dealId)

    suspend fun submitReview(
        dealId: String,
        targetUserId: String,
        authorUser: User,
        rating: Int,
        reviewText: String,
        projectTitle: String = "Website Delivery",
        categoryFeedback: String = "Quality Delivery"
    ): Result<Boolean> {
        // Rule 1: Cannot review oneself
        if (targetUserId == authorUser.id) {
            return Result.failure(IllegalArgumentException("You cannot review yourself."))
        }

        // Rule 2: Deal must exist and be COMPLETED
        val deal = dao.getAllDeals().first().firstOrNull { it.id == dealId }
            ?: return Result.failure(IllegalArgumentException("Deal not found."))
        if (deal.status != DealStatus.COMPLETED.name) {
            return Result.failure(IllegalStateException("Reviews can only be submitted once the deal is marked COMPLETED."))
        }

        // Rule 3: Only client or designer in this deal can leave a review
        if (authorUser.id != deal.clientUserId && authorUser.id != deal.designerUserId) {
            return Result.failure(SecurityException("Only participants in this deal can leave a review."))
        }

        // Rule 4: One review per user per completed deal
        val existingReviewsForDeal = dao.getReviewsForDeal(dealId).first()
        if (existingReviewsForDeal.any { it.authorUserId == authorUser.id }) {
            return Result.failure(IllegalStateException("You have already submitted a review for this completed deal."))
        }

        val clampedRating = rating.coerceIn(1, 5)
        val review = Review(
            id = "rev_${UUID.randomUUID()}",
            dealId = dealId,
            targetUserId = targetUserId,
            authorUserId = authorUser.id,
            authorName = authorUser.name,
            authorRole = authorUser.role,
            rating = clampedRating,
            reviewText = reviewText.trim(),
            projectTitle = projectTitle.ifBlank { deal.projectTitle },
            categoryFeedback = categoryFeedback,
            timestamp = System.currentTimeMillis()
        )
        dao.insertReview(review)

        // Recalculate target user's rating & star breakdown
        recalculateUserRating(targetUserId)

        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "New Review Received",
                description = "${authorUser.name} gave you a $clampedRating-star rating for '${deal.projectTitle}'.",
                type = "REVIEW"
            )
        )
        return Result.success(true)
    }

    suspend fun reportReview(reviewId: String, reportedByUserId: String, reason: String) {
        dao.reportReview(reviewId, reason)
        dao.insertReport(
            ReportItem(
                id = "rep_${UUID.randomUUID()}",
                reportedByUserId = reportedByUserId,
                targetType = "REVIEW",
                targetId = reviewId,
                targetName = "Inappropriate Review ($reviewId)",
                reason = reason,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteReview(reviewId: String) {
        val allRevs = dao.getAllReviews().first()
        val rev = allRevs.firstOrNull { it.id == reviewId }
        dao.deleteReviewById(reviewId)
        if (rev != null) {
            recalculateUserRating(rev.targetUserId)
        }
    }

    private suspend fun recalculateUserRating(userId: String) {
        val userReviews = dao.getReviewsForUser(userId).first()
        if (userReviews.isEmpty()) return
        val count = userReviews.size
        val avg = userReviews.map { it.rating }.average().toFloat()
        val r5 = userReviews.count { it.rating == 5 }
        val r4 = userReviews.count { it.rating == 4 }
        val r3 = userReviews.count { it.rating == 3 }
        val r2 = userReviews.count { it.rating == 2 }
        val r1 = userReviews.count { it.rating == 1 }
        dao.updateUserRatingAndBreakdown(
            userId = userId,
            rating = (Math.round(avg * 100.0) / 100.0).toFloat(),
            reviewCount = count,
            r5 = r5,
            r4 = r4,
            r3 = r3,
            r2 = r2,
            r1 = r1
        )
    }

    // Verification System
    val allVerificationRequests: Flow<List<VerificationRequest>> = dao.getAllVerificationRequests()

    fun getVerificationRequestForDesigner(userId: String): Flow<VerificationRequest?> =
        dao.getVerificationRequestForDesigner(userId)

    suspend fun submitVerificationRequest(
        designerUser: User,
        fullName: String,
        email: String,
        phone: String,
        professionalRole: String,
        skills: String,
        portfolioUrl: String,
        description: String
    ): VerificationRequest {
        val req = VerificationRequest(
            id = "ver_req_${UUID.randomUUID()}",
            designerUserId = designerUser.id,
            designerName = designerUser.name,
            fullName = fullName.trim(),
            email = email.trim(),
            phone = phone.trim(),
            professionalRole = professionalRole.trim(),
            skills = skills.trim(),
            portfolioUrl = portfolioUrl.trim(),
            description = description.trim(),
            status = "PENDING",
            submittedAt = System.currentTimeMillis()
        )
        dao.insertVerificationRequest(req)
        dao.updateUserVerificationStatus(
            userId = designerUser.id,
            isVerified = false,
            status = "Verification Pending",
            rejectionReason = ""
        )
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Verification Request Submitted",
                description = "Your application is under review by DealNest platform administrators.",
                type = "VERIFICATION"
            )
        )
        return req
    }

    suspend fun approveVerificationRequest(requestId: String, designerUserId: String) {
        val reqs = dao.getAllVerificationRequests().first()
        val req = reqs.firstOrNull { it.id == requestId }
        if (req != null) {
            dao.updateVerificationRequest(
                req.copy(status = "APPROVED", reviewedAt = System.currentTimeMillis())
            )
        }
        dao.updateUserVerificationStatus(
            userId = designerUserId,
            isVerified = true,
            status = "Verified",
            rejectionReason = ""
        )
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Badge Approved: You Are Verified!",
                description = "Congratulations! Your DealNest Verified badge is now active on your public profile and proposals.",
                type = "VERIFICATION"
            )
        )
    }

    suspend fun rejectVerificationRequest(requestId: String, designerUserId: String, reason: String) {
        val reqs = dao.getAllVerificationRequests().first()
        val req = reqs.firstOrNull { it.id == requestId }
        if (req != null) {
            dao.updateVerificationRequest(
                req.copy(
                    status = "REJECTED",
                    rejectionReason = reason,
                    reviewedAt = System.currentTimeMillis()
                )
            )
        }
        dao.updateUserVerificationStatus(
            userId = designerUserId,
            isVerified = false,
            status = "Verification Rejected",
            rejectionReason = reason
        )
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Verification Update",
                description = "Your verification request could not be approved: $reason",
                type = "VERIFICATION"
            )
        )
    }

    // Designer Services
    fun getServicesForDesigner(designerUserId: String): Flow<List<DesignerService>> =
        dao.getServicesForDesigner(designerUserId)

    suspend fun addDesignerService(
        designerUserId: String,
        serviceName: String,
        description: String,
        startingPrice: Double,
        deliveryDays: Int
    ) {
        dao.insertService(
            DesignerService(
                id = "srv_${UUID.randomUUID()}",
                designerUserId = designerUserId,
                serviceName = serviceName,
                description = description,
                startingPrice = startingPrice,
                deliveryDays = deliveryDays
            )
        )
    }

    suspend fun deleteDesignerService(serviceId: String) {
        dao.deleteService(serviceId)
    }

    // Smart Match Engine (Upgraded Phase 5)
    fun calculateSmartMatch(project: Project, designer: User): MatchCalculation {
        var score = 45
        val reasons = mutableListOf<String>()

        // 1. Required Skills
        val projectSkills = project.requiredSkills.split(",").map { it.trim().lowercase() }.filter { it.isNotBlank() }
        val designerSkills = designer.skills.split(",").map { it.trim().lowercase() }.filter { it.isNotBlank() }
        val matchingSkills = projectSkills.filter { ps -> designerSkills.any { ds -> ds.contains(ps) || ps.contains(ds) } }

        if (matchingSkills.isNotEmpty()) {
            val boost = (matchingSkills.size * 10).coerceAtMost(22)
            score += boost
            reasons.add("✓ Required skills: ${matchingSkills.take(3).joinToString(", ") { it.replaceFirstChar { c -> c.uppercase() } }}")
        } else {
            reasons.add("✓ Core web design & layout fundamentals")
        }

        // 2. Similar Portfolio & Website Type Experience
        val typeLower = project.websiteType.lowercase()
        val hasPortfolioFit = designer.services.lowercase().contains(typeLower) ||
                designer.about.lowercase().contains(typeLower) ||
                designer.headline.lowercase().contains(typeLower) ||
                designer.portfolioLinks.isNotBlank()
        if (hasPortfolioFit) {
            score += 12
            reasons.add("✓ Similar portfolio in ${project.websiteType} & responsive UI")
        }

        // 3. Budget Compatibility
        if (designer.startingPrice in 1.0..project.budgetMax) {
            score += 10
            reasons.add("✓ Budget compatibility ($${designer.startingPrice.toInt()} fits within $${project.budgetMin.toInt()}-$${project.budgetMax.toInt()})")
        } else if (designer.startingPrice <= 0) {
            score += 8
            reasons.add("✓ Flexible project rate")
        }

        // 4. Availability
        if (designer.availability.lowercase().contains("available") || designer.availability.lowercase().contains("immediate") || designer.availability.lowercase().contains("spot")) {
            score += 8
            reasons.add("✓ Availability (${designer.availability})")
        } else {
            reasons.add("✓ Standard project queuing capacity")
        }

        // 5. Designer Experience & Track Record
        if (designer.experienceYears >= 3) {
            score += 7
            reasons.add("✓ Experience (${designer.experienceYears}+ years, ${designer.completedProjectsCount} completed deals)")
        } else {
            reasons.add("✓ Verified design craft & platform reviews")
        }

        // 6. Deadline feasibility
        if (designer.deliveryTimeDays <= project.deadlineDays) {
            score += 5
        }

        // 7. Languages
        if (designer.languages.contains("English", ignoreCase = true)) {
            score += 3
        }

        val clampedScore = score.coerceIn(72, 96)
        return MatchCalculation(
            percentage = clampedScore,
            reasons = reasons,
            isHighFit = clampedScore >= 80
        )
    }

    // Notifications
    val allNotifications: Flow<List<AppNotification>> = dao.getAllNotifications()

    suspend fun markNotificationRead(id: String) = dao.markNotificationAsRead(id)

    suspend fun markAllNotificationsRead() = dao.markAllNotificationsAsRead()

    suspend fun deleteNotification(id: String) = dao.deleteNotificationById(id)

    // Disputes System (Phase 5)
    val allDisputes: Flow<List<DisputeItem>> = dao.getAllDisputes()

    fun getDisputesForDeal(dealId: String): Flow<List<DisputeItem>> = dao.getDisputesForDeal(dealId)

    suspend fun openDispute(
        deal: Deal,
        raisedByUser: User,
        milestoneTitle: String,
        reason: String,
        description: String,
        evidenceNotes: String
    ): DisputeItem {
        val isClient = raisedByUser.id == deal.clientUserId
        val againstUserId = if (isClient) deal.designerUserId else deal.clientUserId
        val againstUserName = if (isClient) deal.designerName else deal.clientName

        val dispute = DisputeItem(
            id = "disp_${UUID.randomUUID().toString().take(8)}",
            dealId = deal.id,
            projectTitle = deal.projectTitle,
            raisedByUserId = raisedByUser.id,
            raisedByName = raisedByUser.name,
            raisedByRole = raisedByUser.role,
            againstUserId = againstUserId,
            againstUserName = againstUserName,
            milestoneTitle = milestoneTitle,
            reason = reason,
            description = description,
            evidenceNotes = evidenceNotes,
            status = "Open",
            adminDecisionNotes = "",
            timestamp = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        dao.insertDispute(dispute)

        // Notify both parties
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Dispute Opened: ${deal.projectTitle}",
                description = "${raisedByUser.name} submitted a dispute for '$milestoneTitle'. DealNest mediators have been alerted.",
                type = "DISPUTE"
            )
        )
        return dispute
    }

    suspend fun updateDisputeStatus(disputeId: String, newStatus: String, adminNotes: String) {
        dao.updateDisputeStatus(disputeId, newStatus, adminNotes, System.currentTimeMillis())
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Dispute Status Updated",
                description = "Dispute $disputeId has been updated to '$newStatus'. Decision: $adminNotes",
                type = "DISPUTE"
            )
        )
    }

    // Payment Architecture (Phase 5)
    val allPaymentTransactions: Flow<List<PaymentTransaction>> = dao.getAllPaymentTransactions()

    fun getPaymentsForDeal(dealId: String): Flow<List<PaymentTransaction>> = dao.getPaymentsForDeal(dealId)

    suspend fun recordPayment(
        deal: Deal,
        milestoneId: String?,
        amount: Double,
        payerUser: User,
        payeeUser: User,
        paymentMethod: String = "Escrow Demo",
        status: String = "Paid"
    ): PaymentTransaction {
        val txn = PaymentTransaction(
            id = "txn_${UUID.randomUUID().toString().take(8)}",
            dealId = deal.id,
            milestoneId = milestoneId,
            projectTitle = deal.projectTitle,
            payerUserId = payerUser.id,
            payerName = payerUser.name,
            payeeUserId = payeeUser.id,
            payeeName = payeeUser.name,
            amount = amount,
            currency = "USD",
            status = status,
            paymentMethod = paymentMethod,
            transactionRef = "TXN-DN-${System.currentTimeMillis() % 1000000}",
            timestamp = System.currentTimeMillis()
        )
        dao.insertPaymentTransaction(txn)

        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Payment $status: $$amount",
                description = "Escrow transaction of $$amount processed for '${deal.projectTitle}'.",
                type = "PAYMENT"
            )
        )
        return txn
    }

    // Favorites
    val allFavorites: Flow<List<FavoriteItem>> = dao.getAllFavorites()

    suspend fun toggleFavorite(itemType: String, targetId: String, isCurrentlySaved: Boolean) {
        if (isCurrentlySaved) {
            dao.deleteFavoriteByTargetId(targetId)
        } else {
            dao.insertFavorite(
                FavoriteItem(
                    id = "fav_${UUID.randomUUID()}",
                    itemType = itemType,
                    targetId = targetId
                )
            )
        }
    }

    // Reports
    val allReports: Flow<List<ReportItem>> = dao.getAllReports()
    val allBlockedUsers: Flow<List<BlockedUser>> = dao.getAllBlockedUsers()

    suspend fun submitReport(
        reportedByUserId: String,
        targetType: String,
        targetId: String,
        targetName: String,
        reason: String
    ) {
        dao.insertReport(
            ReportItem(
                id = "rep_${UUID.randomUUID()}",
                reportedByUserId = reportedByUserId,
                targetType = targetType,
                targetId = targetId,
                targetName = targetName,
                reason = reason,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun dismissReport(reportId: String) {
        dao.deleteReportById(reportId)
    }
}

data class MatchCalculation(
    val percentage: Int,
    val reasons: List<String>,
    val isHighFit: Boolean
)
