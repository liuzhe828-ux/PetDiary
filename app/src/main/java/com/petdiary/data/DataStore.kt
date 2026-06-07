package com.petdiary.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.ConcurrentHashMap

// ========== 数据模型 ==========
data class PetData(
    val name: String = "小团子",
    val hunger: Int = 100,
    val happiness: Int = 100,
    val lastFedTime: Long = System.currentTimeMillis(),
    val lastPlayedTime: Long = System.currentTimeMillis(),
    val level: Int = 1,
    val exp: Int = 0,
    val petType: String = "cat"
)

data class DiaryData(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val mood: String = "neutral",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class TaskData(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val priority: Int = 1,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val reminderTime: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

// ========== JSON 文件存储 ==========
class DataStore private constructor(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pet_diary_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    // === 宠物 ===
    fun getPet(): PetData {
        val json = prefs.getString("pet", null) ?: return PetData()
        return try { gson.fromJson(json, PetData::class.java) } catch (_: Exception) { PetData() }
    }

    fun savePet(pet: PetData) {
        prefs.edit().putString("pet", gson.toJson(pet)).apply()
    }

    // === 日记 ===
    fun getAllDiaries(): List<DiaryData> {
        val json = prefs.getString("diaries", "[]") ?: "[]"
        return try {
            val type = object : TypeToken<List<DiaryData>>() {}.type
            gson.fromJson(json, type)
        } catch (_: Exception) { emptyList() }
    }

    fun saveDiary(entry: DiaryData): Long {
        val list = getAllDiaries().toMutableList()
        val id = if (entry.id == 0L) (list.maxOfOrNull { it.id } ?: 0) + 1 else entry.id
        val updated = entry.copy(id = id)
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) list[idx] = updated else list.add(0, updated)
        prefs.edit().putString("diaries", gson.toJson(list)).apply()
        return id
    }

    fun deleteDiary(id: Long) {
        val list = getAllDiaries().toMutableList()
        list.removeAll { it.id == id }
        prefs.edit().putString("diaries", gson.toJson(list)).apply()
    }

    // === 任务 ===
    fun getAllTasks(): List<TaskData> {
        val json = prefs.getString("tasks", "[]") ?: "[]"
        return try {
            val type = object : TypeToken<List<TaskData>>() {}.type
            gson.fromJson(json, type)
        } catch (_: Exception) { emptyList() }
    }

    fun saveTask(task: TaskData): Long {
        val list = getAllTasks().toMutableList()
        val id = if (task.id == 0L) (list.maxOfOrNull { it.id } ?: 0) + 1 else task.id
        val updated = task.copy(id = id)
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) list[idx] = updated else list.add(updated)
        prefs.edit().putString("tasks", gson.toJson(list)).apply()
        return id
    }

    fun deleteTask(id: Long) {
        val list = getAllTasks().toMutableList()
        list.removeAll { it.id == id }
        prefs.edit().putString("tasks", gson.toJson(list)).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: DataStore? = null
        fun get(context: Context): DataStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataStore(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
