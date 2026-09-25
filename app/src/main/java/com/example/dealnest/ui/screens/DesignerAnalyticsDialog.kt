package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dealnest.model.UserRole
import com.example.dealnest.ui.DealNestViewModel
import com.example.ui.theme.*

@Composable
fun DesignerAnalyticsDialog(
    viewModel: DealNestViewModel,
    onDismiss: () -> Unit
) {
    val analytics by viewModel.designerAnalytics.collectAsState()
    val clientAnalytics by viewModel.clientAnalytics.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var selectedTab by remember {
        mutableStateOf(if (currentRole == UserRole.CLIENT) "Client" else "Designer")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("designer_analytics_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DealCyanPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Insights, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text(
                                text = "Marketplace Analytics",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Performance metrics, funnel & project activity",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Switcher: Designer Analytics vs Client Analytics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Designer", "Client").forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) DealCyanPrimary else Color.Transparent,
                            onClick = { selectedTab = tab },
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$tab Analytics",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTab == "Designer") {
                    // DESIGNER ANALYTICS
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Total Earnings Hero Banner
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                border = BorderStroke(1.dp, DealEmerald.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "LIFETIME CLOSED DEALS",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DealEmerald,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = viewModel.formatPrice(analytics.totalEarningsUsd),
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${analytics.completedProjects} deals completed • 4.9★ avg rating",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = DealEmerald.copy(alpha = 0.15f),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = DealEmerald)
                                        }
                                    }
                                }
                            }
                        }

                        // Traffic & Discovery Metrics
                        item {
                            Text(
                                text = "TRAFFIC & PROFILE DISCOVERY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AnalyticsMetricCard(
                                    title = "Profile Views",
                                    value = "${analytics.profileViews}",
                                    subtitle = "+18% this month",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricCard(
                                    title = "Portfolio Views",
                                    value = "${analytics.portfolioViews}",
                                    subtitle = "+32% this month",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricCard(
                                    title = "Projects Viewed",
                                    value = "214",
                                    subtitle = "+12% active search",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Proposal Conversion Funnel
                        item {
                            Text(
                                text = "PROPOSALS & DEALS PIPELINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AnalyticsMetricCard(
                                    title = "Proposals Sent",
                                    value = "${analytics.proposalsSent}",
                                    subtitle = "Past 30 days",
                                    isPositive = null,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricCard(
                                    title = "Proposals Viewed",
                                    value = "${analytics.proposalViews}",
                                    subtitle = "80% read rate",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricCard(
                                    title = "Accepted",
                                    value = "${analytics.proposalsAccepted}",
                                    subtitle = "66.7% win rate",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Active Deals & Reputation
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AnalyticsMetricCard(
                                    title = "Active Deals",
                                    value = "${analytics.activeDeals}",
                                    subtitle = "In progress",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricCard(
                                    title = "Completed Deals",
                                    value = "${analytics.completedProjects}",
                                    subtitle = "100% delivered",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricCard(
                                    title = "Avg Rating",
                                    value = "4.9 ★",
                                    subtitle = "28 verified reviews",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Weekly Views Bar Chart
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Weekly Views Momentum", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("Daily Avg: ~240", fontSize = 11.sp, color = DealCyanPrimary, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                                    val data = analytics.weeklyViews
                                    val maxVal = data.maxOrNull()?.toFloat() ?: 350f

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        data.forEachIndexed { i, count ->
                                            val fraction = (count / maxVal).coerceIn(0.15f, 1f)
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "$count",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .width(18.dp)
                                                        .fillMaxHeight(fraction)
                                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                        .background(
                                                            Brush.verticalGradient(
                                                                listOf(DealCyanPrimary, DealIndigo)
                                                            )
                                                        )
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = days.getOrElse(i) { "" },
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Growth Takeaway
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DealCyanPrimary.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, DealCyanPrimary.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(16.dp))
                                        Text("Growth Takeaway", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Proposals highlighting specific Next.js & Shopify project results close 32% faster. Keep sharing specialized case studies!",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // CLIENT ANALYTICS
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Privacy Protection Notice
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DealEmeraldLight.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, DealEmerald.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = DealEmerald, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "Client Privacy Protected: Financial spending metrics are private to your account and never exposed to other marketplace users.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        // Client Spending Hero Banner
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                border = BorderStroke(1.dp, DealCyanPrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "TOTAL PROJECT SPENDING",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DealCyanPrimary,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = viewModel.formatPrice(clientAnalytics.totalProjectSpendingUsd),
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${clientAnalytics.completedProjects} websites successfully commissioned & delivered",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = DealCyanPrimary.copy(alpha = 0.15f),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = DealCyanPrimary)
                                        }
                                    }
                                }
                            }
                        }

                        // Project Sourcing Activity
                        item {
                            Text(
                                text = "PROJECT SOURCING & PROPOSALS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AnalyticsMetricCard(
                                    title = "Projects Posted",
                                    value = "${clientAnalytics.projectsPosted}",
                                    subtitle = "5 total listings",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricCard(
                                    title = "Proposals Received",
                                    value = "${clientAnalytics.proposalsReceived}",
                                    subtitle = "~3.8 per project",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricCard(
                                    title = "Designers Contacted",
                                    value = "${clientAnalytics.designersContacted}",
                                    subtitle = "Direct interviews",
                                    isPositive = null,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Deals Activity
                        item {
                            Text(
                                text = "WORKPLACE DEALS & ESCROW",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AnalyticsMetricCard(
                                    title = "Active Deals",
                                    value = "${clientAnalytics.activeDeals}",
                                    subtitle = "In development",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricCard(
                                    title = "Completed Projects",
                                    value = "${clientAnalytics.completedProjects}",
                                    subtitle = "100% satisfaction",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricCard(
                                    title = "Milestone Escrow",
                                    value = "Protected",
                                    subtitle = "Safe release",
                                    isPositive = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Monthly Spending Chart
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Monthly Design Spending", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("5-Month Trend", fontSize = 11.sp, color = DealCyanPrimary, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    val months = listOf("May", "Jun", "Jul", "Aug", "Sep")
                                    val spendData = clientAnalytics.monthlySpendingUsd
                                    val maxSpend = spendData.maxOrNull()?.toFloat() ?: 3000f

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        spendData.forEachIndexed { i, amount ->
                                            val fraction = (amount.toFloat() / maxSpend).coerceIn(0.15f, 1f)
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "$${amount.toInt()}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .width(22.dp)
                                                        .fillMaxHeight(fraction)
                                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                        .background(
                                                            Brush.verticalGradient(
                                                                listOf(DealEmerald, DealCyanPrimary)
                                                            )
                                                        )
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = months.getOrElse(i) { "" },
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DealCyanPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("Close Analytics", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AnalyticsMetricCard(
    title: String,
    value: String,
    subtitle: String,
    isPositive: Boolean?,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = when (isPositive) {
                    true -> DealEmerald
                    false -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 1
            )
        }
    }
}
