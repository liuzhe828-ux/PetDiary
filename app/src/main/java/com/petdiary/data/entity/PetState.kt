package com.petdiary.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pet_state")
data class PetState(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "小团子",
    val hunger: Int = 100,      // 饱腹度 0-100
    val happiness: Int = 100,    // 快乐度 0-100
    val lastFedTime: Long = System.currentTimeMillis(),
    val lastPlayedTime: Long = System.currentTimeMillis(),
    val level: Int = 1,
    val exp: Int = 0,
    val petType: String = "cat"  // cat, dog, bunny
)
