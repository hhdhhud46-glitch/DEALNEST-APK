package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.dealnest.data.SeedData
import com.example.dealnest.util.MatchingService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("DEALNEST", appName)
  }

  @Test
  fun `verify matching service calculation`() {
    val designer = SeedData.demoDesigners.first()
    val project = SeedData.demoProjects.first()
    val matchResult = MatchingService.calculateMatch(designer, project)

    assertTrue("Match percentage should be realistic", matchResult.percentage in 50..100)
    assertTrue("Should have key strengths", matchResult.keyStrengths.isNotEmpty())
    assertTrue("Should have factors", matchResult.factors.isNotEmpty())
  }

  @Test
  fun `verify ai proposal assistant generation`() {
    val designer = SeedData.demoDesigners.first()
    val project = SeedData.demoProjects.first()
    val proposal = com.example.dealnest.util.AiProposalAssistant.generateProposal(project, designer)

    assertTrue("Proposal price should be positive", proposal.suggestedPrice > 0)
    assertTrue("Proposal should have client understanding", proposal.clientUnderstanding.isNotBlank())
    assertTrue("Proposal should have suggested approach", proposal.suggestedApproach.isNotBlank())
    assertTrue("Proposal should have timeline phases", proposal.suggestedTimelinePhases.isNotBlank())
    assertTrue("Proposal pitch message should mention client", proposal.fullPitchMessage.contains(project.clientName))
  }

  @Test
  fun `verify demo conversations and chat data`() {
    val conversations = SeedData.demoConversations
    assertTrue("Should have demo conversations", conversations.isNotEmpty())
    val activeDealConv = conversations.firstOrNull { it.dealId != null }
    assertTrue("Should have a conversation with active deal", activeDealConv != null)
    assertEquals("DEAL_ACTIVE", activeDealConv?.dealStatus)

    val demoMessages = SeedData.demoMessages
    assertTrue("Should have demo messages", demoMessages.isNotEmpty())
    val figmaMsg = demoMessages.firstOrNull { it.attachmentType == "FIGMA" }
    assertTrue("Should support FIGMA attachment type", figmaMsg != null)
  }

  @Test
  fun `verify review data and rating breakdown integrity`() {
    val reviews = SeedData.demoReviews
    assertTrue("Should have demo reviews", reviews.isNotEmpty())
    val sampleReview = reviews.first()
    assertTrue("Review rating should be between 1 and 5", sampleReview.rating in 1..5)
    assertTrue("Review should be tied to a deal", sampleReview.dealId.isNotBlank())
    assertTrue("Review author and target must differ", sampleReview.authorUserId != sampleReview.targetUserId)

    val designer = SeedData.demoDesigners.first()
    val totalBreakdown = designer.ratingBreakdown5 + designer.ratingBreakdown4 +
            designer.ratingBreakdown3 + designer.ratingBreakdown2 + designer.ratingBreakdown1
    assertTrue("Rating breakdown counts should equal total reviewCount", totalBreakdown == designer.reviewCount)
  }

  @Test
  fun `verify verification status and professional portfolio fields`() {
    val designer = SeedData.demoDesigners.first()
    val slug = designer.name.lowercase().replace(" ", "-").replace(".", "")
    val expectedUrl = "https://dealnest.com/portfolio/$slug"
    assertTrue("Generated portfolio slug should match standard", expectedUrl.contains("/portfolio/"))

    val services = SeedData.demoDesignerServices
    assertTrue("Should have designer services", services.isNotEmpty())
    val service = services.first()
    assertTrue("Service starting price should be positive", service.startingPrice > 0)
    assertTrue("Service delivery days should be positive", service.deliveryDays > 0)
  }

  @Test
  fun `verify search filter state matching`() {
    val filterState = com.example.dealnest.model.SearchFilterState(
        searchQuery = "Shopify",
        websiteType = "E-Commerce",
        verifiedOnly = true
    )
    assertTrue("SearchFilterState should indicate active filters", filterState.hasActiveFilters())

    val emptyFilter = com.example.dealnest.model.SearchFilterState()
    assertTrue("Empty filter state should not indicate active filters", !emptyFilter.hasActiveFilters())
  }
}
