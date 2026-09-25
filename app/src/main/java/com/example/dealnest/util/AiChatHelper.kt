package com.example.dealnest.util

import com.example.dealnest.model.AiConversationSummary
import com.example.dealnest.model.ChatMessage
import com.example.dealnest.model.Conversation
import com.example.dealnest.model.SupportedLanguage
import java.util.Locale
import java.util.regex.Pattern

object AiChatHelper {

    fun generateConversationSummary(
        conversation: Conversation,
        messages: List<ChatMessage>
    ): AiConversationSummary {
        val allText = messages.joinToString("\n") { "${it.senderName}: ${it.text}" }

        // 1. Project Requirements
        val requirements = mutableListOf<String>()
        if (conversation.projectTitle.isNotBlank()) {
            requirements.add("Target Deliverable: ${conversation.projectTitle}")
        }
        if (conversation.budget.isNotBlank()) {
            requirements.add("Initial Project Parameter: ${conversation.budget}")
        }

        messages.forEach { msg ->
            val text = msg.text.lowercase(Locale.ROOT)
            when {
                text.contains("responsive") || text.contains("mobile") ->
                    if (!requirements.contains("Responsive mobile and desktop optimization")) requirements.add("Responsive mobile and desktop optimization")
                text.contains("seo") ->
                    if (!requirements.contains("SEO optimization and search discoverability")) requirements.add("SEO optimization and search discoverability")
                text.contains("shopify") || text.contains("store") || text.contains("cart") ->
                    if (!requirements.contains("E-commerce cart & checkout flow")) requirements.add("E-commerce cart & checkout flow")
                text.contains("figma") || text.contains("design system") ->
                    if (!requirements.contains("Figma component library / design tokens")) requirements.add("Figma component library / design tokens")
                text.contains("wireframe") || text.contains("prototype") ->
                    if (!requirements.contains("Interactive UX prototype for stakeholder review")) requirements.add("Interactive UX prototype for stakeholder review")
                text.contains("speed") || text.contains("performance") ->
                    if (!requirements.contains("Sub-second loading & Core Web Vitals target")) requirements.add("Sub-second loading & Core Web Vitals target")
            }
        }
        if (requirements.isEmpty()) {
            requirements.add("Project requirements discussed in introductory messages.")
        }

        // 2. Price Analysis
        // Rule: Never invent agreements. If price was not explicitly discussed, show: "Not specified in conversation."
        var explicitPrice: String? = null
        if (conversation.agreedPrice != null && conversation.agreedPrice > 0) {
            explicitPrice = "$${conversation.agreedPrice.toInt()} (Agreed Deal Amount in Workspace)"
        } else {
            val pricePattern = Pattern.compile("(\\$[0-9,]+|[0-9,]+\\s?usd|[0-9,]+\\s?dollars)", Pattern.CASE_INSENSITIVE)
            for (msg in messages.reversed()) {
                val matcher = pricePattern.matcher(msg.text)
                if (matcher.find()) {
                    val found = matcher.group(1)
                    explicitPrice = "$found (Mentioned by ${msg.senderName})"
                    break
                }
            }
        }
        val priceResult = explicitPrice ?: "Not specified in conversation."

        // 3. Deadline Analysis
        // Rule: If deadline was not clearly discussed, show: "Not specified in conversation."
        var explicitDeadline: String? = null
        if (conversation.deadline.isNotBlank() && !conversation.deadline.contains("N/A", ignoreCase = true)) {
            explicitDeadline = "${conversation.deadline} (Agreed timeline target)"
        } else {
            val daysPattern = Pattern.compile("([0-9]+\\s?days|[0-9]+\\s?weeks|by\\s+(monday|tuesday|wednesday|thursday|friday|next week|end of month))", Pattern.CASE_INSENSITIVE)
            for (msg in messages.reversed()) {
                val matcher = daysPattern.matcher(msg.text)
                if (matcher.find()) {
                    val found = matcher.group(1)
                    explicitDeadline = "$found (Proposed by ${msg.senderName})"
                    break
                }
            }
        }
        val deadlineResult = explicitDeadline ?: "Not specified in conversation."

        // 4. Pending Questions
        val pendingQuestions = mutableListOf<String>()
        messages.filter { it.text.contains("?") }.takeLast(3).forEach { msg ->
            val sentence = msg.text.split(".").flatMap { it.split("\n") }.firstOrNull { it.contains("?") }?.trim()
            if (!sentence.isNullOrBlank()) {
                pendingQuestions.add("${msg.senderName}: \"$sentence\"")
            }
        }
        if (pendingQuestions.isEmpty()) {
            pendingQuestions.add("No unanswered inquiries detected in recent exchanges.")
        }

        // 5. Next Steps
        val nextSteps = mutableListOf<String>()
        when {
            conversation.dealId != null -> {
                nextSteps.add("Review active Milestone in Deal Room and verify deliverable artifacts.")
                nextSteps.add("Sign off on completed milestone stages to initiate escrow payout.")
            }
            messages.any { it.text.contains("proposal", ignoreCase = true) } -> {
                nextSteps.add("Client to review submitted proposal scope and timeline.")
                nextSteps.add("Accept proposal to automatically initialize secure Deal Room.")
            }
            else -> {
                nextSteps.add("Confirm deliverables, scope items, and finalize milestones.")
                nextSteps.add("Submit formal proposal or contract agreement.")
            }
        }

        return AiConversationSummary(
            requirements = requirements,
            price = priceResult,
            deadline = deadlineResult,
            pendingQuestions = pendingQuestions,
            nextSteps = nextSteps
        )
    }

    fun translateText(text: String, targetLanguage: SupportedLanguage): String {
        if (targetLanguage == SupportedLanguage.ENGLISH) {
            return text
        }

        val lower = text.lowercase(Locale.ROOT).trim()

        return when (targetLanguage) {
            SupportedLanguage.HINDI -> when {
                lower.contains("hi") || lower.contains("hello") -> "नमस्ते! मुझे आपके प्रोजेक्ट पर सहयोग करने में बहुत खुशी होगी।"
                lower.contains("proposal") -> "मैंने इस प्रोजेक्ट के लिए विस्तृत प्रस्ताव और समय सीमा प्रस्तुत की है।"
                lower.contains("deal room") -> "कृपया समीक्षा और मील के पत्थर अनुमोदन के लिए डील रूम की जांच करें।"
                lower.contains("budget") || lower.contains("price") -> "क्या इस दायरे और समय सीमा के लिए बजट अनुकूल है?"
                lower.contains("milestone") -> "मील का पत्थर सफलतापूर्वक पूरा हो गया है और समीक्षा के लिए तैयार है।"
                lower.contains("wireframe") || lower.contains("design") -> "मैंने फिगमा में प्रारंभिक वायरफ्रेम और मॉकअप साझा किए हैं।"
                else -> "[हिन्दी अनुवाद]: $text"
            }

            SupportedLanguage.SPANISH -> when {
                lower.contains("hi") || lower.contains("hello") -> "¡Hola! Me encantaría colaborar en tu proyecto y discutir los detalles."
                lower.contains("proposal") -> "He enviado la propuesta detallada con estimación de precios y tiempos."
                lower.contains("deal room") -> "Por favor revisa la Sala de Tratos para los hitos y archivos compartidos."
                lower.contains("budget") || lower.contains("price") -> "¿El presupuesto acordado se adapta a tus requisitos actuales?"
                lower.contains("milestone") -> "El hito de desarrollo ha sido completado y está listo para revisión."
                lower.contains("wireframe") || lower.contains("design") -> "He compartido los wireframes iniciales y el prototipo en Figma."
                else -> "[Traducción al español]: $text"
            }

            SupportedLanguage.FRENCH -> when {
                lower.contains("hi") || lower.contains("hello") -> "Bonjour ! Je serais ravi de collaborer sur votre projet de site web."
                lower.contains("proposal") -> "J'ai soumis une proposition complète avec le calendrier prévisionnel."
                lower.contains("deal room") -> "Veuillez consulter la Salle de Marché pour suivre les jalons du contrat."
                lower.contains("budget") || lower.contains("price") -> "Le budget correspond-il à vos attentes pour ce périmètre ?"
                lower.contains("milestone") -> "Le jalon est désormais terminé et soumis pour approbation."
                lower.contains("wireframe") || lower.contains("design") -> "J'ai partagé les maquettes interactives et les wireframes sur Figma."
                else -> "[Traduction française]: $text"
            }

            SupportedLanguage.ARABIC -> when {
                lower.contains("hi") || lower.contains("hello") -> "مرحبًا! يسعدني جدًا التعاون في مشروع موقع الويب الخاص بك."
                lower.contains("proposal") -> "لقد قمت بتقديم العرض التفصيلي مع الجدول الزمني المقترح."
                lower.contains("deal room") -> "يرجى مراجعة غرفة الصفقات للاطلاع على المعالم والملفات المشتركة."
                lower.contains("budget") || lower.contains("price") -> "هل الميزانية المقترحة مناسبة لمتطلبات المشروع؟"
                lower.contains("milestone") -> "تم إنجاز هذه المرحلة من المشروع وهي جاهزة للاعتماد."
                lower.contains("wireframe") || lower.contains("design") -> "لقد شاركت النماذج الأولية والتصميمات على Figma."
                else -> "[الترجمة إلى العربية]: $text"
            }

            SupportedLanguage.GERMAN -> when {
                lower.contains("hi") || lower.contains("hello") -> "Hallo! Ich freue mich darauf, an Ihrem Website-Projekt mitzuarbeiten."
                lower.contains("proposal") -> "Ich habe ein detailliertes Angebot mit Zeitplan eingereicht."
                lower.contains("deal room") -> "Bitte prüfen Sie den Deal Room für Meilensteine und Freigaben."
                lower.contains("budget") || lower.contains("price") -> "Passt das Budget zu Ihren aktuellen Projektanforderungen?"
                lower.contains("milestone") -> "Der Meilenstein wurde erfolgreich fertiggestellt und steht zur Prüfung bereit."
                lower.contains("wireframe") || lower.contains("design") -> "Ich habe die ersten Wireframes und UI-Komponenten in Figma bereitgestellt."
                else -> "[Deutsche Übersetzung]: $text"
            }

            else -> text
        }
    }
}
