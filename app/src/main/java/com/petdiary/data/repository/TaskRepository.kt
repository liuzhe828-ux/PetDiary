package com.petdiary.data.repository

import com.petdiary.data.dao.TaskDao
import com.petdiary.data.entity.TaskItem
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<TaskItem>> = taskDao.getAllTasks()

    fun getPendingTasks(): Flow<List<TaskItem>> = taskDao.getPendingTasks()

    fun getCompletedTasks(): Flow<List<TaskItem>> = taskDao.getCompletedTasks()

    suspend fun getTaskById(id: Long): TaskItem? = taskDao.getTaskById(id)

    suspend fun getTasksWithReminders(): List<TaskItem> = taskDao.getTasksWithReminders()

    suspend fun insert(task: TaskItem): Long = taskDao.insert(task)

    suspend fun update(task: TaskItem) = taskDao.update(task)

    suspend fun delete(task: TaskItem) = taskDao.delete(task)

    suspend fun deleteById(id: Long) = taskDao.deleteById(id)

    suspend fun toggleComplete(task: TaskItem) {
        val updated = if (task.isCompleted) {
            task.copy(isCompleted = false, completedAt = null)
        } else {
            task.copy(isCompleted = true, completedAt = System.currentTimeMillis())
        }
        taskDao.update(updated)
    }
}
