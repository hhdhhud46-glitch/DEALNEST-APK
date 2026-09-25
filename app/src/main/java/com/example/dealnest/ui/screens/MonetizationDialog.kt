package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dealnest.model.User
import com.example.dealnest.ui.DealNestViewModel
import com.example.ui.theme.*

@Composable
fun MonetizationDialog(
    viewModel: DealNestViewModel,
    currentUser: User,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("monetization_dialog")
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = DealAmber, modifier = Modifier.size(24.dp))
                        Column {
                            Text("DealNest Membership", fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Text("Plans & Premium Marketplace Visibility", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Demo Sandbox Notice
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DealAmber.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, DealAmber.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = DealAmber, modifier = Modifier.size(16.dp))
                            Text(
                                text = "MVP Demo Mode: Membership tiers are currently in simulated preview. No real funds are charged until external payment processor integration is live.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    // FREE PLAN CARD
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("FREE PLAN", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("$0 / month", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            listOf(
                                "✓ Basic profile listing in marketplace",
                                "✓ Up to 5 proposal submissions per month",
                                "✓ Basic portfolio project showcase",
                                "✓ Standard client search & browsing"
                            ).forEach { item ->
                                Text(item, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }

                    // DESIGNER PRO PLAN CARD
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(2.dp, DealAmber),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("DESIGNER PRO", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DealAmber)
                                        Surface(shape = RoundedCornerShape(4.dp), color = DealAmber) {
                                            Text("RECOMMENDED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                    Text("For growth-focused web designers & studios", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("$29 / mo", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            listOf(
                                "Unlimited proposal opportunities & bid boosts",
                                "Advanced designer & client analytics funnel",
                                "AI Proposal Writer with multi-tone rewriter",
                                "AI Client Requirement builder access",
                                "Smart Lead Radar with instant budget & skill alerts",
                                "Featured profile options & Golden Pro badge"
                            ).forEach { perk ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = DealEmerald, modifier = Modifier.size(16.dp))
                                    Text(perk, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }

                    // ADD-ON PROMOTIONS
                    Text("PREMIUM VISIBILITY BOOSTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, DealCyanPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Featured Project", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DealCyanPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Top feed pinned spot for 7 days", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$15 / project (Demo)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, DealIndigo.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Featured Designer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DealIndigo)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Hero banner placement on homepage", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$25 / week (Demo)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle / Upgrade Button
                Button(
                    onClick = {
                        val newPro = !currentUser.isPro
                        viewModel.updateProfile(
                            name = currentUser.name,
                            headline = currentUser.headline,
                            skills = currentUser.skills,
                            startingPrice = currentUser.startingPrice,
                            availability = currentUser.availability,
                            location = currentUser.location,
                            about = currentUser.about
                        )
                        viewModel.emitSnackbar(
                            if (newPro) "Demo Pro status activated! Pro features unlocked."
                            else "Demo subscription set to Free tier."
                        )
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DealAmber,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("subscribe_pro_button")
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentUser.isPro) "Active: Manage Demo Pro Plan" else "Activate Designer Pro (Demo)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
