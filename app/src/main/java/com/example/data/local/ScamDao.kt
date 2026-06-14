package com.example.data.local

import androidx.room.*
import com.example.data.model.ScamRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ScamDao {
    @Query("SELECT * FROM scam_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<ScamRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ScamRecord): Long

    @Query("DELETE FROM scam_records WHERE id = :id")
    suspend fun deleteRecordById(id: Int)

    @Query("UPDATE scam_records SET userFeedback = :feedback WHERE id = :id")
    suspend fun updateFeedback(id: Int, feedback: String)

    @Query("DELETE FROM scam_records")
    suspend fun clearAllRecords()

    @Query("SELECT COUNT(*) FROM scam_records")
    fun getCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM scam_records WHERE status = 'SCAM'")
    fun getScamCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM scam_records WHERE status = 'SAFE'")
    fun getSafeCountFlow(): Flow<Int>
}
