package com.example.dealnest.data

import com.example.dealnest.model.ChatMessage
import com.example.dealnest.model.Conversation
import com.example.dealnest.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

/**
 * ChatService defines the messaging contract for DealNest.
 * Architecture is designed to be easily pluggable with real-time backends
 * such as Firebase Realtime Database / Cloud Firestore or Supabase Realtime.
 */
interface ChatService {
    fun getConversations(userId: String): Flow<List<Conversation>>
    fun getConversationById(conversationId: String): Flow<Conversation?>
    fun getMessages(conversationId: String): Flow<List<ChatMessage>>

    suspend fun sendMessage(
        conversationId: String,
        senderUser: User,
        text: String,
        attachmentName: String? = null,
        attachmentType: String? = null,
        replyToMessage: ChatMessage? = null
    ): ChatMessage

    suspend fun markConversationAsRead(conversationId: String, currentUserId: String)
    suspend fun deleteMessage(messageId: String)

    suspend fun getOrCreateConversation(
        projectId: String,
        projectTitle: String,
        clientUser: User,
        designerUser: User,
        budget: String,
        deadline: String,
        dealId: String? = null,
        initialMessageText: String? = null
    ): Conversation

    suspend fun linkDealToConversation(
        conversationId: String,
        dealId: String,
        agreedPrice: Double
    )
}

/**
 * Local Room implementation of ChatService providing immediate offline-first responsiveness.
 */
class RoomChatService(private val dao: DealNestDao) : ChatService {

    override fun getConversations(userId: String): Flow<List<Conversation>> =
        dao.getConversationsForUser(userId)

    override fun getConversationById(conversationId: String): Flow<Conversation?> =
        dao.getConversationById(conversationId)

    override fun getMessages(conversationId: String): Flow<List<ChatMessage>> =
        dao.getMessagesForDeal(conversationId)

    override suspend fun sendMessage(
        conversationId: String,
        senderUser: User,
        text: String,
        attachmentName: String?,
        attachmentType: String?,
        replyToMessage: ChatMessage?
    ): ChatMessage {
        val message = ChatMessage(
            id = "msg_${UUID.randomUUID()}",
            dealOrChannelId = conversationId,
            senderUserId = senderUser.id,
            senderName = senderUser.name,
            senderRole = senderUser.role,
            text = text,
            attachmentName = attachmentName,
            attachmentType = attachmentType,
            replyToMessageId = replyToMessage?.id,
            replyToText = replyToMessage?.text?.take(80),
            replyToSenderName = replyToMessage?.senderName,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        dao.insertMessage(message)

        // Update conversation's last message & unread counter
        val conv = dao.getConversationById(conversationId).firstOrNull()
        if (conv != null) {
            val isClientSender = senderUser.id == conv.clientUserId
            val updatedConv = conv.copy(
                lastMessageText = if (attachmentName != null && text.isBlank()) "📎 $attachmentName" else text,
                lastMessageSenderId = senderUser.id,
                lastMessageTimestamp = System.currentTimeMillis(),
                unreadCountClient = if (isClientSender) conv.unreadCountClient else conv.unreadCountClient + 1,
                unreadCountDesigner = if (!isClientSender) conv.unreadCountDesigner else conv.unreadCountDesigner + 1
            )
            dao.updateConversation(updatedConv)
        }

        return message
    }

    override suspend fun markConversationAsRead(conversationId: String, currentUserId: String) {
        val conv = dao.getConversationById(conversationId).firstOrNull() ?: return
        if (currentUserId == conv.clientUserId) {
            dao.markConversationReadForClient(conversationId)
        } else {
            dao.markConversationReadForDesigner(conversationId)
        }
        dao.markMessagesAsReadForChannel(conversationId)
    }

    override suspend fun deleteMessage(messageId: String) {
        dao.deleteMessageById(messageId)
    }

    override suspend fun getOrCreateConversation(
        projectId: String,
        projectTitle: String,
        clientUser: User,
        designerUser: User,
        budget: String,
        deadline: String,
        dealId: String?,
        initialMessageText: String?
    ): Conversation {
        val existing = dao.findConversationByProjectAndDesigner(projectId, designerUser.id)
        if (existing != null) {
            if (dealId != null && existing.dealId == null) {
                dao.linkDealToConversation(existing.id, dealId, "DEAL_ACTIVE", existing.agreedPrice ?: 0.0)
            }
            return existing
        }

        val convId = "conv_${projectId}_${designerUser.id}"
        val newConv = Conversation(
            id = convId,
            projectId = projectId,
            projectTitle = projectTitle,
            clientUserId = clientUser.id,
            clientName = clientUser.name,
            clientAvatarUrl = clientUser.avatarUrl,
            designerUserId = designerUser.id,
            designerName = designerUser.name,
            designerAvatarUrl = designerUser.avatarUrl,
            designerHeadline = designerUser.headline,
            dealId = dealId,
            dealStatus = if (dealId != null) "DEAL_ACTIVE" else null,
            budget = budget,
            deadline = deadline,
            projectStatus = "OPEN",
            lastMessageText = initialMessageText ?: "Conversation started",
            lastMessageSenderId = clientUser.id,
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCountClient = 0,
            unreadCountDesigner = if (initialMessageText != null) 1 else 0
        )
        dao.insertConversation(newConv)

        if (initialMessageText != null) {
            dao.insertMessage(
                ChatMessage(
                    id = "msg_${UUID.randomUUID()}",
                    dealOrChannelId = convId,
                    senderUserId = clientUser.id,
                    senderName = clientUser.name,
                    senderRole = clientUser.role,
                    text = initialMessageText,
                    timestamp = System.currentTimeMillis(),
                    isRead = true
                )
            )
        }

        return newConv
    }

    override suspend fun linkDealToConversation(
        conversationId: String,
        dealId: String,
        agreedPrice: Double
    ) {
        dao.linkDealToConversation(conversationId, dealId, "DEAL_ACTIVE", agreedPrice)
    }
}
