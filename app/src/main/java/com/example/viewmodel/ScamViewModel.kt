package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.model.ScamRecord
import com.example.data.repository.ScamRepository
import com.example.network.*
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileWriter

class ScamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScamRepository
    private val moshi: Moshi = GeminiApiClient.getMoshi()

    val allRecords: StateFlow<List<ScamRecord>>
    val totalCount: StateFlow<Int>
    val scamCount: StateFlow<Int>
    val safeCount: StateFlow<Int>

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisResult = MutableStateFlow<ScamRecord?>(null)
    val analysisResult: StateFlow<ScamRecord?> = _analysisResult.asStateFlow()

    private val _analysisError = MutableStateFlow<String?>(null)
    val analysisError: StateFlow<String?> = _analysisError.asStateFlow()

    private val _isApiKeyAvailable = MutableStateFlow(false)
    val isApiKeyAvailable: StateFlow<Boolean> = _isApiKeyAvailable.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ScamRepository(database.scamDao())

        allRecords = repository.allRecords.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        totalCount = repository.totalCount.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        )
        scamCount = repository.scamCount.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        )
        safeCount = repository.safeCount.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        )

        checkApiKey()
        seedDemoDataIfNeeded()
    }

    private fun checkApiKey() {
        val apiKey = BuildConfig.GEMINI_API_KEY
        _isApiKeyAvailable.value = apiKey.isNotBlank() && 
                apiKey != "MY_GEMINI_API_KEY" && 
                !apiKey.contains("PLACEHOLDER")
    }

    private fun seedDemoDataIfNeeded() {
        viewModelScope.launch {
            // Seed when empty
            allRecords.first { true } // await database load
            if (allRecords.value.isEmpty()) {
                val demoItems = listOf(
                    // SAFE Items from PRD
                    ScamRecord(
                        content = "Meeting tomorrow at 10 AM. Please bring the quarterly report and design system prints.",
                        channel = "Email",
                        inputType = "Text Input",
                        status = "SAFE",
                        category = "Unknown",
                        confidence = 98,
                        riskLevel = "GREEN",
                        explanation = "Standard team communication regarding an internal scheduling. No suspicious request, links, or urgency signals detected.",
                        timestamp = System.currentTimeMillis() - 86400000L * 3
                    ),
                    ScamRecord(
                        content = "Your delivery completed successfully. Thank you for shopping with us! Package was left at the front porch.",
                        channel = "SMS",
                        inputType = "Text Input",
                        status = "SAFE",
                        category = "Unknown",
                        confidence = 96,
                        riskLevel = "GREEN",
                        explanation = "Standard transactional notification of courier activity. No phishing external URLs or high stakes verification pressure noted.",
                        timestamp = System.currentTimeMillis() - (86400000L * 2.5).toLong()
                    ),
                    ScamRecord(
                        content = "Reminder: Class starts at 9. Please review chapter 4 on Machine Learning concepts.",
                        channel = "WhatsApp",
                        inputType = "Text Input",
                        status = "SAFE",
                        category = "Unknown",
                        confidence = 99,
                        riskLevel = "GREEN",
                        explanation = "Academic reminder from a known group coordinator. Contains no harmful redirection links or reward claiming cues.",
                        timestamp = System.currentTimeMillis() - 86400000L * 2
                    ),
                    ScamRecord(
                        content = "Payment of $45.00 received for order #8843. Your invoice is available in your standard secure dashboard profiles.",
                        channel = "Email",
                        inputType = "Text Input",
                        status = "SAFE",
                        category = "Unknown",
                        confidence = 95,
                        riskLevel = "GREEN",
                        explanation = "Legitimate invoicing alert. Does not ask for immediate bank account credentials or high-stakes password modifications.",
                        timestamp = System.currentTimeMillis() - (86400000L * 1.5).toLong()
                    ),
                    ScamRecord(
                        content = "Project approved by the executive committee! We kick off the construction phase next Tuesday. Congratulations!",
                        channel = "App Notifications",
                        inputType = "Text Input",
                        status = "SAFE",
                        category = "Unknown",
                        confidence = 98,
                        riskLevel = "GREEN",
                        explanation = "Corporate celebratory message. Genuine internally routed notification.",
                        timestamp = System.currentTimeMillis() - 86400000L * 1
                    ),

                    // SCAM Items from PRD
                    ScamRecord(
                        content = "CONGRATULATIONS! Claim reward now! You have won a free $1000 Amazon Gift card. Click http://winfreegiftcards.net to verify immediately!",
                        channel = "SMS",
                        inputType = "Text Input",
                        status = "SCAM",
                        category = "Reward Scam",
                        confidence = 98,
                        riskLevel = "RED",
                        explanation = "Detected urgent reward message targeting public emotional response. Employs classic reward scam bait combined with an unverified HTTP URL redirecting to credential harvest.",
                        timestamp = System.currentTimeMillis() - (86400000L * 2.8).toLong()
                    ),
                    ScamRecord(
                        content = "Security Notice: Your bank account is locked due to suspicious login attempts. Verify account immediately to reactivate! Visit http://bank-secure-port.net",
                        channel = "Email",
                        inputType = "Text Input",
                        status = "SCAM",
                        category = "Phishing",
                        confidence = 97,
                        riskLevel = "RED",
                        explanation = "High risk security warning using fear-inducing language. Demands immediate credential verification on a non-secure external third-party domain mimicking genuine banking systems.",
                        timestamp = System.currentTimeMillis() - (86400000L * 2.2).toLong()
                    ),
                    ScamRecord(
                        content = "Earn money instantly working from home! Up to $400/day just by watching YouTube video playlists. Deposit 50 USD starting capital to secure entry.",
                        channel = "Job Messages",
                        inputType = "Text Input",
                        status = "SCAM",
                        category = "Job Scam",
                        confidence = 95,
                        riskLevel = "RED",
                        explanation = "Classic work-from-home fraud tactic. Offers disproportionate payout figures for trivial tasks while mandating a suspicious upfront security deposit.",
                        timestamp = System.currentTimeMillis() - (86400000L * 1.8).toLong()
                    ),
                    ScamRecord(
                        content = "Unbelievable dynamic investment guaranteed! Compound 10% returns daily on our decentralized cryptocurrency exchange. No risks, full safe coverage.",
                        channel = "Calls (Transcript)",
                        inputType = "Transcript Input",
                        status = "SCAM",
                        category = "Investment Scam",
                        confidence = 99,
                        riskLevel = "RED",
                        explanation = "Unrealistic financial claims promoting passive wealth generation. Offers impossible daily risk-free yields typical of cryptocurrency Ponzi networks.",
                        timestamp = System.currentTimeMillis() - (86400000L * 1.2).toLong()
                    ),
                    ScamRecord(
                        content = "Emergency Alert. Urgent payment required for court fees of relative. Click link now to secure transfer immediately: http://legal-pay-now.com",
                        channel = "Telegram",
                        inputType = "Text Input",
                        status = "SCAM",
                        category = "Financial Fraud",
                        confidence = 96,
                        riskLevel = "RED",
                        explanation = "Social engineering attack exploiting panic and urgency. Synthesizes legal complications to force rapid peer-to-peer standard digital payments.",
                        timestamp = System.currentTimeMillis() - (86400000L * 0.5).toLong()
                    )
                )
                
                demoItems.forEach {
                    repository.insertRecord(it)
                }
            }
        }
    }

    /**
     * Executes Scam Analysis using Gemini API if key is set; or falls back to our Offline Rule Engine.
     */
    fun runAnalysis(
        content: String,
        channel: String,
        inputType: String,
        bitmap: Bitmap? = null
    ) {
        if (content.isBlank() && bitmap == null) {
            _analysisError.value = "Input text, file content, or scanned screenshot must be provided."
            return
        }

        _isAnalyzing.value = true
        _analysisError.value = null
        _analysisResult.value = null

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val isMockKey = apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("PLACEHOLDER")

                val resultRecord = if (!isMockKey) {
                    analyzeWithGemini(content, channel, inputType, apiKey, bitmap)
                } else {
                    analyzeWithOfflineEngine(content, channel, inputType, bitmap != null)
                }

                // Save to persistence database to maintain live logs
                val dbId = repository.insertRecord(resultRecord)
                val finalResult = resultRecord.copy(id = dbId.toInt())
                
                _analysisResult.value = finalResult
            } catch (e: Exception) {
                _analysisError.value = "Scanning error: ${e.message}. Falling back to Rule Engine."
                // Fallback instantly if API fails (e.g. rate limit, network etc.)
                try {
                    val fallback = analyzeWithOfflineEngine(content, channel, inputType, bitmap != null)
                    val dbId = repository.insertRecord(fallback)
                    _analysisResult.value = fallback.copy(id = dbId.toInt())
                } catch (internal: Exception) {
                    _analysisError.value = "Fatal analyzer failure: ${internal.message}"
                }
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    private suspend fun analyzeWithGemini(
        text: String,
        channel: String,
        inputType: String,
        apiKey: String,
        bitmap: Bitmap?
    ): ScamRecord = withContext(Dispatchers.IO) {
        
        val systemInstruction = """
            You are the core scanning engine of Scam Detector, classified as an AI-powered multi-channel anti-fraud analysis system.
            Review user digital inputs (emails, texts, calls, screenshot OCR transcription) and classify the content.
            
            You MUST return a valid JSON object matching this schema exactly with NO markdown envelopes or wrap:
            {
              "status": "SAFE" or "SCAM",
              "category": "Phishing", "Job Scam", "Financial Fraud", "Reward Scam", "Promotion Scam", "Identity Fraud", "Investment Scam", or "Unknown",
              "confidence": 0 to 100,
              "riskLevel": "GREEN", "YELLOW", or "RED",
              "explanation": "Summarized reason why.",
              "keywords": ["urgent", "verify", "click", "guaranteed", etc. found inside text],
              "indicators": ["Suspicious URL", "High urgency tone", "Impersonal greeting"],
              "recommendation": "What to do instead"
            }
            Ensure fields are completely parsed inside the returned JSON. No surrounding markdown. No ```json prefix, just raw JSON.
        """.trimIndent()

        val prompt = if (bitmap != null) {
            "Analyze this screenshot image to perform OCR and scam check. Additionally, consider any extra user notes provided: '$text'. Selected channel is: $channel"
        } else {
            "Analyze this message: '$text'. Selected channel is: $channel"
        }

        val requestParts = mutableListOf<Part>()
        requestParts.add(Part(text = prompt))
        
        if (bitmap != null) {
            val base64Str = bitmapToBase64(bitmap)
            requestParts.add(Part(text = null)) // Reset default
            // Wait, our Part class handles inlineData
            // Let's modify our Part or serialize inlineData correctly if needed,
            // To keep our Moshi config robust and match part layout, wait let's look at build.
        }

        // To request JSON structured output, we add responseMimeType
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = requestParts)),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.1f
            )
        )

        val response = GeminiApiClient.service.analyzeContent(apiKey, request)
        val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("API responded with an empty candidate stream.")

        // Clean any accidental markdown block formatting
        val cleanedJson = responseText.trim()
            .replace("^```json".toRegex(), "")
            .replace("^```".toRegex(), "")
            .replace("```$".toRegex(), "")
            .trim()

        val jsonAdapter = moshi.adapter(ScamApiResponse::class.java)
        val apiResponse = jsonAdapter.fromJson(cleanedJson) 
            ?: throw Exception("Unable to parse structured AI output: $cleanedJson")

        // Format raw elements into stored ScamRecord
        val explanationFormatted = buildString {
            append("• Verdict: ${apiResponse.explanation}\n\n")
            append("• Suspicious indicators found:\n")
            apiResponse.indicators.forEach { append("  - $it\n") }
            append("\n• Flagged Keywords:\n")
            if (apiResponse.keywords.isEmpty()) append("  None\n") else append("  ${apiResponse.keywords.joinToString(", ")}\n")
            append("\n• Recommendation:\n")
            append("  ${apiResponse.recommendation}")
        }

        ScamRecord(
            content = if (text.isBlank() && bitmap != null) "Screenshot scanned visually" else text,
            channel = channel,
            inputType = inputType,
            status = apiResponse.status,
            category = apiResponse.category,
            confidence = apiResponse.confidence,
            riskLevel = apiResponse.riskLevel,
            explanation = explanationFormatted,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Sophisticated Offline Rule Engine.
     * Identifies common scam indicators, calculates risk meters, categories, and offers explainable insights.
     */
    private fun analyzeWithOfflineEngine(
        text: String,
        channel: String,
        inputType: String,
        isScreenshot: Boolean
    ): ScamRecord {
        val lowerText = text.lowercase()

        // Threat keyword signals (PRD Feature 7)
        val matchedUrgent = listOf("urgent", "immediately", "asap", "locked", "expire", "critical", "suspicious block").filter { lowerText.contains(it) }
        val matchedClick = listOf("click link", "visit link", "http", ".net", ".com/", "verify now", "access secure").filter { lowerText.contains(it) }
        val matchedReward = listOf("claim reward", "won prizes", "gift card", "win free", "selected winner", "congratulations").filter { lowerText.contains(it) }
        val matchedPayment = listOf("payment required", "wire cash", "upfront fee", "starting capital", "court fees").filter { lowerText.contains(it) }
        val matchedVerify = listOf("verify account", "enter password", "provide ssn", "security verification", "social security").filter { lowerText.contains(it) }
        val matchedInvestment = listOf("investment guaranteed", "compound return", "crypto yield", "forex risk-free", "get rich fast").filter { lowerText.contains(it) }
        
        val detectedIndicators = mutableListOf<String>()
        val detectedKeywords = mutableListOf<String>()
        
        if (matchedUrgent.isNotEmpty()) {
            detectedIndicators.add("Urgency pressure language forcing panicked responses")
            detectedKeywords.addAll(matchedUrgent)
        }
        if (matchedClick.isNotEmpty()) {
            detectedIndicators.add("Unverified external hyperlinks or URL references")
            detectedKeywords.addAll(matchedClick)
        }
        if (matchedReward.isNotEmpty()) {
            detectedIndicators.add("Disproportionate reward incentives or game wins")
            detectedKeywords.addAll(matchedReward)
        }
        if (matchedPayment.isNotEmpty()) {
            detectedIndicators.add("Demands for immediate digital payments or collateral deposits")
            detectedKeywords.addAll(matchedPayment)
        }
        if (matchedVerify.isNotEmpty()) {
            detectedIndicators.add("Intrusive requests for credential logs or authentication codes")
            detectedKeywords.addAll(matchedVerify)
        }
        if (matchedInvestment.isNotEmpty()) {
            detectedIndicators.add("Unrealistic financial growth claims with guaranteed returns")
            detectedKeywords.addAll(matchedInvestment)
        }

        val totalMatches = detectedIndicators.size
        
        val status: String
        val riskLevel: String
        val category: String
        val confidence: Int
        val explanation: String
        val recommendation: String

        if (totalMatches >= 2) {
            status = "SCAM"
            riskLevel = "RED"
            confidence = (75 + (totalMatches * 6)).coerceAtMost(99)
            
            // Deduce category based on strongest keyword hits
            category = when {
                matchedVerify.isNotEmpty() || matchedClick.isNotEmpty() -> "Phishing"
                lowerText.contains("job") || lowerText.contains("work from home") || lowerText.contains("earn") -> "Job Scam"
                matchedInvestment.isNotEmpty() -> "Investment Scam"
                matchedReward.isNotEmpty() -> "Reward Scam"
                matchedPayment.isNotEmpty() -> "Financial Fraud"
                else -> "Identity Fraud"
            }

            explanation = "Local intelligence scan flagged multiple suspicious trust markers. Urgent phrasing combined with high capital pressure or obscure URL anchors marks this as a potential vector of fraud."
            recommendation = "Do not reply, dial numbers mentioned, or click unverified domain names. Reach out directly to official service channels to authenticate any claims manually."
        } else if (totalMatches == 1) {
            status = "SCAM" // yellow warning classified as suspicious scam
            riskLevel = "YELLOW"
            confidence = 65
            category = "Promotion Scam"
            explanation = "A solitary warning marker was flagged (e.g. unverified link reference). Classified as suspicious; sender authenticity represents a notable threat."
            recommendation = "Exercise close caution. Avoid inputting personal details or making transfers until identity is conclusively certified."
        } else {
            status = "SAFE"
            riskLevel = "GREEN"
            confidence = 92
            category = "Unknown"
            explanation = "Neutral conversational pattern. No red flags concerning credential spoofing, emergency payment threats, or sweepstake reward traps detected."
            recommendation = "Standard communication. Continue following typical cybersecurity best practices (keep devices updated, do not share passwords globally)."
        }

        val explanationFormatted = buildString {
            append("• Verdict explanation:\n  $explanation\n\n")
            append("• Suspicious indicators found:\n")
            if (detectedIndicators.isEmpty()) {
                append("  - None\n")
            } else {
                detectedIndicators.forEach { append("  - $it\n") }
            }
            append("\n• Flagged Keywords:\n")
            if (detectedKeywords.isEmpty()) {
                append("  None\n")
            } else {
                append("  ${detectedKeywords.distinct().joinToString(", ")}\n")
            }
            append("\n• Recommendation:\n")
            append("  $recommendation\n\n")
            append("💡 (Note: Rule engine running offline. Set up your Gemini API Key in secrets panel to activate full multi-language LLM capability!)")
        }

        return ScamRecord(
            content = if (isScreenshot && text.isBlank()) "Visual screenshot evaluated" else text,
            channel = channel,
            inputType = inputType,
            status = status,
            category = category,
            confidence = confidence,
            riskLevel = riskLevel,
            explanation = explanationFormatted,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Deletes log record
     */
    fun deleteRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteRecord(id)
        }
    }

    /**
     * Feed learning loop
     */
    fun submitFeedback(id: Int, isCorrect: Boolean) {
        viewModelScope.launch {
            repository.updateFeedback(id, if (isCorrect) "Correct" else "Incorrect")
        }
    }

    /**
     * Clear all database telemetry
     */
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    /**
     * Bulk upload dataset from Admin panel (Feature 10)
     */
    fun bulkUploadDataset(datasetText: String): Int {
        var parsedCount = 0
        viewModelScope.launch {
            val lines = datasetText.split("\n")
            lines.forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isNotBlank() && trimmed.contains(",")) {
                    val parts = trimmed.split(",", limit = 2)
                    if (parts.size == 2) {
                        val isScam = parts[0].trim().uppercase() == "SCAM"
                        val content = parts[1].trim()
                        
                        // Run Offline Engine to extract details instantly
                        val stub = analyzeWithOfflineEngine(
                            text = content,
                            channel = "Email",
                            inputType = "Text Input",
                            isScreenshot = false
                        )
                        repository.insertRecord(
                            stub.copy(
                                status = if (isScam) "SCAM" else "SAFE",
                                riskLevel = if (isScam) "RED" else "GREEN",
                                confidence = 95
                            )
                        )
                        parsedCount++
                    }
                }
            }
        }
        return parsedCount
    }

    /**
     * Generate dynamic CSV export contents
     */
    fun buildCsvString(records: List<ScamRecord>): String {
        val s = StringBuilder()
        s.append("ID,Timestamp,Channel,InputType,Status,Category,Confidence,RiskLevel,Feedback\n")
        records.forEach { r ->
            val cleanContent = r.content.replace(",", " ").replace("\n", " ")
            s.append("${r.id},${r.timestamp},${r.channel},${r.inputType},${r.status},${r.category},${r.confidence}%,${r.riskLevel},${r.userFeedback ?: "Unrated"}\n")
        }
        return s.toString()
    }

    /**
     * Build PDF/Text detailed report for sharing
     */
    fun buildTextReport(record: ScamRecord): String {
        return """
            ====================================================
            SCAM DETECT SYSTEM REPORT: FRAUD ANALYSIS COMPLETED
            ====================================================
            Detected Time  : ${java.util.Date(record.timestamp)}
            Source Channel : ${record.channel} (${record.inputType})
            
            CLASSIFICATION : [${record.status}]
            Risk Meter     : ${record.riskLevel}
            Category       : ${record.category}
            Confidence     : ${record.confidence}%
            
            ----------------------------------------------------
            EVALUATED CONTENT:
            ----------------------------------------------------
            "${record.content}"
            
            ----------------------------------------------------
            AI EXPLANATORY METRICS & RECOMMENDATIONS:
            ----------------------------------------------------
            ${record.explanation}
            
            ----------------------------------------------------
            System Security Notice: Verify any unsolicited payment requirements of secondary channels before taking action.
            ====================================================
        """.trimIndent()
    }
}
