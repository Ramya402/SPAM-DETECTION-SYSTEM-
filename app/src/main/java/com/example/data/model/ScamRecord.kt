package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scam_records")
data class ScamRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val channel: String,         // Email, SMS, WhatsApp, etc.
    val inputType: String,       // Text, Screenshot OCR, CSV File, Voice, etc.
    val status: String,          // SAFE, SCAM
    val category: String,        // Phishing, Job Scam, Financial Fraud, Reward Scam, Promotion Scam, etc.
    val confidence: Int,         // Confidence Score (e.g. 95)
    val riskLevel: String,       // GREEN (Safe), YELLOW (Suspicious), RED (High Risk)
    val explanation: String,     // JSON or general text of Explainable AI indicators + recommendations
    val timestamp: Long = System.currentTimeMillis(),
    val userFeedback: String? = null // "Correct", "False Positive", "False Negative" etc.
)
