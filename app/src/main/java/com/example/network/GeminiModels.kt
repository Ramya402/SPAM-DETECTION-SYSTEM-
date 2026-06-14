package com.example.network

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null,
    val responseSchema: ResponseSchema? = null,
    val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class ResponseSchema(
    val type: String,
    val properties: Map<String, SchemaProperty>? = null,
    val required: List<String>? = null,
    val items: SchemaProperty? = null
)

@JsonClass(generateAdapter = true)
data class SchemaProperty(
    val type: String,
    val description: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

// The structure we enforce the Gemini model to respond in JSON mode
@JsonClass(generateAdapter = true)
data class ScamApiResponse(
    val status: String,          // "SAFE" or "SCAM"
    val category: String,        // Phishing, Job Scam, Financial Fraud, Reward Scam, Promotion Scam, Identity Fraud, Investment Scam, Unknown
    val confidence: Int,         // 0 to 100
    val riskLevel: String,       // GREEN, YELLOW, RED
    val explanation: String,     // Concise overview
    val keywords: List<String>,   // URgent, click, verify, etc.
    val indicators: List<String>, // Suspicious details
    val recommendation: String    // Actions to take
)
