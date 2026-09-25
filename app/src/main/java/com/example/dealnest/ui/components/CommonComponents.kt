package com.example.dealnest.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dealnest.model.UserRole
import com.example.ui.theme.*

@Composable
fun DealNestHeader(
    currentRole: UserRole,
    unreadNotifCount: Int,
    unreadChatCount: Int = 0,
    onRoleClick: () -> Unit,
    onNotifClick: () -> Unit,
    onSafetyClick: () -> Unit,
    onChatClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Tagline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(DealCyanPrimary, DealIndigo)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Handshake,
                        contentDescription = "DealNest Icon",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "DEAL",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "NEST",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = DealCyanPrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "Find Clients. Make Deals.",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Role Switcher & Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Role Badge Button
                Surface(
                    onClick = onRoleClick,
                    shape = RoundedCornerShape(20.dp),
                    color = when (currentRole) {
                        UserRole.CLIENT -> DealIndigoLight.copy(alpha = 0.25f)
                        UserRole.DESIGNER -> DealCyanLight.copy(alpha = 0.25f)
                        UserRole.ADMIN -> DealAmberLight.copy(alpha = 0.25f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when (currentRole) {
                            UserRole.CLIENT -> DealIndigo
                            UserRole.DESIGNER -> DealCyanPrimary
                            UserRole.ADMIN -> DealAmber
                        }
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("role_switcher_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (currentRole) {
                                UserRole.CLIENT -> Icons.Default.BusinessCenter
                                UserRole.DESIGNER -> Icons.Default.Palette
                                UserRole.ADMIN -> Icons.Default.Shield
                            },
                            contentDescription = "Role",
                            tint = when (currentRole) {
                                UserRole.CLIENT -> DealIndigo
                                UserRole.DESIGNER -> DealCyanPrimary
                                UserRole.ADMIN -> DealAmber
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = currentRole.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Safety / Guidelines Icon
                IconButton(
                    onClick = onSafetyClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("safety_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.GppGood,
                        contentDescription = "Safety & Guidelines",
                        tint = DealEmerald,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Chat Icon with badge
                if (onChatClick != null) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(
                            onClick = onChatClick,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("header_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = "Messages",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (unreadChatCount > 0) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp, end = 4.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(DealCyanPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadChatCount > 9) "9+" else "$unreadChatCount",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // Notifications Icon with badge
                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(
                        onClick = onNotifClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    if (unreadNotifCount > 0) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(DealRose),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unreadNotifCount",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchScoreBadge(score: Int, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DealCyanLight.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, DealCyanPrimary),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Match",
                tint = DealCyanPrimary,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = "$score% Match",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = DealCyanPrimary
            )
        }
    }
}

@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val badgeModifier = if (onClick != null) {
        modifier.clip(RoundedCornerShape(10.dp)).clickable { onClick() }
    } else {
        modifier
    }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DealEmeraldLight.copy(alpha = 0.25f),
        border = BorderStroke(1.dp, DealEmerald.copy(alpha = 0.6f)),
        modifier = badgeModifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified badge",
                tint = DealEmerald,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "Verified",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = DealEmerald
            )
        }
    }
}

@Composable
fun StatusChip(statusText: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (statusText.uppercase()) {
        "OPEN" -> Pair(DealEmeraldLight.copy(alpha = 0.2f), DealEmerald)
        "IN_PROGRESS", "IN PROGRESS" -> Pair(DealCyanLight.copy(alpha = 0.2f), DealCyanPrimary)
        "REVIEW", "PROPOSALS_REVIEW" -> Pair(DealAmberLight.copy(alpha = 0.2f), DealAmber)
        "COMPLETED" -> Pair(DealIndigoLight.copy(alpha = 0.2f), DealIndigo)
        "ACCEPTED" -> Pair(DealEmeraldLight.copy(alpha = 0.2f), DealEmerald)
        "REJECTED" -> Pair(DealRoseLight.copy(alpha = 0.2f), DealRose)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = statusText.replace("_", " "),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun StarRatingBar(rating: Float, reviewCount: Int = 0, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Star",
            tint = DealAmber,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = String.format("%.1f", rating),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (reviewCount > 0) {
            Text(
                text = "($reviewCount)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyPlaceholder(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DealCyanPrimary,
                    contentColor = Color.Black
                ),
                modifier = Modifier.height(44.dp)
            ) {
                Text(actionButtonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AvatarImage(
    name: String,
    avatarUrl: String = "",
    size: androidx.compose.ui.unit.Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(DealIndigo.copy(alpha = 0.2f))
            .border(1.dp, DealCyanPrimary.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            fontSize = (size.value * 0.42f).sp,
            fontWeight = FontWeight.Black,
            color = DealCyanPrimary
        )
    }
}
