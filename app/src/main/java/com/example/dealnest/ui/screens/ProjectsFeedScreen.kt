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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dealnest.model.Project
import com.example.dealnest.model.User
import com.example.dealnest.model.UserRole
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.EmptyPlaceholder
import com.example.dealnest.ui.components.MatchScoreBadge
import com.example.dealnest.ui.components.StatusChip
import com.example.dealnest.util.MatchingService
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun ProjectsFeedScreen(
    viewModel: DealNestViewModel,
    projects: List<Project>,
    currentUser: User,
    currentRole: UserRole,
    modifier: Modifier = Modifier
) {
    val searchFilterState by viewModel.searchFilterState.collectAsState()
    val filteredProjects by viewModel.filteredProjects.collectAsState()
    val isSearching = searchFilterState.hasActiveFilters()

    val categories = listOf("All", "E-Commerce", "SaaS", "Portfolio", "Landing Page", "Booking", "Corporate")

    Box(modifier = modifier.fillMaxSize().testTag("projects_feed_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Project Feed",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${filteredProjects.size} active website deals available",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (currentRole == UserRole.CLIENT) {
                            Button(
                                onClick = { viewModel.showPostProjectDialog.value = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DealCyanPrimary,
                                    contentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("post_project_header_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Post Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search input with Advanced Filter button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchFilterState.searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = { Text("Filter by title, client, or tech stack...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = DealCyanPrimary)
                            },
                            trailingIcon = {
                                if (searchFilterState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).testTag("feed_search_input")
                        )

                        IconButton(
                            onClick = { viewModel.showAdvancedSearchSheet.value = true },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(DealCyanLight.copy(alpha = 0.25f))
                                .testTag("feed_advanced_filter_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Advanced Filters",
                                tint = DealCyanPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Category Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            val isSelected = (searchFilterState.websiteType == null && category == "All") ||
                                    searchFilterState.websiteType.equals(category, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateWebsiteTypeFilter(category) },
                                label = {
                                    Text(
                                        text = category,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DealCyanPrimary,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    // Active Filter Chips Strip
                    if (isSearching) {
                        Spacer(modifier = Modifier.height(8.dp))
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
                            if (searchFilterState.minBudget != null || searchFilterState.maxBudget != null) {
                                StatusChip(statusText = "Budget: \$${searchFilterState.minBudget?.toInt() ?: 0} - \$${searchFilterState.maxBudget?.toInt() ?: "Any"}")
                            }
                            if (searchFilterState.requiredSkill != null) {
                                StatusChip(statusText = searchFilterState.requiredSkill ?: "")
                            }
                        }
                    }
                }
            }

            // Empty state
            if (filteredProjects.isEmpty()) {
                item {
                    EmptyPlaceholder(
                        icon = Icons.Outlined.SearchOff,
                        title = "No projects found",
                        subtitle = "Try changing your search keywords or filter category.",
                        actionButtonText = "Reset Filters",
                        onActionClick = {
                            viewModel.clearSearchFilters()
                        }
                    )
                }
            } else {
                // List of Projects
                items(filteredProjects, key = { it.id }) { project ->
                    val matchResult = remember(project, currentUser) {
                        MatchingService.calculateMatch(currentUser, project)
                    }

                    ProjectFeedCard(
                        project = project,
                        matchScore = matchResult.percentage,
                        matchHighlights = matchResult.keyStrengths,
                        isSaved = viewModel.isFavorite("PROJECT", project.id),
                        onSaveToggle = { viewModel.toggleFavorite("PROJECT", project.id) },
                        onClick = {
                            viewModel.selectedProjectForDetail.value = project
                        },
                        onSendProposal = {
                            viewModel.selectedProjectForDetail.value = project
                            viewModel.showSendProposalDialog.value = true
                        },
                        onExplainMatch = {
                            viewModel.openAiMatchExplanation(project, currentUser)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectFeedCard(
    project: Project,
    matchScore: Int,
    matchHighlights: List<String>,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onClick: () -> Unit,
    onSendProposal: () -> Unit,
    onExplainMatch: () -> Unit = {}
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
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            .testTag("project_feed_card_${project.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: Type chip, client name, save icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(statusText = project.websiteType)
                    Text(
                        text = project.clientName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clickable { onExplainMatch() }) {
                        MatchScoreBadge(score = matchScore)
                    }
                    IconButton(onClick = onSaveToggle, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save Project",
                            tint = if (isSaved) DealCyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = project.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = project.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            // Skills chips
            if (project.requiredSkills.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val skillList = project.requiredSkills.split(",").map { it.trim() }.take(3)
                    skillList.forEach { skill ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = skill,
                                fontSize = 11.sp,
                                color = DealCyanPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            // Specs row: Budget, Deadline, Pages
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Budget", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "$${project.budgetMin.toInt()} - $${project.budgetMax.toInt()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = DealEmerald
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Deadline", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${project.deadlineDays} Days",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Pages", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${project.pageCount} Pages",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("View Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSendProposal,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DealCyanPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("Send Proposal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
