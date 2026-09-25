package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dealnest.model.SearchFilterState
import com.example.dealnest.model.SearchType
import com.example.dealnest.ui.DealNestViewModel
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSearchSheet(
    viewModel: DealNestViewModel,
    onDismiss: () -> Unit
) {
    val currentFilter by viewModel.searchFilterState.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    var searchType by remember { mutableStateOf(currentFilter.searchType) }
    var query by remember { mutableStateOf(currentFilter.searchQuery) }
    var minBudget by remember { mutableStateOf(currentFilter.minBudget?.toString() ?: "") }
    var maxBudget by remember { mutableStateOf(currentFilter.maxBudget?.toString() ?: "") }
    var selectedWebsiteType by remember { mutableStateOf(currentFilter.websiteType ?: "All") }
    var selectedSkill by remember { mutableStateOf(currentFilter.requiredSkill ?: "All") }
    var minRating by remember { mutableStateOf(currentFilter.minRating ?: 0f) }
    var minExperience by remember { mutableStateOf(currentFilter.minExperienceYears?.toString() ?: "") }
    var verifiedOnly by remember { mutableStateOf(currentFilter.verifiedOnly) }
    var availableOnly by remember { mutableStateOf(currentFilter.availableOnly) }
    var sortOption by remember { mutableStateOf(currentFilter.sortOption) }

    val websiteTypes = listOf("All", "E-Commerce", "SaaS", "Corporate", "Booking", "Portfolio", "Landing Page")
    val skillsList = listOf("All", "Shopify", "Webflow", "Next.js", "Figma", "WordPress", "React", "Liquid", "Framer")

    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("advanced_search_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null, tint = DealCyanPrimary)
                    Text(
                        text = "Search & Filter Engine",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(
                    onClick = {
                        query = ""
                        minBudget = ""
                        maxBudget = ""
                        selectedWebsiteType = "All"
                        selectedSkill = "All"
                        minRating = 0f
                        minExperience = ""
                        verifiedOnly = false
                        availableOnly = false
                        sortOption = "Relevance"
                        viewModel.clearSearchFilters()
                    }
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Target Segmented Control (All, Projects, Designers)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(SearchType.ALL, "All"),
                    Pair(SearchType.PROJECTS, "Projects"),
                    Pair(SearchType.DESIGNERS, "Designers")
                ).forEach { (type, label) ->
                    val isSelected = searchType == type
                    FilledTonalButton(
                        onClick = { searchType = type },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isSelected) DealCyanPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Search Input with suggestions
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search title, skill, or keyword") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("sheet_search_input")
                )

                // Quick suggestions chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Suggested Searches", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(viewModel.searchSuggestions) { sug ->
                            SuggestionChip(
                                onClick = { query = sug },
                                label = { Text(sug, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Recent searches
                if (recentSearches.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Recent Searches", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "Clear",
                                fontSize = 10.sp,
                                color = DealCyanPrimary,
                                modifier = Modifier.clickable { viewModel.clearRecentSearches() }
                            )
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(recentSearches) { r ->
                                FilterChip(
                                    selected = query == r,
                                    onClick = { query = r },
                                    label = { Text(r, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // WEBSITE TYPE
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Website Type", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(websiteTypes) { type ->
                            FilterChip(
                                selected = selectedWebsiteType == type,
                                onClick = { selectedWebsiteType = type },
                                label = { Text(type, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // SKILLS
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Required Skill", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(skillsList) { sk ->
                            FilterChip(
                                selected = selectedSkill == sk,
                                onClick = { selectedSkill = sk },
                                label = { Text(sk, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // BUDGET RANGE (For projects / starting price for designers)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Budget / Price Range ($)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = minBudget,
                            onValueChange = { minBudget = it },
                            label = { Text("Min $") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = maxBudget,
                            onValueChange = { maxBudget = it },
                            label = { Text("Max $") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // TOGGLES: Verified Only & Available Now
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Verified Only", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Only show DealNest verified badge holders", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = verifiedOnly, onCheckedChange = { verifiedOnly = it })
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Available Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Designers ready for immediate kickoff", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = availableOnly, onCheckedChange = { availableOnly = it })
                        }
                    }
                }

                // SORT BY
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Sort Results By", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    val sortOptions = listOf("Relevance", "Rating", "Experience", "Budget: High to Low", "Budget: Low to High", "Fastest Deadline")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(sortOptions) { sort ->
                            FilterChip(
                                selected = sortOption == sort,
                                onClick = { sortOption = sort },
                                label = { Text(sort, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val updated = SearchFilterState(
                        searchQuery = query.trim(),
                        searchType = searchType,
                        minBudget = minBudget.toDoubleOrNull(),
                        maxBudget = maxBudget.toDoubleOrNull(),
                        websiteType = if (selectedWebsiteType == "All") null else selectedWebsiteType,
                        requiredSkill = if (selectedSkill == "All") null else selectedSkill,
                        verifiedOnly = verifiedOnly,
                        availableOnly = availableOnly,
                        sortOption = sortOption
                    )
                    viewModel.updateSearchFilter(updated)
                    if (query.isNotBlank()) {
                        viewModel.addRecentSearch(query.trim())
                    }
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("apply_search_filters_button")
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Apply Search Filters", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
