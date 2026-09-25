package com.example.dealnest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dealnest.ui.DealNestViewModel
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DealCyanPrimary
import com.example.ui.theme.DealEmerald
import com.example.ui.theme.DealIndigo
import com.example.ui.theme.DealRose

@Composable
fun PaymentsSheet(
    viewModel: DealNestViewModel,
    onDismiss: () -> Unit
) {
    val payments by viewModel.allPaymentTransactions.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .testTag("payments_dialog")
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
                            color = DealEmerald.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = DealEmerald, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text("Payment & Escrow Hub", fontSize = 17.sp, fontWeight = FontWeight.Black)
                            Text("Payment-ready modular infrastructure", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Security & Modular Architecture Notice
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = DealCyanPrimary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Security Guarantee: No card numbers or banking secrets are stored on device. Transactions reflect escrow state and connect directly to PSP webhooks.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Escrow Transaction History", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(payments) { txn ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(txn.projectTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                                        Text(txn.transactionRef, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (txn.status) {
                                            "Paid" -> DealEmerald.copy(alpha = 0.2f)
                                            "Processing" -> DealAmber.copy(alpha = 0.2f)
                                            "Refunded" -> DealIndigo.copy(alpha = 0.2f)
                                            else -> DealRose.copy(alpha = 0.2f)
                                        }
                                    ) {
                                        Text(
                                            text = txn.status,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (txn.status) {
                                                "Paid" -> DealEmerald
                                                "Processing" -> DealAmber
                                                "Refunded" -> DealIndigo
                                                else -> DealRose
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${txn.payerName} → ${txn.payeeName}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = "${currency.symbol}${txn.amount.toInt()} ${currency.code}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DealEmerald
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DealCyanPrimary, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("Close Payments", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
