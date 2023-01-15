package com.medise.bashga.presentation.add_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medise.bashga.data.repository.GymRepository
import com.medise.bashga.domain.model.PersonEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddScreenViewModel @Inject constructor(
    private val repository: GymRepository
) :ViewModel(){

    val fName = MutableLiveData("")
    val lName = MutableLiveData("")
    val checkState = mutableStateOf(false)

    private val _sharedFlow = MutableSharedFlow<UiEvent>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun addPerson(personEntity: PersonEntity){
        viewModelScope.launch {
            repository.insertPerson(personEntity)
            _sharedFlow.emit(UiEvent.SavedNote)
        }
    }
    sealed class UiEvent {
        data class SnackBar(val message: String) : UiEvent()
        object SavedNote : UiEvent()
    }
}

