package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.model.Challenge

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChallenge(challenge: Challenge)

    @Query("SELECT * FROM Challenge ORDER BY id DESC")
    suspend fun getListChallenge(): MutableList<Challenge>

    @Delete
    suspend fun deleteChallenge(challenge: Challenge)
    @Update
    suspend fun updateChallenge(challenge: Challenge)
}