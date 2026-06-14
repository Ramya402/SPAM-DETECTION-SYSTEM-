package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ScamGreen
import com.example.ui.theme.ScamRed
import com.example.ui.theme.ScamYellow
import com.example.ui.theme.WhitePure

@Composable
fun KnowledgeCenterScreen() {
    var activeTab by remember { mutableStateOf("Prevention Guide") }
    
    // Quiz State
    var currentQuestionIdx by remember { mutableStateOf(0) }
    var selectedAnswerIdx by remember { mutableStateOf<Int?>(null) }
    var quizScore by remember { mutableStateOf(0) }
    var showQuizExplanation by remember { mutableStateOf(false) }
    var quizCompleted by remember { mutableStateOf(false) }

    val quizQuestions = listOf(
        QuizQuestion(
            question = "You receive an SMS from an unknown number claiming your package delivery was suspended. It includes a link (http://post-office-delivery.net). What should you do?",
            options = listOf(
                "Click the link immediately to see which package it is.",
                "Delete the message and manually verify tracking on the official merchant's portal.",
                "Reply to the sender to verify their official identities."
            ),
            correctIdx = 1,
            explanation = "Post offices and couriers never request emergency credential updates or fee transfers on unsecure public net domains. Always access official dashboards directly."
        ),
        QuizQuestion(
            question = "A friendly trader on WhatsApp guarantees you make 300% passive returns daily putting money in decentralised digital asset portfolios. What type of scam has occurred?",
            options = listOf(
                "Phishing",
                "Job scam campaign",
                "Guaranteed Investment Scam"
            ),
            correctIdx = 2,
            explanation = "Any financial offer that claims guaranteed returns without variable risk profiles is a classic Ponzi scheme. High returns are never guaranteed."
        ),
        QuizQuestion(
            question = "An email matching your primary bank's layout states that server logs locked your account and requests you re-input your login passcode on their portal. How can you be certain it is phishing?",
            options = listOf(
                "The email body incorporates standard official graphic logos.",
                "Banks never request you reveal private logins or transaction pins in generic email lines.",
                "The message incorporates friendly congratulatory greetings."
            ),
            correctIdx = 1,
            explanation = "Legitimate financial operators never use SMS or email channels to request private login keys. If in doubt, call the official number on your physical bank card."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Headers
        Column {
            Text(
                text = "Scam Knowledge & Awareness Center",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Learn to identify spoofing campaigns and test your digital safety score.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Prevention Guide", "Risk Campaigns", "Safety Quiz").forEach { title ->
                val selected = (title == activeTab)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        .clickable { activeTab = title }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Sub Screen content
        Box(modifier = Modifier.weight(1f)) {
            when (activeTab) {
                "Prevention Guide" -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            GuideCard(
                                title = "Phishing & Spoofing",
                                body = "Attackers impersonate prominent institutions (Banks, Netflix, Postal services) using fake logos and high stakes language to steal passwords.\n\n🛡️ PREVENTION: Inspect sender headers. Never type logins on domain names that do not match the official bank portal.",
                                icon = Icons.Default.Email,
                                color = ScamRed
                            )
                        }
                        item {
                            GuideCard(
                                title = "Fake Job campaigns",
                                body = "Received lucrative job opportunities on WhatsApp promising passive yields watching video playlists, but mandating upfront collateral fee payments.\n\n🛡️ PREVENTION: No professional entity demands money deposits for training or security setups. Ignore unverified recruiters.",
                                icon = Icons.Default.List,
                                color = ScamYellow
                            )
                        }
                        item {
                            GuideCard(
                                title = "Investment & Crypto Fraud",
                                body = "Decentralized platforms guarantee huge constant daily compounding ratios with zero risk markers.\n\n🛡️ PREVENTION: Higher financial rates always equate to higher speculative risks. Do not download isolated crypto software profiles.",
                                icon = Icons.Default.Info,
                                color = ScamGreen
                            )
                        }
                    }
                }

                "Risk Campaigns" -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            WarningBanner(
                                text = "ALERTS: Cyber authorities warning of malicious package tracking SMS codes demanding payment to release deliveries.",
                                severity = "CRITICAL"
                            )
                        }
                        item {
                            WarningBanner(
                                text = "EMERGENCY: Phishing scams spoofing emergency grandparent cash calls. Scam networks mimic children's voices requesting urgent cash wires.",
                                severity = "HIGH SEVERITY"
                            )
                        }
                        item {
                            WarningBanner(
                                text = "TRENDING: Deepfake video endorsements showcasing fake banking apps and dynamic trading bots. Double check before downloading.",
                                severity = "IMPORTANT"
                            )
                        }
                    }
                }

                "Safety Quiz" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (quizCompleted) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Quiz Done",
                                    tint = ScamGreen,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Evaluation Completed!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Your Cyber Intelligence Score is: $quizScore / ${quizQuestions.size}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (quizScore == quizQuestions.size) {
                                        "Outstanding! You possess premier cybersecurity habits. Keep your systems secure."
                                    } else {
                                        "Good effort! Regularly scan unknown links with the Scam Detector system to preserve safety parameters."
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = {
                                        currentQuestionIdx = 0
                                        selectedAnswerIdx = null
                                        quizScore = 0
                                        showQuizExplanation = false
                                        quizCompleted = false
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Re-test Awareness")
                                }
                            }
                        } else {
                            val activeQ = quizQuestions[currentQuestionIdx]
                            
                            // Question header count
                            Text(
                                text = "QUESTION ${currentQuestionIdx + 1} OF ${quizQuestions.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Question Text
                            Text(
                                text = activeQ.question,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 18.sp
                            )

                            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f))

                            // Options mapping
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                activeQ.options.forEachIndexed { optIdx, option ->
                                    val isSelected = (selectedAnswerIdx == optIdx)
                                    val isOptionCorrect = (optIdx == activeQ.correctIdx)
                                    
                                    val optionBgColor = when {
                                        showQuizExplanation && isOptionCorrect -> ScamGreen.copy(alpha = 0.12f)
                                        showQuizExplanation && isSelected && !isOptionCorrect -> ScamRed.copy(alpha = 0.1f)
                                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }

                                    val optionBorderColor = when {
                                        showQuizExplanation && isOptionCorrect -> ScamGreen
                                        showQuizExplanation && isSelected && !isOptionCorrect -> ScamRed
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(optionBgColor)
                                            .border(1.dp, optionBorderColor, RoundedCornerShape(10.dp))
                                            .clickable(enabled = !showQuizExplanation) {
                                                selectedAnswerIdx = optIdx
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                showQuizExplanation && isOptionCorrect -> Icons.Default.CheckCircle
                                                showQuizExplanation && isSelected && !isOptionCorrect -> Icons.Default.Clear
                                                else -> Icons.Default.CheckCircle
                                            },
                                            contentDescription = "Option Check",
                                            tint = when {
                                                showQuizExplanation && isOptionCorrect -> ScamGreen
                                                showQuizExplanation && isSelected && !isOptionCorrect -> ScamRed
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                            },
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = option,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }

                            // Feedback Reveal Panel
                            AnimatedVisibility(
                                visible = showQuizExplanation,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "EXPLANATORY RECON:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = activeQ.explanation,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Actions
                            Button(
                                onClick = {
                                    if (selectedAnswerIdx == null) return@Button
                                    
                                    if (!showQuizExplanation) {
                                        if (selectedAnswerIdx == activeQ.correctIdx) {
                                            quizScore++
                                        }
                                        showQuizExplanation = true
                                    } else {
                                        // Proceed to next
                                        if (currentQuestionIdx + 1 < quizQuestions.size) {
                                            currentQuestionIdx++
                                            selectedAnswerIdx = null
                                            showQuizExplanation = false
                                        } else {
                                            quizCompleted = true
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = (selectedAnswerIdx != null),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (!showQuizExplanation) "Lock Answer" else if (currentQuestionIdx + 1 < quizQuestions.size) "Next question" else "Submit evaluation"
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
fun GuideCard(
    title: String,
    body: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .background(color.copy(alpha = 0.12f), CircleShape)
                    .padding(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = body,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun WarningBanner(text: String, severity: String) {
    val color = when (severity) {
        "CRITICAL" -> ScamRed
        "HIGH SEVERITY" -> ScamYellow
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .background(color, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = severity,
                color = WhitePure,
                fontWeight = FontWeight.Black,
                fontSize = 8.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIdx: Int,
    val explanation: String
)
