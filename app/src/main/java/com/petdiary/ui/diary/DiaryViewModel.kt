package com.petdiary.ui.diary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.petdiary.PetDiaryApplication
import com.petdiary.data.DiaryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val store = (application as PetDiaryApplication).dataStore

    private val _entries = MutableStateFlow(store.getAllDiaries())
    val entries = _entries.asStateFlow()

    private val _currentEntry = MutableStateFlow<DiaryData?>(null)
    val currentEntry = _currentEntry.asStateFlow()

    fun loadEntry(id: Long) {
        _currentEntry.value = store.getAllDiaries().find { it.id == id }
    }

    fun saveEntry(title: String, content: String, mood: String) {
        val current = _currentEntry.value
        val entry = if (current != null) {
            current.copy(title = title, content = content, mood = mood, updatedAt = System.currentTimeMillis())
        } else {
            DiaryData(title = title, content = content, mood = mood)
        }
        store.saveDiary(entry)
        _entries.value = store.getAllDiaries()
        _currentEntry.value = null
    }

    fun deleteEntry(id: Long) {
        store.deleteDiary(id)
        _entries.value = store.getAllDiaries()
    }

    fun searchAll(query: String) {
        val list = store.getAllDiaries()
        _entries.value = if (query.isBlank()) list
        else list.filter { it.title.contains(query, true) || it.content.contains(query, true) }
    }

    fun clearCurrent() { _currentEntry.value = null }
}
