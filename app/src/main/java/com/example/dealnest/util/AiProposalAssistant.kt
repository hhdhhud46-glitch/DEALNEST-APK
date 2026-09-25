package com.example.dealnest.util

import com.example.dealnest.model.Project
import com.example.dealnest.model.User
import java.util.Locale

data class AiGeneratedProposal(
    val professionalOpening: String,
    val understandingOfClient: String,
    val proposedSolution: String,
    val developmentApproach: String,
    val estimatedTimeline: String,
    val suggestedPriceRange: String,
    val suggestedPrice: Double,
    val suggestedDeliveryDays: Int,
    val questionsForClient: String,
    val professionalClosing: String,
    val fullPitchMessage: String
)

object AiProposalAssistant {

    fun generateProposal(
        project: Project,
        designer: User,
        tone: String = "Professional" // "Professional", "Technical", "Persuasive"
    ): AiGeneratedProposal {
        // Price calculation
        val medianBudget = (project.budgetMin + project.budgetMax) / 2.0
        val basePrice = when {
            designer.startingPrice > medianBudget -> project.budgetMax.coerceAtLeast(designer.startingPrice)
            designer.startingPrice < project.budgetMin -> project.budgetMin + (project.budgetMax - project.budgetMin) * 0.4
            else -> medianBudget
        }
        val suggestedPrice = ((basePrice / 50.0).toInt() * 50).toDouble()
        val priceRangeText = "$${project.budgetMin.toInt()} - $${project.budgetMax.toInt()} (Proposed: $${suggestedPrice.toInt()})"

        // Timeline calculation
        val suggestedDays = (project.deadlineDays - 2).coerceAtLeast(designer.deliveryTimeDays).coerceAtMost(project.deadlineDays)

        // 1. Professional Opening
        val opening = when (tone) {
            "Technical" -> "Hello ${project.clientName}, I am pleased to submit my technical proposal for '${project.title}'. As an engineer specializing in ${designer.skills.split(",").take(2).joinToString(" & ")}, I have reviewed your requirements with keen interest."
            "Persuasive" -> "Hi ${project.clientName}! Your vision for '${project.title}' is exciting and represents a massive commercial opportunity. With ${designer.experienceYears}+ years designing high-conversion digital experiences, I am confident we can create a category-leading digital footprint for you."
            else -> "Hello ${project.clientName}, thank you for posting your project brief for '${project.title}'. Having delivered over ${designer.completedProjectsCount} web projects, I would love to collaborate on bringing this vision to life."
        }

        // 2. Understanding of Client's Requirement
        val understanding = "Based on your brief, you require a high-performing ${project.websiteType} website targeting ${project.clientName}'s audience. The core focus involves ${if (project.requiredFeatures.isNotBlank()) project.requiredFeatures else "responsive UX, fast loading, and clean brand typography"}, built with an emphasis on ${project.requiredSkills}."

        // 3. Proposed Solution
        val solution = "I propose building an intuitive, accessible, and responsive ${project.websiteType} architecture. The solution will include a structured layout across approximately ${project.pageCount} pages, ensuring seamless interaction across mobile, tablet, and desktop devices."

        // 4. Development Approach
        val approach = when (tone) {
            "Technical" -> "• Phase 1 (Architecture & Tokens): Information architecture, type scale, Figma autolayout tokens.\n• Phase 2 (Interactive Prototype): Component states, micro-interactions, responsive breakpoints.\n• Phase 3 (Production Build): Clean, modular codebase using ${project.requiredSkills}, zero layout shift, semantic markup.\n• Phase 4 (Optimization & Deployment): Lighthouse 95+ audit, SEO meta tags, cross-browser QA, staging review."
            "Persuasive" -> "• Strategy & Wireframes: Map out customer journey and conversion funnels to maximize engagement.\n• High-Fidelity UI Design: Eye-catching aesthetics with premium typography that elevates your brand.\n• Rapid Build & Integrations: Turn designs into lightning-fast reality using modern web standards.\n• Polish & Launch: Thorough mobile testing, speed tuning, and seamless DNS go-live."
            else -> "• Discovery & Low-Fi UX: Solidify site sitemap, wireframes, and user journeys.\n• Figma UI Prototype: Craft pixel-perfect responsive layouts with client feedback checkpoints.\n• Development Build: Implement clean, semantic code with ${project.requiredSkills}.\n• QA & Handover: Rigorous cross-browser testing, SEO checklist, and complete admin documentation."
        }

        // 5. Estimated Timeline
        val timeline = "Estimated turnaround is $suggestedDays days total:\n" +
                "• Days 1–3: UX Wireframes & Structural Approval\n" +
                "• Days 4–${(suggestedDays * 0.5).toInt()}: High-Fidelity UI & Component Design\n" +
                "• Days ${((suggestedDays * 0.5) + 1).toInt()}–${suggestedDays - 2}: Core Development & Integrations\n" +
                "• Days ${suggestedDays - 1}–$suggestedDays: Final QA, Staging Review & Launch Handover"

        // 6. Questions for Client
        val questions = "A couple of quick questions to ensure perfect alignment:\n" +
                "1. Do you have finalized brand assets (logos, typography, photography), or should that be factored into our milestones?\n" +
                "2. Are there specific benchmark websites whose interaction style or animations you admire?\n" +
                "3. Will you need integrations with third-party tools (e.g. CRM, email marketing, or analytics)?"

        // 7. Professional Closing
        val closing = "I take immense pride in clear communication, prompt milestone delivery, and zero surprises. Let's connect in the Deal Room to discuss the initial kickoff!\n\nBest regards,\n${designer.name}\n${designer.headline}"

        // Full Pitch Message
        val fullPitch = buildString {
            append("$opening\n\n")
            append("1. UNDERSTANDING OF YOUR REQUIREMENTS:\n$understanding\n\n")
            append("2. PROPOSED SOLUTION:\n$solution\n\n")
            append("3. DEVELOPMENT APPROACH:\n$approach\n\n")
            append("4. ESTIMATED TIMELINE:\n$timeline\n\n")
            append("5. SUGGESTED BUDGET & VALUE:\n$priceRangeText (Includes full responsive build, QA & launch support)\n\n")
            append("6. QUESTIONS FOR YOU:\n$questions\n\n")
            append(closing)
        }

        return AiGeneratedProposal(
            professionalOpening = opening,
            understandingOfClient = understanding,
            proposedSolution = solution,
            developmentApproach = approach,
            estimatedTimeline = timeline,
            suggestedPriceRange = priceRangeText,
            suggestedPrice = suggestedPrice,
            suggestedDeliveryDays = suggestedDays,
            questionsForClient = questions,
            professionalClosing = closing,
            fullPitchMessage = fullPitch
        )
    }
}
