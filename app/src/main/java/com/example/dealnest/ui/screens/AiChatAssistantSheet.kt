package com.example.dealnest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dealnest.model.ChatMessage
import com.example.dealnest.model.Conversation
import com.example.dealnest.model.Project
import com.example.ui.theme.*

enum class AiAssistantTool(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    QUESTIONS("Suggest Questions", Icons.Outlined.HelpOutline),
    BRIEF_BUILDER("Project Brief", Icons.Outlined.Description),
    EXPLAIN_TECH("Explain Specs", Icons.Outlined.Lightbulb),
    SUMMARIZE("Summarize Chat", Icons.Outlined.Summarize),
    TRANSLATE("Translate", Icons.Outlined.Translate),
    GLOSSARY("Tech Glossary", Icons.Outlined.MenuBook)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatAssistantSheet(
    conversation: Conversation,
    project: Project?,
    messages: List<ChatMessage>,
    onInsertTextToChat: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTool by remember { mutableStateOf(AiAssistantTool.QUESTIONS) }
    var selectedLanguage by remember { mutableStateOf("Spanish") }
    var customTranslateText by remember { mutableStateOf("") }
    var selectedGlossaryTerm by remember { mutableStateOf("Headless Commerce") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("ai_chat_assistant_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header with AI Sparkle Gradient
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(DealCyanPrimary, DealIndigo))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Sparkle",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "DealNest AI Assistant",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Smart conversation & requirement tools",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CRITICAL SAFETY DISCLAIMER
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DealAmberLight.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, DealAmber.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Safety",
                        tint = DealAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Safety Guard: The AI assistant helps clarify requirements and suggest questions, but will NEVER negotiate or agree to prices, contracts, payments, or deadlines on your behalf.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tool Switcher Chips
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AiAssistantTool.values().size) { idx ->
                    val tool = AiAssistantTool.values()[idx]
                    val isSelected = selectedTool == tool
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTool = tool },
                        leadingIcon = {
                            Icon(tool.icon, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        label = { Text(tool.title, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DealCyanPrimary,
                            selectedLabelColor = Color.Black,
                            selectedLeadingIconColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tool Content Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTool) {
                    AiAssistantTool.QUESTIONS -> {
                        QuestionsSection(
                            projectTitle = conversation.projectTitle,
                            onInsert = {
                                onInsertTextToChat(it)
                                onDismiss()
                            }
                        )
                    }
                    AiAssistantTool.BRIEF_BUILDER -> {
                        BriefBuilderSection(
                            conversation = conversation,
                            project = project,
                            onInsert = {
                                onInsertTextToChat(it)
                                onDismiss()
                            }
                        )
                    }
                    AiAssistantTool.EXPLAIN_TECH -> {
                        ExplainTechSection(
                            project = project,
                            conversation = conversation,
                            onInsert = {
                                onInsertTextToChat(it)
                                onDismiss()
                            }
                        )
                    }
                    AiAssistantTool.SUMMARIZE -> {
                        SummarizeChatSection(
                            messages = messages,
                            conversation = conversation,
                            onInsert = {
                                onInsertTextToChat(it)
                                onDismiss()
                            }
                        )
                    }
                    AiAssistantTool.TRANSLATE -> {
                        TranslateSection(
                            messages = messages,
                            selectedLanguage = selectedLanguage,
                            onLanguageSelected = { selectedLanguage = it },
                            customText = customTranslateText,
                            onCustomTextChanged = { customTranslateText = it },
                            onInsert = {
                                onInsertTextToChat(it)
                                onDismiss()
                            }
                        )
                    }
                    AiAssistantTool.GLOSSARY -> {
                        GlossarySection(
                            selectedTerm = selectedGlossaryTerm,
                            onSelectTerm = { selectedGlossaryTerm = it },
                            onInsert = {
                                onInsertTextToChat(it)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionsSection(
    projectTitle: String,
    onInsert: (String) -> Unit
) {
    val suggestedQuestions = listOf(
        "Could you clarify the primary call-to-actions (e.g. Lead Form, Stripe Checkout, or Booking) for this project?",
        "Do you already have high-res brand assets, typography guidelines, and logos ready, or should we include brand styling?",
        "Are there specific third-party integrations (e.g. Klaviyo, Google Analytics 4, Calendly) you want baked into the launch?",
        "What is your target launch deadline, and do you have a staging domain already purchased?",
        "Will you need CMS training or Loom video documentation for updating content after delivery?"
    )

    Text(
        text = "Tailored Questions for '$projectTitle'",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = "Tap any question to quickly populate your message input.",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    suggestedQuestions.forEach { question ->
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onInsert(question) }
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = question,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Use",
                    tint = DealCyanPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun BriefBuilderSection(
    conversation: Conversation,
    project: Project?,
    onInsert: (String) -> Unit
) {
    val briefTemplate = """
📋 PROJECT SCOPE & BRIEF
• Project: ${conversation.projectTitle}
• Budget Target: ${conversation.budget}
• Target Delivery: ${conversation.deadline}

1. Objective:
Deliver a modern, fast, responsive website designed to maximize conversion rates and user engagement.

2. Key Deliverables:
- Full responsive UI system (Desktop, Tablet, Mobile)
- Figma component library with auto-layout & design tokens
- Complete frontend code implementation with SEO optimization
- Clean cross-browser QA testing & handoff

3. Milestone Strategy:
- Milestone 1: UX Wireframes & Architecture
- Milestone 2: High-Fidelity UI Design
- Milestone 3: Interactive Build & CMS Setup
- Milestone 4: QA Audit & Live Deployment
""".trimIndent()

    Text(
        text = "Structured Project Brief",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = briefTemplate,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onInsert(briefTemplate) },
                colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Insert Full Brief into Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ExplainTechSection(
    project: Project?,
    conversation: Conversation,
    onInsert: (String) -> Unit
) {
    val skills = project?.requiredSkills ?: "Webflow, Shopify, Next.js, Tailwind CSS"
    Text(
        text = "Requirement Explanations: $skills",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )

    val explanations = listOf(
        "Shopify / E-Commerce" to "High-converting online store with native payment gateways, inventory tracking, custom cart drawers, and abandoned checkout recovery.",
        "Next.js & React" to "Ultra-fast server-rendered web application that achieves 95+ Google PageSpeed scores, dynamic API integrations, and instant page transitions.",
        "Webflow / Framer" to "Visual development platforms that allow pixel-perfect animations and rapid content editing without complex hosting servers.",
        "Tailwind CSS & Design Tokens" to "Standardized styling framework ensuring exact color, typography, and spacing consistency across all screen resolutions."
    )

    explanations.forEach { (tech, desc) ->
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(tech, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                TextButton(
                    onClick = { onInsert("Regarding $tech: $desc") },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Share explanation in chat →", fontSize = 11.sp, color = DealCyanPrimary)
                }
            }
        }
    }
}

@Composable
private fun SummarizeChatSection(
    messages: List<ChatMessage>,
    conversation: Conversation,
    onInsert: (String) -> Unit
) {
    val summaryText = if (messages.isEmpty()) {
        "No messages yet to summarize. Start the conversation to generate automated summaries!"
    } else {
        """
📝 CONVERSATION SUMMARY & NEXT STEPS
• Thread: ${conversation.projectTitle}
• Participants: ${conversation.clientName} & ${conversation.designerName}
• Status: ${if (conversation.dealId != null) "Deal Active (Milestones Underway)" else "Initial Project Discussion"}

Key Takeaways:
1. Requirements & mockups discussed in thread (${messages.size} total messages exchange).
2. Deliverable focus: Clean responsive styling, optimized user flows, and clear milestones.
3. Next Action: Review current milestone or finalize proposal terms.
""".trimIndent()
    }

    Text(
        text = "Conversation Summary",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = summaryText,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (messages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onInsert(summaryText) },
                    colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Paste Summary to Collaborator", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TranslateSection(
    messages: List<ChatMessage>,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    customText: String,
    onCustomTextChanged: (String) -> Unit,
    onInsert: (String) -> Unit
) {
    val languages = listOf("Spanish", "French", "German", "Japanese", "Chinese", "Hindi", "Portuguese")

    Text(
        text = "Translate Message",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )

    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(languages.size) { i ->
            val lang = languages[i]
            FilterChip(
                selected = selectedLanguage == lang,
                onClick = { onLanguageSelected(lang) },
                label = { Text(lang, fontSize = 11.sp) }
            )
        }
    }

    val latestMsg = messages.lastOrNull()?.text ?: "Hello! I am ready to collaborate on the project."
    val translatedSample = when (selectedLanguage) {
        "Spanish" -> "¡Hola! He revisado los requisitos del proyecto y estoy listo para avanzar con los hitos."
        "French" -> "Bonjour! J'ai examiné les exigences du projet et je suis prêt à commencer."
        "German" -> "Hallo! Ich habe die Projektanforderungen geprüft und bin bereit für den Start."
        "Japanese" -> "こんにちは！プロジェクトの要件を確認しました。マイルストーンを進める準備が整いました。"
        "Chinese" -> "您好！我已经查看了项目要求，准备好推进里程碑交付。"
        "Hindi" -> "नमस्ते! मैंने प्रोजेक्ट की आवश्यकताएं देख ली हैं और काम शुरू करने के लिए तैयार हूँ।"
        else -> "Olá! Analisei os requisitos do projeto e estou pronto para começar."
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Source ($latestMsg):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Translated ($selectedLanguage):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary)
            Text(translatedSample, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onInsert(translatedSample) },
                colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Insert Translated Text", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GlossarySection(
    selectedTerm: String,
    onSelectTerm: (String) -> Unit,
    onInsert: (String) -> Unit
) {
    val glossary = mapOf(
        "Headless Commerce" to "Separating the frontend presentation layer (e.g. Next.js storefront) from backend e-commerce operations (Shopify checkout/inventory) for maximum speed and custom layout flexibility.",
        "Figma Tokens" to "Centralized style values (colors, spacing, typography scales) that allow instantaneous design updates across hundreds of components simultaneously.",
        "DNS Propagation" to "The estimated 2 to 24-hour period it takes for domain name servers worldwide to update with your new website's IP address.",
        "Core Web Vitals" to "Google's real-world metrics measuring loading performance (LCP), interactivity (INP), and visual stability (CLS) for SEO ranking.",
        "Responsive Breakpoints" to "Specific screen pixel widths (typically 375px mobile, 768px tablet, 1280px desktop) where layout elements rearrange automatically."
    )

    Text(
        text = "Technical Terms & Concepts",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )

    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(glossary.keys.toList().size) { idx ->
            val term = glossary.keys.toList()[idx]
            FilterChip(
                selected = selectedTerm == term,
                onClick = { onSelectTerm(term) },
                label = { Text(term, fontSize = 11.sp) }
            )
        }
    }

    val explanation = glossary[selectedTerm] ?: ""
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(selectedTerm, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(explanation, fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onInsert("💡 $selectedTerm: $explanation") },
                colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Explain this term in chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
