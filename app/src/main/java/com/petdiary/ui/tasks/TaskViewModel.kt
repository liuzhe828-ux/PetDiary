package com.petdiary.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.petdiary.PetDiaryApplication
import com.petdiary.data.TaskData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val store = (application as PetDiaryApplication).dataStore
    private val scheduler = (application as PetDiaryApplication).taskScheduler

    private val _tasks = MutableStateFlow(store.getAllTasks())
    val tasks = _tasks.asStateFlow()

    private val _currentTask = MutableStateFlow<TaskData?>(null)
    val currentTask = _currentTask.asStateFlow()

    private val _showCompleted = MutableStateFlow(false)
    val showCompleted = _showCompleted.asStateFlow()

    fun loadTask(id: Long) {
        _currentTask.value = store.getAllTasks().find { it.id == id }
    }

    fun saveTask(title: String, description: String, priority: Int, dueDate: Long?, reminderTime: Long?) {
        val current = _currentTask.value
        val task = if (current != null) {
            current.copy(title = title, description = description, priority = priority, dueDate = dueDate, reminderTime = reminderTime)
        } else {
            TaskData(title = title, description = description, priority = priority, dueDate = dueDate, reminderTime = reminderTime)
        }
        val id = store.saveTask(task)
        if (reminderTime != null) scheduler.scheduleReminder(task.copy(id = id))
        _tasks.value = store.getAllTasks()
        _currentTask.value = null
    }

    fun toggleComplete(task: TaskData) {
        val updated = if (task.isCompleted) task.copy(isCompleted = false, completedAt = null)
        else task.copy(isCompleted = true, completedAt = System.currentTimeMillis())
        store.saveTask(updated)
        if (updated.isCompleted) scheduler.cancelReminder(task)
        _tasks.value = store.getAllTasks()
    }

    fun deleteTask(id: Long) {
        store.deleteTask(id)
        _tasks.value = store.getAllTasks()
    }

    fun toggleShowCompleted() { _showCompleted.value = !_showCompleted.value }
    fun clearCurrent() { _currentTask.value = null }
}
