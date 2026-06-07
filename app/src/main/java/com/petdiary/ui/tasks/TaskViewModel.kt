package com.petdiary.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.petdiary.PetDiaryApplication
import com.petdiary.data.entity.TaskItem
import com.petdiary.worker.TaskReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskRepository = (application as PetDiaryApplication).taskRepository
    private val reminderScheduler = TaskReminderScheduler(application)

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _currentTask = MutableStateFlow<TaskItem?>(null)
    val currentTask: StateFlow<TaskItem?> = _currentTask.asStateFlow()

    private val _showCompleted = MutableStateFlow(false)
    val showCompleted: StateFlow<Boolean> = _showCompleted.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            taskRepository.getAllTasks().collect {
                _tasks.value = it
            }
        }
    }

    fun loadTask(id: Long) {
        viewModelScope.launch {
            _currentTask.value = taskRepository.getTaskById(id)
        }
    }

    fun saveTask(
        title: String,
        description: String,
        priority: Int,
        dueDate: Long?,
        reminderTime: Long?
    ) {
        viewModelScope.launch {
            val current = _currentTask.value
            if (current != null) {
                val updated = current.copy(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate,
                    reminderTime = reminderTime
                )
                taskRepository.update(updated)
                if (reminderTime != null) {
                    reminderScheduler.scheduleReminder(updated)
                }
            } else {
                val task = TaskItem(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate,
                    reminderTime = reminderTime
                )
                val id = taskRepository.insert(task)
                if (reminderTime != null) {
                    reminderScheduler.scheduleReminder(task.copy(id = id))
                }
            }
            _currentTask.value = null
        }
    }

    fun toggleComplete(task: TaskItem) {
        viewModelScope.launch {
            taskRepository.toggleComplete(task)
            // 取消提醒
            if (task.isCompleted) {
                reminderScheduler.cancelReminder(task)
            }
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            taskRepository.deleteById(id)
        }
    }

    fun toggleShowCompleted() {
        _showCompleted.value = !_showCompleted.value
    }

    fun clearCurrentTask() {
        _currentTask.value = null
    }
}
