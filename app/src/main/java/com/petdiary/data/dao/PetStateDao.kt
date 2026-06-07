package com.petdiary.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.petdiary.data.entity.PetState
import kotlinx.coroutines.flow.Flow

@Dao
interface PetStateDao {
    @Query("SELECT * FROM pet_state WHERE id = 1")
    fun getPetState(): Flow<PetState?>

    @Query("SELECT * FROM pet_state WHERE id = 1")
    suspend fun getPetStateRaw(): PetState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(pet: PetState)

    @Update
    suspend fun update(pet: PetState)
}
