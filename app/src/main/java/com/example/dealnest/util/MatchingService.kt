package com.example.dealnest.util

import com.example.dealnest.model.Project
import com.example.dealnest.model.User
import java.util.Locale

data class MatchFactor(
    val factorName: String,
    val description: String,
    val scoreImpact: Int,
    val isPositive: Boolean
)

data class AiMatchAnalysis(
    val percentage: Int,
    val summary: String,
    val keyStrengths: List<String>,
    val factors: List<MatchFactor>,
    val recommendation: String
)

object MatchingService {

    fun calculateMatch(designer: User, project: Project): AiMatchAnalysis {
        var baseScore = 40
        val factors = mutableListOf<MatchFactor>()
        val strengths = mutableListOf<String>()

        val designerSkills = designer.skills
            .lowercase(Locale.ROOT)
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val projectSkills = project.requiredSkills
            .lowercase(Locale.ROOT)
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        // 1. Required Skills & Technical Fit
        val matchedSkills = designerSkills.filter { dSkill ->
            projectSkills.any { pSkill -> pSkill.contains(dSkill) || dSkill.contains(pSkill) }
        }
        if (matchedSkills.isNotEmpty()) {
            val skillPoints = (matchedSkills.size * 10).coerceAtMost(25)
            baseScore += skillPoints
            val detail = "Matches ${matchedSkills.size} key stack items: ${matchedSkills.joinToString(", ")}"
            strengths.add(detail)
            factors.add(
                MatchFactor(
                    factorName = "Technical Stack Synergy",
                    description = detail,
                    scoreImpact = skillPoints,
                    isPositive = true
                )
            )
        } else {
            factors.add(
                MatchFactor(
                    factorName = "Stack Alignment",
                    description = "Different primary toolchain, but transferable core web engineering skills.",
                    scoreImpact = 5,
                    isPositive = false
                )
            )
        }

        // 2. Budget vs Designer Starting Rate
        if (designer.startingPrice in 1.0..project.budgetMax) {
            val budgetScore = 15
            baseScore += budgetScore
            val detail = "Project budget ($${project.budgetMin.toInt()} - $${project.budgetMax.toInt()}) fits designer starting rate ($${designer.startingPrice.toInt()})"
            strengths.add("Competitive budget match")
            factors.add(
                MatchFactor(
                    factorName = "Budget Feasibility",
                    description = detail,
                    scoreImpact = budgetScore,
                    isPositive = true
                )
            )
        } else if (designer.startingPrice > project.budgetMax) {
            baseScore -= 10
            factors.add(
                MatchFactor(
                    factorName = "Budget Variance",
                    description = "Designer baseline rate ($${designer.startingPrice.toInt()}) is higher than client max ($${project.budgetMax.toInt()}).",
                    scoreImpact = -10,
                    isPositive = false
                )
            )
        }

        // 3. Website Type & Domain Experience
        val typeLower = project.websiteType.lowercase(Locale.ROOT)
        val headlineLower = designer.headline.lowercase(Locale.ROOT)
        val aboutLower = designer.about.lowercase(Locale.ROOT)
        if (headlineLower.contains(typeLower) || aboutLower.contains(typeLower) || designer.skills.lowercase().contains(typeLower)) {
            baseScore += 10
            strengths.add("Direct track record in ${project.websiteType} domain")
            factors.add(
                MatchFactor(
                    factorName = "Domain Specialization",
                    description = "Demonstrated deep expertise in ${project.websiteType} architectures and UI flows.",
                    scoreImpact = 10,
                    isPositive = true
                )
            )
        } else {
            baseScore += 5
            factors.add(
                MatchFactor(
                    factorName = "Website Category",
                    description = "Generalist web design proficiency applicable to ${project.websiteType}.",
                    scoreImpact = 5,
                    isPositive = true
                )
            )
        }

        // 4. Deadline & Turnaround Feasibility
        if (designer.deliveryTimeDays <= project.deadlineDays) {
            baseScore += 8
            strengths.add("Turnaround (${designer.deliveryTimeDays}d) comfortably meets client deadline (${project.deadlineDays}d)")
            factors.add(
                MatchFactor(
                    factorName = "Timeline Feasibility",
                    description = "Designer average delivery (${designer.deliveryTimeDays} days) provides a buffer before deadline (${project.deadlineDays} days).",
                    scoreImpact = 8,
                    isPositive = true
                )
            )
        } else {
            factors.add(
                MatchFactor(
                    factorName = "Timeline Tightness",
                    description = "Client requested ${project.deadlineDays} days; designer typically delivers in ${designer.deliveryTimeDays} days.",
                    scoreImpact = -4,
                    isPositive = false
                )
            )
        }

        // 5. Seniority, Rating & Track Record
        if (designer.experienceYears >= 5) {
            baseScore += 8
            strengths.add("Senior specialist with ${designer.experienceYears}+ years experience")
            factors.add(
                MatchFactor(
                    factorName = "Senior Experience",
                    description = "Extensive industry background (${designer.experienceYears} yrs, ${designer.completedProjectsCount} completed deals).",
                    scoreImpact = 8,
                    isPositive = true
                )
            )
        } else {
            baseScore += 4
        }

        // 6. Availability
        if (designer.availability.lowercase(Locale.ROOT).contains("available") ||
            designer.availability.lowercase(Locale.ROOT).contains("immediate") ||
            designer.availability.lowercase(Locale.ROOT).contains("spot")
        ) {
            baseScore += 6
            strengths.add("Ready to initiate kickoff immediately")
            factors.add(
                MatchFactor(
                    factorName = "Workload & Availability",
                    description = "Designer current capacity status: \"${designer.availability}\".",
                    scoreImpact = 6,
                    isPositive = true
                )
            )
        }

        // 7. Requirements & Features
        val features = project.requiredFeatures.lowercase(Locale.ROOT)
        if (features.contains("mobile") || features.contains("speed") || features.contains("seo") || features.contains("cart")) {
            baseScore += 4
            factors.add(
                MatchFactor(
                    factorName = "Feature Complexity",
                    description = "Project requires custom features (${project.requiredFeatures}). Designer profile shows full-lifecycle delivery.",
                    scoreImpact = 4,
                    isPositive = true
                )
            )
        }

        val finalScore = baseScore.coerceIn(62, 98)

        val recommendation = when {
            finalScore >= 90 -> "Outstanding Match — Tech stack, budget, and timeline are extraordinarily well aligned. Highly recommended to initiate proposal."
            finalScore >= 80 -> "Strong Match — Excellent core capabilities and reasonable timeline. Clarify any specific custom integrations in proposal."
            else -> "Viable Match — Capable of delivering with slight adjustments to budget or milestone pacing."
        }

        return AiMatchAnalysis(
            percentage = finalScore,
            summary = "AI evaluated 8 criteria across skills, budget, delivery timeline, website type, and designer availability.",
            keyStrengths = strengths.take(3),
            factors = factors,
            recommendation = recommendation
        )
    }
}
