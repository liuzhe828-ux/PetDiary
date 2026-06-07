package com.petdiary.ui.pet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.petdiary.PetDiaryApplication
import com.petdiary.data.PetData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PetViewModel(application: Application) : AndroidViewModel(application) {
    private val store = (application as PetDiaryApplication).dataStore

    private val _pet = MutableStateFlow(store.getPet())
    val pet = _pet.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun feed() {
        val current = _pet.value
        val newHunger = (current.hunger + 30).coerceAtMost(100)
        val newExp = current.exp + 10
        val newLevel = if (newExp >= current.level * 100) current.level + 1 else current.level
        val updated = current.copy(hunger = newHunger, exp = newExp % (newLevel * 100), level = newLevel, lastFedTime = System.currentTimeMillis())
        store.savePet(updated)
        _pet.value = updated
        _message.value = "${updated.name}吃得很开心！😋"
        clearMsg()
    }

    fun play() {
        val current = _pet.value
        val newHappiness = (current.happiness + 30).coerceAtMost(100)
        val newExp = current.exp + 15
        val newLevel = if (newExp >= current.level * 100) current.level + 1 else current.level
        val updated = current.copy(happiness = newHappiness, exp = newExp % (newLevel * 100), level = newLevel, lastPlayedTime = System.currentTimeMillis())
        store.savePet(updated)
        _pet.value = updated
        _message.value = "${updated.name}玩得真高兴！🎉"
        clearMsg()
    }

    fun changeName(name: String) {
        val updated = _pet.value.copy(name = name)
        store.savePet(updated)
        _pet.value = updated
        _message.value = "你好，$name！"
        clearMsg()
    }

    fun changeType(type: String) {
        val updated = _pet.value.copy(petType = type)
        store.savePet(updated)
        _pet.value = updated
        _message.value = "新宠物来了！"
        clearMsg()
    }

    private fun clearMsg() {
        viewModelScope.launch { delay(2000); _message.value = null }
    }
}
