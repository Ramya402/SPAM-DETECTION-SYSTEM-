package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ScamGreen
import com.example.ui.theme.ScamRed
import com.example.ui.theme.ScamYellow

@Composable
fun AdminPanelScreen(
    onBulkUpload: (String) -> Int,
    onResetDb: () -> Unit
) {
    val context = LocalContext.current
    var uploadText by remember { mutableStateOf("") }
    var uploadStatusMessage by remember { mutableStateOf("") }

    val defaultDatasetTemplate = """SCAM, Claim reward now free vacations vouchers click http://rewards-promo-win.net
SCAM, Earn money instantly 500 dollars a day starting collateral deposit
SAFE, Meeting at corporate office tomorrow in main conference board
SAFE, Payment received for outstanding logistics orders invoices
SCAM, Investment guaranteed compound daily returns risk-free http://forex-crypt.com
SAFE, Class reminder chapter 1 machine learning notes required"""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Headers
        Column {
            Text(
                text = "Secured Admin panel",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Ingest intelligence files and manage internal simulation telemetry.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

        // Bulk Dataset Uploader
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bulk Dataset Ingestor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    
                    Text(
                        text = "Fill Template",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            uploadText = defaultDatasetTemplate
                        }
                    )
                }

                Text(
                    text = "Input a comma-separated format list of records (e.g. 'SCAM, text body' or 'SAFE, text body'), one entry per row lines:",
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                OutlinedTextField(
                    value = uploadText,
                    onValueChange = { uploadText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("admin_dataset_textbox"),
                    placeholder = {
                        Text(
                            "SCAM, Congratulations you won! claim refund\nSAFE, Hey did you complete the task?",
                            fontSize = 11.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Button(
                    onClick = {
                        if (uploadText.isBlank()) {
                            Toast.makeText(context, "Insert CSV logs first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val items = onBulkUpload(uploadText)
                        uploadStatusMessage = "Successfully compiled and imported $items training records!"
                        uploadText = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_upload_dataset_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Import DB", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ingest Training Records", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (uploadStatusMessage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ScamGreen.copy(alpha = 0.1f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = uploadStatusMessage,
                            color = ScamGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Database Resets
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Telemetry Diagnostics",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                
                Text(
                    text = "Purging the scam records directory returns your client to clean sandbox settings, clearing all dashboards graphs and historical list items.",
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Button(
                    onClick = {
                        onResetDb()
                        uploadStatusMessage = ""
                        Toast.makeText(context, "Threat Directory Purged!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_clear_records_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ScamRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Purge", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset & Purge DB", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
