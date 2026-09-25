package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dealnest.ui.DealNestViewModel
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealEmerald

@Composable
fun LeadAlertsConfigDialog(
    viewModel: DealNestViewModel,
    onDismiss: () -> Unit
) {
    val existingConfig by viewModel.currentLeadAlertConfig.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()

    var isRadarEnabled by remember(existingConfig) {
        mutableStateOf(existingConfig?.isEnabled ?: true)
    }

    val availableSkills = listOf(
        "React",
        "Next.js",
        "WordPress",
        "Shopify",
        "UI/UX",
        "E-commerce",
        "HTML/CSS",
        "Other"
    )

    var selectedSkillsList by remember(existingConfig) {
        val initial = (existingConfig?.monitoredSkills ?: "React, Next.js, Shopify, UI/UX")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
        mutableStateOf(initial.toSet())
    }

    var minBudgetText by remember(existingConfig) {
        mutableStateOf((existingConfig?.minBudget ?: 1500.0).toInt().toString())
    }
    var preferredTypes by remember(existingConfig) {
        mutableStateOf(existingConfig?.preferredWebsiteTypes ?: "SaaS, E-Commerce, Landing Page")
    }
    var preferredLanguages by remember { mutableStateOf("English, Spanish, Hindi") }
    var availabilityPreference by remember { mutableStateOf("Immediate (Within 48 hrs)") }
    var locationPreference by remember { mutableStateOf("Global / Remote Worldwide") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("lead_alerts_dialog")
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
                                Icon(Icons.Default.Radar, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text(
                                text = "Smart Lead Preferences",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Instant notifications when matching projects are posted",
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

                // Toggle Radar Status
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, if (isRadarEnabled) DealEmerald.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isRadarEnabled) "🔥 Lead Alerts Active" else "Lead Alerts Paused",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRadarEnabled) DealEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Push notification: '🔥 New project matches your skills'",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = isRadarEnabled,
                            onCheckedChange = { isRadarEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = DealCyanPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Skills Multiselect Chips
                    Text(
                        text = "MONITORED SKILLS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DealCyanPrimary,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableSkills.take(4).forEach { skill ->
                            val isSelected = selectedSkillsList.contains(skill)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedSkillsList = if (isSelected) selectedSkillsList - skill else selectedSkillsList + skill
                                },
                                label = { Text(skill, fontSize = 10.sp) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableSkills.drop(4).forEach { skill ->
                            val isSelected = selectedSkillsList.contains(skill)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedSkillsList = if (isSelected) selectedSkillsList - skill else selectedSkillsList + skill
                                },
                                label = { Text(skill, fontSize = 10.sp) }
                            )
                        }
                    }

                    // Min Budget Input
                    OutlinedTextField(
                        value = minBudgetText,
                        onValueChange = { minBudgetText = it },
                        label = { Text("Minimum Project Budget (${selectedCurrency.code})") },
                        placeholder = { Text("1500") },
                        supportingText = { Text("Ignore leads with client budgets below this threshold.") },
                        modifier = Modifier.fillMaxWidth().testTag("radar_min_budget")
                    )

                    // Preferred Website Types
                    OutlinedTextField(
                        value = preferredTypes,
                        onValueChange = { preferredTypes = it },
                        label = { Text("Preferred Project Types") },
                        placeholder = { Text("SaaS, E-Commerce, Landing Page, Corporate") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Preferred Languages
                    OutlinedTextField(
                        value = preferredLanguages,
                        onValueChange = { preferredLanguages = it },
                        label = { Text("Preferred Client Communication Languages") },
                        placeholder = { Text("English, Spanish, Hindi, French") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Availability Preference
                    OutlinedTextField(
                        value = availabilityPreference,
                        onValueChange = { availabilityPreference = it },
                        label = { Text("Availability Preference") },
                        placeholder = { Text("Immediate, 20 hrs/week, Next Month") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Location Preference
                    OutlinedTextField(
                        value = locationPreference,
                        onValueChange = { locationPreference = it },
                        label = { Text("Location Preference") },
                        placeholder = { Text("Global / Remote, North America, Europe") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val budget = minBudgetText.toDoubleOrNull() ?: 1000.0
                        val skillsStr = selectedSkillsList.joinToString(", ")
                        viewModel.saveLeadAlertConfig(
                            monitoredSkills = skillsStr,
                            minBudget = budget,
                            preferredTypes = preferredTypes,
                            isEnabled = isRadarEnabled
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DealCyanPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_lead_alerts_btn")
                ) {
                    Text("Save Lead Radar Preferences", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
