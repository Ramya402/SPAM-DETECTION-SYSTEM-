package com.example.data.repository

import com.example.data.model.ScamRecord
import com.example.data.local.ScamDao
import kotlinx.coroutines.flow.Flow

class ScamRepository(private val scamDao: ScamDao) {

    val allRecords: Flow<List<ScamRecord>> = scamDao.getAllRecords()
    val totalCount: Flow<Int> = scamDao.getCountFlow()
    val scamCount: Flow<Int> = scamDao.getScamCountFlow()
    val safeCount: Flow<Int> = scamDao.getSafeCountFlow()

    suspend fun insertRecord(record: ScamRecord): Long {
        return scamDao.insertRecord(record)
    }

    suspend fun deleteRecord(id: Int) {
        scamDao.deleteRecordById(id)
    }

    suspend fun updateFeedback(id: Int, feedback: String) {
        scamDao.updateFeedback(id, feedback)
    }

    suspend fun clearHistory() {
        scamDao.clearAllRecords()
    }
}
