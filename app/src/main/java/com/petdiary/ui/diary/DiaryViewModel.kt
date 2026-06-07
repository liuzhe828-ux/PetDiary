package com.petdiary.ui.diary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.petdiary.PetDiaryApplication
import com.petdiary.data.entity.DiaryEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryRepository = (application as PetDiaryApplication).diaryRepository

    private val _entries = MutableStateFlow<List<DiaryEntry>>(emptyList())
    val entries: StateFlow<List<DiaryEntry>> = _entries.asStateFlow()

    private val _currentEntry = MutableStateFlow<DiaryEntry?>(null)
    val currentEntry: StateFlow<DiaryEntry?> = _currentEntry.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            diaryRepository.getAllEntries().collect {
                _entries.value = it
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                diaryRepository.getAllEntries().collect { _entries.value = it }
            } else {
                diaryRepository.searchEntries(query).collect { _entries.value = it }
            }
        }
    }

    fun loadEntry(id: Long) {
        viewModelScope.launch {
            _currentEntry.value = diaryRepository.getEntryById(id)
        }
    }

    fun saveEntry(title: String, content: String, mood: String) {
        viewModelScope.launch {
            val current = _currentEntry.value
            if (current != null) {
                val updated = current.copy(
                    title = title,
                    content = content,
                    mood = mood,
                    updatedAt = System.currentTimeMillis()
                )
                diaryRepository.update(updated)
            } else {
                val entry = DiaryEntry(
                    title = title,
                    content = content,
                    mood = mood
                )
                diaryRepository.insert(entry)
            }
            _currentEntry.value = null
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            diaryRepository.deleteById(id)
        }
    }

    fun clearCurrentEntry() {
        _currentEntry.value = null
    }
}
