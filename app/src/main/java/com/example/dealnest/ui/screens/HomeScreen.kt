package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dealnest.data.SeedData
import com.example.dealnest.model.Project
import com.example.dealnest.model.SearchType
import com.example.dealnest.model.User
import com.example.dealnest.model.UserRole
import com.example.dealnest.ui.AppTab
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.EmptyPlaceholder
import com.example.dealnest.ui.components.MatchScoreBadge
import com.example.dealnest.ui.components.StarRatingBar
import com.example.dealnest.ui.components.StatusChip
import com.example.dealnest.ui.components.VerifiedBadge
import com.example.dealnest.util.MatchingService
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: DealNestViewModel,
    projects: List<Project>,
    designers: List<User>,
    currentUser: User,
    currentRole: UserRole,
    modifier: Modifier = Modifier
) {
    val searchFilterState by viewModel.searchFilterState.collectAsState()
    val filteredProjects by viewModel.filteredProjects.collectAsState()
    val filteredDesigners by viewModel.filteredDesigners.collectAsState()
    val isSearching = searchFilterState.hasActiveFilters()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. HERO BANNER
        item {
            HeroBannerSection(
                onNeedWebsiteClick = {
                    viewModel.switchRole(UserRole.CLIENT)
                    viewModel.showPostProjectDialog.value = true
                },
                onImDesignerClick = {
                    viewModel.switchRole(UserRole.DESIGNER)
                    viewModel.switchTab(AppTab.PROJECTS)
                }
            )
        }

        // 2. QUICK SEARCH BAR WITH ADVANCED FILTER BUTTON & SUGGESTIONS
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchFilterState.searchQuery,
                        onValueChange = {
                            viewModel.updateSearchQuery(it)
                        },
                        placeholder = {
                            Text(
                                "Search projects, designers, skills (e.g. Shopify)...",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = DealCyanPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchFilterState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DealCyanPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_search_input")
                    )

                    IconButton(
                        onClick = { viewModel.showAdvancedSearchSheet.value = true },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DealCyanLight.copy(alpha = 0.25f))
                            .testTag("home_advanced_filter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filters",
                            tint = DealCyanPrimary
                        )
                    }
                }

                // Quick Search Suggestions
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(viewModel.searchSuggestions.take(6)) { tag ->
                        val isSelected = searchFilterState.searchQuery.equals(tag, ignoreCase = true)
                        SuggestionChip(
                            onClick = {
                                viewModel.updateSearchQuery(tag)
                                viewModel.addRecentSearch(tag)
                            },
                            label = { Text(tag, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isSelected) DealCyanPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                labelColor = if (isSelected) DealCyanPrimary else MaterialTheme.colorScheme.onSurface
                            ),
                            border = if (isSelected) BorderStroke(1.dp, DealCyanPrimary) else null
                        )
                    }
                }

                // Active Filter Chips Strip
                if (isSearching) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DealRoseLight.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, DealRose.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable { viewModel.clearSearchFilters() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = DealRose, modifier = Modifier.size(12.dp))
                                Text("Clear Filters", fontSize = 10.sp, color = DealRose, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (searchFilterState.searchQuery.isNotBlank()) {
                            StatusChip(statusText = "\"${searchFilterState.searchQuery}\"")
                        }
                        if (searchFilterState.websiteType != null && searchFilterState.websiteType != "All") {
                            StatusChip(statusText = searchFilterState.websiteType ?: "")
                        }
                        if (searchFilterState.verifiedOnly) {
                            StatusChip(statusText = "Verified Only")
                        }
                    }
                }
            }
        }

        if (isSearching) {
            // SEARCH RESULTS SECTION
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search Results",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${filteredProjects.size} Projects • ${filteredDesigners.size} Designers",
                        fontSize = 12.sp,
                        color = DealCyanPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (filteredProjects.isEmpty() && filteredDesigners.isEmpty()) {
                item {
                    EmptyPlaceholder(
                        icon = Icons.Outlined.SearchOff,
                        title = "No results found",
                        subtitle = "No projects or designers matched your search criteria.",
                        actionButtonText = "Reset Filters",
                        onActionClick = { viewModel.clearSearchFilters() }
                    )
                }
            } else {
                // Show matching designers if appropriate
                if (searchFilterState.searchType == SearchType.ALL || searchFilterState.searchType == SearchType.DESIGNERS) {
                    if (filteredDesigners.isNotEmpty()) {
                        item {
                            Text(
                                text = "Designers (${filteredDesigners.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        items(filteredDesigners) { designer ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                TopDesignerCard(
                                    designer = designer,
                                    isSaved = viewModel.isFavorite("DESIGNER", designer.id),
                                    onSaveClick = { viewModel.toggleFavorite("DESIGNER", designer.id) },
                                    onClick = { viewModel.selectedDesignerForDetail.value = designer },
                                    onOpenPortfolio = { viewModel.openPublicPortfolio(designer) },
                                    onToggleCompare = { viewModel.toggleComparison(designer) },
                                    onVerifiedBadgeClick = { viewModel.showVerificationInfoDialog.value = true }
                                )
                            }
                        }
                    }
                }

                // Show matching projects if appropriate
                if (searchFilterState.searchType == SearchType.ALL || searchFilterState.searchType == SearchType.PROJECTS) {
                    if (filteredProjects.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Projects (${filteredProjects.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        items(filteredProjects) { project ->
                            val topDesigner = designers.firstOrNull() ?: SeedData.demoDesigners.first()
                            val matchResult = MatchingService.calculateMatch(topDesigner, project)
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                FeaturedProjectCard(
                                    project = project,
                                    matchScore = matchResult.percentage,
                                    isSaved = viewModel.isFavorite("PROJECT", project.id),
                                    onSaveClick = { viewModel.toggleFavorite("PROJECT", project.id) },
                                    onClick = { viewModel.selectedProjectForDetail.value = project },
                                    onProposeClick = {
                                        viewModel.selectedProjectForDetail.value = project
                                        viewModel.showSendProposalDialog.value = true
                                    },
                                    onExplainMatch = {
                                        viewModel.openAiMatchExplanation(project, topDesigner)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Standard Home Layout

        // 3. STATS STRIP
        item {
            MarketplaceStatsStrip()
        }

        // 4. SMART MATCHING SPOTLIGHT
        item {
            SmartMatchingSpotlight(
                currentRole = currentRole,
                projects = projects,
                designers = designers,
                onExploreMatches = {
                    viewModel.switchTab(AppTab.PROJECTS)
                }
            )
        }

        // 5. FEATURED PROJECTS SECTION
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Featured Projects",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Verified clients seeking talent right now",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = { viewModel.switchTab(AppTab.PROJECTS) },
                        modifier = Modifier.testTag("view_all_projects_button")
                    ) {
                        Text(
                            text = "View All",
                            color = DealCyanPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(projects.take(4)) { project ->
                        val topDesigner = designers.firstOrNull() ?: SeedData.demoDesigners.first()
                        val matchResult = MatchingService.calculateMatch(topDesigner, project)

                        FeaturedProjectCard(
                            project = project,
                            matchScore = matchResult.percentage,
                            isSaved = viewModel.isFavorite("PROJECT", project.id),
                            onSaveClick = { viewModel.toggleFavorite("PROJECT", project.id) },
                            onClick = {
                                viewModel.selectedProjectForDetail.value = project
                            },
                            onProposeClick = {
                                viewModel.selectedProjectForDetail.value = project
                                viewModel.showSendProposalDialog.value = true
                            },
                            onExplainMatch = {
                                viewModel.openAiMatchExplanation(project, topDesigner)
                            }
                        )
                    }
                }
            }
        }

        // 6. TOP RATED DESIGNERS SECTION
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Top Verified Designers",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Vetted experts with proven deal completions",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(onClick = { viewModel.showClientComparisonDialog.value = true }) {
                        Icon(Icons.Default.CompareArrows, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Compare (3)", fontSize = 12.sp, color = DealCyanPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(designers) { designer ->
                        TopDesignerCard(
                            designer = designer,
                            isSaved = viewModel.isFavorite("DESIGNER", designer.id),
                            onSaveClick = { viewModel.toggleFavorite("DESIGNER", designer.id) },
                            onClick = {
                                viewModel.selectedDesignerForDetail.value = designer
                            },
                            onOpenPortfolio = {
                                viewModel.openDesignerPortfolio(designer)
                            },
                            onToggleCompare = {
                                viewModel.toggleComparison(designer)
                            },
                            onVerifiedBadgeClick = {
                                viewModel.showVerificationInfoDialog.value = true
                            }
                        )
                    }
                }
            }
        }

        // 7. DEALNEST GUARANTEE / TRUST BANNER
        item {
            TrustGuaranteeBanner(
                onGuidelinesClick = { viewModel.showSafetyDialog.value = true }
            )
        }
        }
    }
}

@Composable
fun HeroBannerSection(
    onNeedWebsiteClick: () -> Unit,
    onImDesignerClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(DealCyanPrimary.copy(alpha = 0.5f), DealIndigo.copy(alpha = 0.5f))),
                RoundedCornerShape(20.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DealIndigo.copy(alpha = 0.18f),
                            DealCyanPrimary.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(DealEmerald)
                    )
                    Text(
                        text = "THE DIGITAL SERVICES MARKETPLACE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DealCyanPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Find Clients.\nMake Deals. Grow.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 30.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Skip social media hunting. Connect clients with top website developers through smart matching & secure Deal Rooms.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNeedWebsiteClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DealCyanPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("hero_need_website_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "I Need a Site",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onImDesignerClick,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DealIndigo),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("hero_im_designer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = DealIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "I'm a Designer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketplaceStatsStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatPill(number = "480+", label = "Deals Made", modifier = Modifier.weight(1f))
        StatPill(number = "94%", label = "Match Score", modifier = Modifier.weight(1f))
        StatPill(number = "$0", label = "Hidden Fees", modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatPill(number: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = DealCyanPrimary
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SmartMatchingSpotlight(
    currentRole: UserRole,
    projects: List<Project>,
    designers: List<User>,
    onExploreMatches: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Smart Match",
                        tint = DealAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Smart Deal Match Engine",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                MatchScoreBadge(score = 96)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Our algorithmic matcher checks skills, budget compatibility, availability, and website type to pair you instantly with the best opportunities.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = onExploreMatches,
                    label = { Text("E-Commerce (Next.js/Shopify)", fontSize = 11.sp) }
                )
                SuggestionChip(
                    onClick = onExploreMatches,
                    label = { Text("SaaS (Webflow)", fontSize = 11.sp) }
                )
            }
        }
    }
}

@Composable
fun FeaturedProjectCard(
    project: Project,
    matchScore: Int,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onClick: () -> Unit,
    onProposeClick: () -> Unit,
    onExplainMatch: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .testTag("featured_project_card_${project.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(statusText = project.websiteType)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clickable { onExplainMatch() }) {
                        MatchScoreBadge(score = matchScore)
                    }
                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save Project",
                            tint = if (isSaved) DealCyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = project.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Budget & Deadline Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Budget",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${project.budgetMin.toInt()} - $${project.budgetMax.toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DealEmerald
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Deadline",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${project.deadlineDays} Days",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onExplainMatch,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.weight(0.9f).height(38.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Fit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onProposeClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DealCyanPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1.1f)
                        .height(38.dp)
                ) {
                    Text(
                        text = "Send Proposal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TopDesignerCard(
    designer: User,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onClick: () -> Unit,
    onOpenPortfolio: () -> Unit = {},
    onToggleCompare: () -> Unit = {},
    onVerifiedBadgeClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(260.dp)
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .testTag("top_designer_card_${designer.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DealIndigo.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = designer.name.take(1),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DealIndigo
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = designer.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (designer.isVerified) {
                                VerifiedBadge(onClick = onVerifiedBadgeClick)
                            }
                        }

                        StarRatingBar(rating = designer.rating, reviewCount = designer.completedProjectsCount)
                    }
                }

                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isSaved) DealRose else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = designer.headline,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = designer.skills,
                fontSize = 11.sp,
                color = DealCyanPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "From $${designer.startingPrice.toInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DealEmerald
                )

                Text(
                    text = designer.availability,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenPortfolio,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp),
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Text("Portfolio", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onToggleCompare,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp),
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Text("+ Compare", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary)
                }
            }
        }
    }
}

@Composable
fun TrustGuaranteeBanner(onGuidelinesClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = "Shield",
                tint = DealEmerald,
                modifier = Modifier.size(28.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DealNest Deal Protection",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Milestone approvals, verified profiles, and scam prevention safeguard every collaboration.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(onClick = onGuidelinesClick) {
                Text("Safety", fontSize = 12.sp, color = DealCyanPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
