package com.example.myapplication.repository

import android.content.Context
import com.example.myapplication.data.ChallengeDB
import com.example.myapplication.data.ChallengeDao
import com.example.myapplication.model.Challenge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChallengeRepository(val context: Context){
    private var challengeDao: ChallengeDao = ChallengeDB.getDatabase(context).challengeDao()
    suspend fun saveChallenge(challenge: Challenge, messageResponse: (String) -> Unit) {
        try {
            withContext(Dispatchers.IO) {
                challengeDao.saveChallenge(challenge)
            }
            messageResponse("Reto guardado correctamente")
        } catch (e: Exception) {
            messageResponse("Error al guardar el reto: ${e.message}")
        }
    }

    suspend fun getListChallenge(): MutableList<Challenge>{
        return withContext(Dispatchers.IO){
            challengeDao.getListChallenge()
        }
    }
    suspend fun deleteChallenge(challenge: Challenge){
        withContext(Dispatchers.IO){
            challengeDao.deleteChallenge(challenge)
        }
    }

    suspend fun updateRepository(challenge: Challenge){
        withContext(Dispatchers.IO){
            challengeDao.updateChallenge(challenge)
        }
    }
}