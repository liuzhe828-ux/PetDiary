package com.petdiary.data.repository

import com.petdiary.data.dao.PetStateDao
import com.petdiary.data.entity.PetState
import kotlinx.coroutines.flow.Flow

class PetRepository(private val petStateDao: PetStateDao) {

    fun getPetState(): Flow<PetState?> = petStateDao.getPetState()

    suspend fun savePetState(pet: PetState) {
        petStateDao.insertOrUpdate(pet)
    }

    suspend fun updatePetState(pet: PetState) {
        petStateDao.update(pet)
    }

    suspend fun feedPet(currentPet: PetState): PetState {
        val newHunger = (currentPet.hunger + 30).coerceAtMost(100)
        val newExp = currentPet.exp + 10
        val newLevel = if (newExp >= currentPet.level * 100) currentPet.level + 1 else currentPet.level
        val updated = currentPet.copy(
            hunger = newHunger,
            exp = newExp % (newLevel * 100),
            level = newLevel,
            lastFedTime = System.currentTimeMillis()
        )
        petStateDao.update(updated)
        return updated
    }

    suspend fun playWithPet(currentPet: PetState): PetState {
        val newHappiness = (currentPet.happiness + 30).coerceAtMost(100)
        val newExp = currentPet.exp + 15
        val newLevel = if (newExp >= currentPet.level * 100) currentPet.level + 1 else currentPet.level
        val updated = currentPet.copy(
            happiness = newHappiness,
            exp = newExp % (newLevel * 100),
            level = newLevel,
            lastPlayedTime = System.currentTimeMillis()
        )
        petStateDao.update(updated)
        return updated
    }

    suspend fun tickDecay(currentPet: PetState): PetState {
        val now = System.currentTimeMillis()
        val hoursSinceFed = (now - currentPet.lastFedTime) / (1000 * 60 * 60)
        val hoursSincePlayed = (now - currentPet.lastPlayedTime) / (1000 * 60 * 60)

        val newHunger = (currentPet.hunger - hoursSinceFed * 5).coerceIn(0, 100)
        val newHappiness = (currentPet.happiness - hoursSincePlayed * 5).coerceIn(0, 100)

        val updated = currentPet.copy(
            hunger = newHunger.toInt(),
            happiness = newHappiness.toInt()
        )
        petStateDao.update(updated)
        return updated
    }
}
