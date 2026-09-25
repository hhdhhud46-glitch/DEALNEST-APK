package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
import com.example.dealnest.model.AiClientRequirementBrief
import com.example.dealnest.util.AiRequirementBuilder
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealEmerald
import com.example.ui.theme.DealIndigo

@Composable
fun AiRequirementBuilderDialog(
    onDismiss: () -> Unit,
    onApplyBrief: (
        title: String,
        websiteType: String,
        description: String,
        budgetMin: Double,
        budgetMax: Double,
        deadlineDays: Int,
        requiredSkills: String,
        pageCount: Int,
        requiredFeatures: String
    ) -> Unit
) {
    var rawPrompt by remember { mutableStateOf("I need a website for my clothing shop.") }
    var generatedBrief by remember { mutableStateOf<AiClientRequirementBrief?>(null) }

    // Editable state fields when generated
    var editableTitle by remember { mutableStateOf("") }
    var editableType by remember { mutableStateOf("") }
    var editableDesc by remember { mutableStateOf("") }
    var editableBudgetMin by remember { mutableStateOf("1500") }
    var editableBudgetMax by remember { mutableStateOf("3500") }
    var editableDeadline by remember { mutableStateOf("14") }
    var editableSkills by remember { mutableStateOf("") }
    var editablePagesCount by remember { mutableStateOf("5") }
    var editableFeatures by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "I need a website for my clothing shop.",
        "SaaS landing page for an AI writing tool.",
        "Dental clinic booking and patient portal.",
        "Luxury interior design agency portfolio.",
        "Artisanal bakery and online order pickup."
    )

    fun runAiGeneration(promptText: String) {
        val brief = AiRequirementBuilder.buildFromPrompt(promptText)
        generatedBrief = brief
        editableTitle = brief.projectTitle
        editableType = brief.websiteType
        editableDesc = brief.generatedDescription
        editableBudgetMin = brief.suggestedBudgetMin.toInt().toString()
        editableBudgetMax = brief.suggestedBudgetMax.toInt().toString()
        editableDeadline = brief.suggestedDeadlineDays.toString()
        editableSkills = brief.requiredSkills.joinToString(", ")
        editablePagesCount = brief.pageCount.toString()
        editableFeatures = brief.suggestedFeatures.joinToString(", ")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("ai_requirement_builder_dialog")
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
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text("AI Project Builder", fontSize = 17.sp, fontWeight = FontWeight.Black)
                            Text("Turn a single sentence into a full project brief", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Form Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Input prompt field
                    OutlinedTextField(
                        value = rawPrompt,
                        onValueChange = { rawPrompt = it },
                        label = { Text("What kind of website do you need?") },
                        placeholder = { Text("e.g. I need a website for my clothing shop...") },
                        modifier = Modifier.fillMaxWidth().testTag("ai_builder_prompt_input")
                    )

                    // Quick suggestion chips
                    Text("Quick Ideas:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickPrompts.take(2).forEach { qp ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                onClick = {
                                    rawPrompt = qp
                                    runAiGeneration(qp)
                                }
                            ) {
                                Text(
                                    text = qp.take(30) + "...",
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { runAiGeneration(rawPrompt) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("generate_ai_brief_btn")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate Structured Project Brief", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Mandatory AI Disclaimer
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
                            Icon(Icons.Default.Info, contentDescription = null, tint = DealAmber, modifier = Modifier.size(15.dp))
                            Text(
                                text = "Important: AI suggestions are estimates only. Do not present estimated prices or deadlines as guarantees.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 13.sp
                            )
                        }
                    }

                    // Generated Results & Editable Fields
                    if (generatedBrief != null) {
                        val brief = generatedBrief!!

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = BorderStroke(1.dp, DealCyanPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("AI GENERATED BRIEF (EDITABLE)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DealCyanPrimary)
                                    Surface(shape = RoundedCornerShape(6.dp), color = DealIndigo.copy(alpha = 0.2f)) {
                                        Text(
                                            "Complexity: ${brief.estimatedComplexity}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DealIndigo,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = editableTitle,
                                    onValueChange = { editableTitle = it },
                                    label = { Text("Project Title") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = editableType,
                                        onValueChange = { editableType = it },
                                        label = { Text("Website Type") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = editablePagesCount,
                                        onValueChange = { editablePagesCount = it },
                                        label = { Text("Pages") },
                                        modifier = Modifier.weight(0.5f)
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = editableBudgetMin,
                                        onValueChange = { editableBudgetMin = it },
                                        label = { Text("Min Budget ($)") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = editableBudgetMax,
                                        onValueChange = { editableBudgetMax = it },
                                        label = { Text("Max Budget ($)") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = editableDeadline,
                                        onValueChange = { editableDeadline = it },
                                        label = { Text("Days") },
                                        modifier = Modifier.weight(0.7f)
                                    )
                                }

                                OutlinedTextField(
                                    value = editableSkills,
                                    onValueChange = { editableSkills = it },
                                    label = { Text("Required Skills") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = editableFeatures,
                                    onValueChange = { editableFeatures = it },
                                    label = { Text("Suggested Features") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = editableDesc,
                                    onValueChange = { editableDesc = it },
                                    label = { Text("Brief Scope & Description") },
                                    minLines = 4,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Suggested Questions for Client
                                Column {
                                    Text("Suggested Questions for You to Consider:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DealAmber)
                                    brief.suggestedQuestionsForClient.forEach { q ->
                                        Text("• $q", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Apply button
                Button(
                    onClick = {
                        val min = editableBudgetMin.toDoubleOrNull() ?: 1000.0
                        val max = editableBudgetMax.toDoubleOrNull() ?: 2500.0
                        val days = editableDeadline.toIntOrNull() ?: 14
                        val pages = editablePagesCount.toIntOrNull() ?: 5

                        onApplyBrief(
                            editableTitle.ifBlank { "Website Project" },
                            editableType.ifBlank { "E-Commerce" },
                            editableDesc,
                            min,
                            max,
                            days,
                            editableSkills.ifBlank { "Shopify, Figma, Tailwind CSS" },
                            pages,
                            editableFeatures.ifBlank { "Responsive UI, Cart, SEO" }
                        )
                        onDismiss()
                    },
                    enabled = generatedBrief != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DealEmerald, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("apply_ai_brief_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Apply to Project Form", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
