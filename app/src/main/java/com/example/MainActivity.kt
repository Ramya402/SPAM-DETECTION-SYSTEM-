package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ScamRed
import com.example.viewmodel.ScamViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ScamViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Theme State Persistence via SharedPreferences
            val context = LocalContext.current
            val sharedPrefs = remember { context.getSharedPreferences("shield_settings", android.content.Context.MODE_PRIVATE) }
            var themeMode by rememberSaveable {
                mutableStateOf(sharedPrefs.getString("theme_mode", "system") ?: "system")
            }

            val updateThemeMode: (String) -> Unit = { mode ->
                themeMode = mode
                sharedPrefs.edit().putString("theme_mode", mode).apply()
            }

            val systemDark = isSystemInDarkTheme()
            val darkThemeEnabled = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> systemDark
            }

            MyApplicationTheme(darkTheme = darkThemeEnabled) {
                // Reactive Database states
                val records by viewModel.allRecords.collectAsStateWithLifecycle()
                val total by viewModel.totalCount.collectAsStateWithLifecycle()
                val scamCount by viewModel.scamCount.collectAsStateWithLifecycle()
                val safeCount by viewModel.safeCount.collectAsStateWithLifecycle()

                // Scanner States
                val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
                val analysisResult by viewModel.analysisResult.collectAsStateWithLifecycle()
                val analysisError by viewModel.analysisError.collectAsStateWithLifecycle()
                val isApiKeyAvailable by viewModel.isApiKeyAvailable.collectAsStateWithLifecycle()

                // Navigation State
                var selectedTabIdx by rememberSaveable { mutableStateOf(1) } // Default starting on Scanner tab

                val navItems = listOf(
                    NavigationItem("Metrics", Icons.Default.Star, "dashboard_tab"),
                    NavigationItem("Scanner", Icons.Default.Search, "scanner_tab"),
                    NavigationItem("Logs", Icons.Default.List, "logs_tab"),
                    NavigationItem("Academy", Icons.Default.Info, "academy_tab"),
                    NavigationItem("Admin", Icons.Default.Settings, "admin_tab")
                )

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.width(320.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(20.dp)
                            ) {
                                // Sidebar Header
                                Row(
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Shield Icon",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "SCAM SHIELD AI",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Accessibility & Theme Preferences",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                                Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(20.dp))

                                // Section Title
                                Text(
                                    text = "INTERFACE APPEARANCE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Text(
                                    text = "Optimize the application visual theme to improve readability, enhance contrast, and save battery.",
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                // Selectable Theme Modes
                                val modes = listOf(
                                    Triple("Light Appearance", "light", "Comfortable in bright surroundings"),
                                    Triple("Dark Appearance", "dark", "High contrast metallic mode"),
                                    Triple("Dynamic System", "system", "Follows Android system settings")
                                )

                                modes.forEach { (label, key, desc) ->
                                    val isSelected = themeMode == key
                                    Surface(
                                        onClick = { updateThemeMode(key) },
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.Transparent,
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .testTag("theme_mode_$key")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { updateThemeMode(key) },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = label,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 13.sp,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                                )
                                                Text(
                                                    text = desc,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Info Tip Box
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = androidx.compose.ui.Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Tips",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Scam Shield's custom dual-contrast color palettes strictly conform with Material Design 3 and WCAG AAA readability guidelines.",
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Close Button
                                Button(
                                    onClick = { scope.launch { drawerState.close() } },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("close_sidebar_button"),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Sidebar",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Close Sidebar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            CenterAlignedTopAppBar(
                                navigationIcon = {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } },
                                        modifier = Modifier.testTag("open_sidebar_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Open Theme Preferences Drawer"
                                        )
                                    }
                                },
                                title = {
                                    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                                        Text(
                                            text = "SCAM SHIELD AI",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp,
                                            letterSpacing = 1.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Multi-Channel Threat Intelligence",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.background,
                                tonalElevation = 8.dp
                            ) {
                                navItems.forEachIndexed { idx, item ->
                                    NavigationBarItem(
                                        selected = (selectedTabIdx == idx),
                                        onClick = { selectedTabIdx = idx },
                                        label = { Text(text = item.label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                                        modifier = Modifier.testTag(item.tag)
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (selectedTabIdx) {
                                0 -> DashboardScreen(
                                    records = records,
                                    total = total,
                                    scamCount = scamCount,
                                    safeCount = safeCount
                                )
                                1 -> ScannerScreen(
                                    isAnalyzing = isAnalyzing,
                                    analysisResult = analysisResult,
                                    analysisError = analysisError,
                                    isApiKeyAvailable = isApiKeyAvailable,
                                    onAnalyze = { text, channel, type, bitmap ->
                                        viewModel.runAnalysis(text, channel, type, bitmap)
                                    }
                                )
                                2 -> HistoryScreen(
                                    records = records,
                                    onDelete = { id -> viewModel.deleteRecord(id) },
                                    onSubmitFeedback = { id, correct -> viewModel.submitFeedback(id, correct) },
                                    onClearAll = { viewModel.clearHistory() },
                                    buildCsvString = { list -> viewModel.buildCsvString(list) },
                                    buildTextReport = { record -> viewModel.buildTextReport(record) }
                                )
                                3 -> KnowledgeCenterScreen()
                                4 -> AdminPanelScreen(
                                    onBulkUpload = { csvText -> viewModel.bulkUploadDataset(csvText) },
                                    onResetDb = { viewModel.clearHistory() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val tag: String
)
