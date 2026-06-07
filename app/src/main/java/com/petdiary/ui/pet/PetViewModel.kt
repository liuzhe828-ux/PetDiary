package com.petdiary.ui.pet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.petdiary.PetDiaryApplication
import com.petdiary.data.entity.PetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PetViewModel(application: Application) : AndroidViewModel(application) {
    private val petRepository = (application as PetDiaryApplication).petRepository

    private val _petState = MutableStateFlow<PetState>(PetState())
    val petState: StateFlow<PetState> = _petState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        viewModelScope.launch {
            petRepository.getPetState().collect { pet ->
                if (pet != null) {
                    _petState.value = petRepository.tickDecay(pet)
                } else {
                    petRepository.savePetState(PetState())
                    _petState.value = PetState()
                }
            }
        }
    }

    fun feed() {
        viewModelScope.launch {
            val updated = petRepository.feedPet(_petState.value)
            _petState.value = updated
            _message.value = "${updated.name}吃得很开心！😋"
            clearMessageAfterDelay()
        }
    }

    fun play() {
        viewModelScope.launch {
            val updated = petRepository.playWithPet(_petState.value)
            _petState.value = updated
            _message.value = "${updated.name}玩得真高兴！🎉"
            clearMessageAfterDelay()
        }
    }

    fun changeName(newName: String) {
        viewModelScope.launch {
            val updated = _petState.value.copy(name = newName)
            petRepository.savePetState(updated)
            _petState.value = updated
            _message.value = "你好，$newName！"
            clearMessageAfterDelay()
        }
    }

    fun changeType(type: String) {
        viewModelScope.launch {
            val updated = _petState.value.copy(petType = type)
            petRepository.savePetState(updated)
            _petState.value = updated
            _message.value = "新宠物来了！"
            clearMessageAfterDelay()
        }
    }

    private fun clearMessageAfterDelay() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _message.value = null
        }
    }
}
