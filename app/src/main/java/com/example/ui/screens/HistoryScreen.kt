package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScamRecord
import com.example.ui.theme.ScamGreen
import com.example.ui.theme.ScamRed
import com.example.ui.theme.ScamYellow
import com.example.ui.theme.WhitePure
import java.util.Date

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
    records: List<ScamRecord>,
    onDelete: (Int) -> Unit,
    onSubmitFeedback: (Int, Boolean) -> Unit,
    onClearAll: () -> Unit,
    buildCsvString: (List<ScamRecord>) -> String,
    buildTextReport: (ScamRecord) -> String
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("All") }
    var filterChannel by remember { mutableStateOf("All") }
    
    // Expanded Records Tracking State
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    // Unique Channels represented in database for filter list
    val availableChannels = remember(records) {
        listOf("All") + records.map { it.channel }.distinct().sorted()
    }

    // Filtered Output list
    val filteredRecords = remember(records, searchQuery, filterStatus, filterChannel) {
        records.filter { r ->
            val matchesQuery = r.content.contains(searchQuery, ignoreCase = true) ||
                             r.category.contains(searchQuery, ignoreCase = true) ||
                             r.status.contains(searchQuery, ignoreCase = true)
            val matchesStatus = (filterStatus == "All" || r.status == filterStatus)
            val matchesChannel = (filterChannel == "All" || r.channel == filterChannel)
            matchesQuery && matchesStatus && matchesChannel
        }
    }

    // Export Handler
    fun shareCsvReport(csv: String) {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, csv)
                type = "text/csv"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Export Threat CSV Database"))
        } catch (e: Exception) {
            Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareRecordReport(reportText: String) {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, reportText)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Export Analysis Report"))
        } catch (e: Exception) {
            Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Actions Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Secured Logs Database",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Export CSV database Action button (Feature 9)
                IconButton(
                    onClick = {
                        val csv = buildCsvString(filteredRecords)
                        shareCsvReport(csv)
                    },
                    modifier = Modifier.testTag("export_csv_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share CSV database Report",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Delete entire database button
                IconButton(
                    onClick = { onClearAll() },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear All Threat Records",
                        tint = ScamRed
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search logs by keywords or status...", fontSize = 13.sp) },
            prefix = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(16.dp)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Filters Container
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Status filter dropdown/selector
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .clickable {
                            filterStatus = when (filterStatus) {
                                "All" -> "SCAM"
                                "SCAM" -> "SAFE"
                                else -> "All"
                            }
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = filterStatus, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.List, contentDescription = "Switch status", modifier = Modifier.size(14.dp))
                }
            }

            // Channel Filter Dropdown/selector
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Channel: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .clickable {
                            val idx = availableChannels.indexOf(filterChannel)
                            val nextIdx = (idx + 1) % availableChannels.size
                            filterChannel = availableChannels[nextIdx]
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = filterChannel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Switch channel", modifier = Modifier.size(14.dp))
                }
            }
        }

        // Records List Grid
        if (filteredRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Empty History logs",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No scanning logs matched",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Apply other active filter options or build a new fraud screening analysis.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRecords, key = { it.id }) { record ->
                    val isExpanded = expandedStates[record.id] ?: false
                    val isScam = (record.status == "SCAM")
                    
                    val accentColor = when (record.riskLevel) {
                        "RED" -> ScamRed
                        "YELLOW" -> ScamYellow
                        else -> ScamGreen
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("history_record_card_${record.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable { expandedStates[record.id] = !isExpanded }
                                .padding(12.dp)
                        ) {
                            
                            // Top Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(accentColor, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${record.channel.uppercase()} • ${record.inputType.uppercase()}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                                
                                Text(
                                    text = Date(record.timestamp).toLocaleString().substring(0, 15),
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Main Subject text preview
                            Text(
                                text = record.content,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Compact Status Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Status tag (SAFE/SCAM)
                                    Box(
                                        modifier = Modifier
                                            .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = record.status,
                                            color = accentColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }
                                    
                                    // Category badge
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = record.category,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                                
                                Text(
                                    text = if (isExpanded) "▲" else "▼",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Expanded AI Details Panels
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, bottom = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f))
                                    
                                    // Explanation Area
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(accentColor.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "DECISION REASONING FACTORS:",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = record.explanation,
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                                            )
                                        }
                                    }

                                    // Feedback Adaptive Loop section (Feedback learning!)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Is AI prediction correct?",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (record.userFeedback == null) "Provide user feedback to adapt intelligence parameters." else "Loop recorded: ${record.userFeedback}",
                                                fontSize = 8.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                            )
                                        }
                                        
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            IconButton(
                                                onClick = { onSubmitFeedback(record.id, true) },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(
                                                        if (record.userFeedback == "Correct") ScamGreen else MaterialTheme.colorScheme.surfaceVariant,
                                                        CircleShape
                                                    )
                                            ) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Correct",
                                                    modifier = Modifier.size(12.dp),
                                                    tint = if (record.userFeedback == "Correct") WhitePure else MaterialTheme.colorScheme.onBackground
                                                )
                                            }

                                            IconButton(
                                                onClick = { onSubmitFeedback(record.id, false) },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(
                                                        if (record.userFeedback == "Incorrect") ScamRed else MaterialTheme.colorScheme.surfaceVariant,
                                                        CircleShape
                                                    )
                                            ) {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "Incorrect",
                                                    modifier = Modifier.size(12.dp),
                                                    tint = if (record.userFeedback == "Incorrect") WhitePure else MaterialTheme.colorScheme.onBackground
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    // Actions Bottom
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Export single report (as text payload PDF style)
                                        Button(
                                            onClick = {
                                                val rep = buildTextReport(record)
                                                shareRecordReport(rep)
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Share detailed report", fontSize = 10.sp)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Delete single record button
                                        IconButton(
                                            onClick = { onDelete(record.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Log Record", tint = ScamRed.copy(alpha = 0.8f))
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
}
