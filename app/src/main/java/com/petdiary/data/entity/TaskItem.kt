package com.petdiary.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: Int = 1,        // 1=低, 2=中, 3=高
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,    // 截止日期时间戳
    val reminderTime: Long? = null, // 提醒时间戳
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
