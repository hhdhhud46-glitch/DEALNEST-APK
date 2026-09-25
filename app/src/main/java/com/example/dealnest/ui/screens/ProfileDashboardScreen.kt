package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dealnest.model.*
import com.example.dealnest.ui.AppTab
import com.example.dealnest.ui.DealNestViewModel
import com.example.dealnest.ui.components.EmptyPlaceholder
import com.example.dealnest.ui.components.StarRatingBar
import com.example.dealnest.ui.components.StatusChip
import com.example.dealnest.ui.components.VerifiedBadge
import com.example.ui.theme.*

@Composable
fun ProfileDashboardScreen(
    viewModel: DealNestViewModel,
    currentUser: User,
    currentRole: UserRole,
    projects: List<Project>,
    proposals: List<Proposal>,
    deals: List<Deal>,
    favorites: List<FavoriteItem>,
    modifier: Modifier = Modifier
) {
    val allReviews by viewModel.allReviews.collectAsState()
    val userReviews = remember(allReviews, currentUser.id) {
        allReviews.filter { it.targetUserId == currentUser.id }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(DealCyanPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.name.take(1),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DealCyanPrimary
                                )
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = currentUser.name,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (currentUser.isVerified) {
                                        VerifiedBadge(onClick = { viewModel.showVerificationInfoDialog.value = true })
                                    }
                                }
                                Text(
                                    text = currentUser.headline,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.showEditProfileDialog.value = true },
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = DealCyanPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bio & Details
                    Text(
                        text = currentUser.about,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StarRatingBar(rating = currentUser.rating, reviewCount = currentUser.completedProjectsCount)
                        Text(
                            text = "📍 ${currentUser.location}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (currentUser.isPro) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DealAmberLight.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "★ PRO MEMBER",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DealAmber,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Verification Status & Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (currentUser.isVerified) {
                                VerifiedBadge(onClick = { viewModel.showVerificationInfoDialog.value = true })
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable { viewModel.showVerificationInfoDialog.value = true }
                                ) {
                                    Text(
                                        text = currentUser.verificationStatus,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { viewModel.showVerificationInfoDialog.value = true },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = "Verification Info", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            }
                        }

                        if (!currentUser.isVerified && currentRole == UserRole.DESIGNER) {
                            Button(
                                onClick = { viewModel.showVerificationRequestDialog.value = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("request_verification_button")
                            ) {
                                Text("Get Verified", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ROLE SWITCHER BANNER
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Active Marketplace Mode",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(UserRole.CLIENT, UserRole.DESIGNER, UserRole.ADMIN).forEach { role ->
                            val isSelected = currentRole == role
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.switchRole(role) },
                                label = {
                                    Text(
                                        text = role.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (role) {
                                            UserRole.CLIENT -> Icons.Default.BusinessCenter
                                            UserRole.DESIGNER -> Icons.Default.Palette
                                            UserRole.ADMIN -> Icons.Default.Shield
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DealCyanPrimary,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // STATS OVERVIEW CARDS
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardStatCard(
                    title = if (currentRole == UserRole.CLIENT) "Active Projects" else "Sent Proposals",
                    value = if (currentRole == UserRole.CLIENT) "${projects.size}" else "${proposals.size}",
                    color = DealCyanPrimary,
                    modifier = Modifier.weight(1f)
                )
                DashboardStatCard(
                    title = "Active Deals",
                    value = "${deals.filter { it.status != DealStatus.COMPLETED.name }.size}",
                    color = DealEmerald,
                    modifier = Modifier.weight(1f)
                )
                DashboardStatCard(
                    title = "Saved Items",
                    value = "${favorites.size}",
                    color = DealAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ROLE SPECIFIC CONTENT
        if (currentRole == UserRole.CLIENT) {
            // CLIENT DASHBOARD SECTION
            item {
                ClientDashboardSection(
                    projects = projects,
                    proposals = proposals,
                    onPostNewProject = { viewModel.showPostProjectDialog.value = true },
                    onAcceptProposal = { viewModel.acceptProposal(it) },
                    onRejectProposal = { viewModel.rejectProposal(it) },
                    onChatProposal = { viewModel.startChatForProposal(it) }
                )
            }
        } else if (currentRole == UserRole.DESIGNER) {
            // DESIGNER DASHBOARD SECTION
            item {
                DesignerDashboardSection(
                    proposals = proposals,
                    deals = deals,
                    onBrowseProjects = { viewModel.switchTab(AppTab.PROJECTS) },
                    onChatProposal = { viewModel.startChatForProposal(it) }
                )
            }

            // DESIGNER PORTFOLIO BUILDER SECTION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Public, contentDescription = null, tint = DealCyanPrimary)
                                Column {
                                    Text("Professional Portfolio", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Showcase projects, services & rates", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.openDesignerPortfolio(currentUser)
                                    viewModel.showPortfolioBuilderDialog.value = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(38.dp).testTag("edit_portfolio_button")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Portfolio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.openPublicPortfolio(currentUser)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                                modifier = Modifier.weight(1f).height(38.dp).testTag("preview_portfolio_button")
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Preview Public", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // REVIEWS & REPUTATION SECTION (For both Client & Designer)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Reviews & Reputation",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${currentUser.rating} ★ average across ${userReviews.size} verified deals",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        StarRatingBar(rating = currentUser.rating, reviewCount = userReviews.size)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rating Breakdown Bars (5 stars to 1 star)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val totalRevs = userReviews.size.coerceAtLeast(1)
                            RatingBarRow(label = "5 Stars", count = currentUser.ratingBreakdown5, total = totalRevs)
                            RatingBarRow(label = "4 Stars", count = currentUser.ratingBreakdown4, total = totalRevs)
                            RatingBarRow(label = "3 Stars", count = currentUser.ratingBreakdown3, total = totalRevs)
                            RatingBarRow(label = "2 Stars", count = currentUser.ratingBreakdown2, total = totalRevs)
                            RatingBarRow(label = "1 Star", count = currentUser.ratingBreakdown1, total = totalRevs)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Recent Reviews", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    if (userReviews.isEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No reviews received yet. Complete deals to build your public platform reputation.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        userReviews.take(4).forEach { rev ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(rev.authorName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Role: ${rev.authorRole} • For: ${rev.projectTitle}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = DealAmber, modifier = Modifier.size(13.dp))
                                            Text(" ${rev.rating}.0", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                            Spacer(modifier = Modifier.width(6.dp))

                                            IconButton(
                                                onClick = { viewModel.reportReview(rev.id, "Flagged by user as inappropriate") },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Outlined.Flag, contentDescription = "Report Review", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }

                                    if (rev.categoryFeedback.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = DealCyanLight.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = rev.categoryFeedback,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = DealCyanPrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = rev.reviewText,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // QUICK ACTIONS MENU (Monetization, Admin Panel, Safety & Guidelines)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Platform Tools & Settings",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Designer Analytics
                NavigationSettingRow(
                    icon = Icons.Default.Insights,
                    title = "Designer Analytics & Funnel",
                    subtitle = "Track profile views, conversion rate, and weekly momentum",
                    badge = "NEW",
                    onClick = { viewModel.showDesignerAnalyticsDialog.value = true }
                )

                // Portfolio Builder
                NavigationSettingRow(
                    icon = Icons.Default.Public,
                    title = "Public Portfolio Builder",
                    subtitle = "Manage case studies, services, and shareable dealnest.com URL",
                    badge = "SHARE",
                    onClick = { viewModel.openDesignerPortfolio(currentUser) }
                )

                // Smart Lead Radar
                NavigationSettingRow(
                    icon = Icons.Default.Radar,
                    title = "Smart Lead Radar & Alerts",
                    subtitle = "Set minimum budget and skill filters for instant deal notifications",
                    badge = "RADAR",
                    onClick = { viewModel.showLeadAlertsDialog.value = true }
                )

                // Client Designer Comparison
                NavigationSettingRow(
                    icon = Icons.Default.CompareArrows,
                    title = "Compare Designers Matrix",
                    subtitle = "Side-by-side comparison of up to 3 designers on skills, rates & ratings",
                    badge = "COMPARE",
                    onClick = { viewModel.showClientComparisonDialog.value = true }
                )

                // Monetization
                NavigationSettingRow(
                    icon = Icons.Default.WorkspacePremium,
                    title = "DealNest Pro & Monetization",
                    subtitle = "Boost profile visibility and get instant lead alerts",
                    badge = if (currentUser.isPro) "ACTIVE" else "UPGRADE",
                    onClick = { viewModel.showMonetizationDialog.value = true }
                )

                // Referral Program & Rewards (Feature 19)
                NavigationSettingRow(
                    icon = Icons.Default.People,
                    title = "Referral Program & Rewards",
                    subtitle = "Share code, invite friends and track credited rewards",
                    badge = "EARN $50",
                    onClick = { viewModel.showReferralDialog.value = true }
                )

                // Payment History & Escrow (Feature 13)
                NavigationSettingRow(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Escrow & Payment Architecture",
                    subtitle = "Transaction ledger, milestone release status and payment methods",
                    badge = "ESCROW",
                    onClick = { viewModel.showPaymentsDialog.value = true }
                )

                // Admin Dashboard
                NavigationSettingRow(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Admin Console",
                    subtitle = "Manage users, disputes, reports, approvals and platform KPIs",
                    badge = "ADMIN",
                    onClick = { viewModel.showAdminDashboard.value = true }
                )

                // Safety & Trust
                NavigationSettingRow(
                    icon = Icons.Default.Security,
                    title = "Safety & Community Guidelines",
                    subtitle = "Scam warning policies, terms, and report center",
                    badge = null,
                    onClick = { viewModel.showSafetyDialog.value = true }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Global Marketplace Preparation (Feature 20)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(18.dp))
                            Text("Global Marketplace Localization", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 1. Currency Selection: INR, USD, EUR, GBP
                        Text(
                            text = "Display Currency (INR, USD, EUR, GBP):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val activeCurrency by viewModel.selectedCurrency.collectAsState()
                            listOf(
                                SupportedCurrency.INR,
                                SupportedCurrency.USD,
                                SupportedCurrency.EUR,
                                SupportedCurrency.GBP
                            ).forEach { curr ->
                                val isSelected = activeCurrency == curr
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.selectCurrency(curr) },
                                    label = { Text("${curr.code} (${curr.symbol})", fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. Language Selection: English, Hindi, Spanish, French, Arabic, German
                        Text(
                            text = "Interface Language:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val activeLanguage by viewModel.selectedLanguage.collectAsState()
                            SupportedLanguage.values().forEach { lang ->
                                val isSelected = activeLanguage == lang
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.selectLanguage(lang) },
                                    label = { Text("${lang.displayName} (${lang.nativeName})", fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3. Time Zone Selection
                        Text(
                            text = "Time Zone Setting:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val activeTz by viewModel.selectedTimeZone.collectAsState()
                            listOf(
                                "UTC-8 (PST)",
                                "UTC-5 (EST)",
                                "UTC+0 (GMT)",
                                "UTC+1 (CET)",
                                "UTC+5:30 (IST)",
                                "UTC+8 (SGT)"
                            ).forEach { tz ->
                                val isSelected = activeTz == tz
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.selectTimeZone(tz) },
                                    label = { Text(tz, fontSize = 10.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Currency estimate disclaimer
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DealAmber.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, DealAmber.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = DealAmber, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Notice: Indicative exchange rates are platform estimates. Real transaction amounts are locked in the contract currency.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ClientDashboardSection(
    projects: List<Project>,
    proposals: List<Proposal>,
    onPostNewProject: () -> Unit,
    onAcceptProposal: (Proposal) -> Unit,
    onRejectProposal: (Proposal) -> Unit,
    onChatProposal: (Proposal) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Received Proposals (${proposals.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = onPostNewProject,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Post New", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (proposals.isEmpty()) {
            EmptyPlaceholder(
                icon = Icons.Outlined.Assignment,
                title = "No proposals yet",
                subtitle = "Post a project to start receiving proposals from verified designers."
            )
        } else {
            proposals.forEach { proposal ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = proposal.designerName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = proposal.designerHeadline,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            StatusChip(statusText = proposal.status)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "For: ${proposal.projectTitle}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DealCyanPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = proposal.coverMessage,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$${proposal.proposedPrice.toInt()} (${proposal.deliveryDays} Days)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DealEmerald
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilledTonalButton(
                                    onClick = { onChatProposal(proposal) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                if (proposal.status == ProposalStatus.PENDING.name) {
                                    OutlinedButton(
                                        onClick = { onRejectProposal(proposal) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Decline", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { onAcceptProposal(proposal) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Accept Deal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DesignerDashboardSection(
    proposals: List<Proposal>,
    deals: List<Deal>,
    onBrowseProjects: () -> Unit,
    onChatProposal: (Proposal) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Sent Proposals",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(onClick = onBrowseProjects) {
                Text("Browse Feed", fontSize = 12.sp, color = DealCyanPrimary, fontWeight = FontWeight.Bold)
            }
        }

        proposals.forEach { proposal ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = proposal.projectTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        StatusChip(statusText = proposal.status)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Offered: $${proposal.proposedPrice.toInt()} in ${proposal.deliveryDays} days",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DealEmerald
                        )

                        FilledTonalButton(
                            onClick = { onChatProposal(proposal) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chat with Client", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = proposal.coverMessage,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationSettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    badge: String?,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DealCyanPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(20.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DealAmberLight.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = DealAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
