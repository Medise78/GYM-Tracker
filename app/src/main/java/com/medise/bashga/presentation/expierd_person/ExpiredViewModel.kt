package com.medise.bashga.presentation.expierd_person

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medise.bashga.data.repository.GymRepository
import com.medise.bashga.domain.model.PersonEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpiredViewModel @Inject constructor(
    private val repository: GymRepository
) : ViewModel() {

    private val _state = mutableStateOf<List<PersonEntity>>(emptyList())
    val state: State<List<PersonEntity>> get() = _state

    init {
        fetch()
    }

    fun fetch() = viewModelScope.launch {
        viewModelScope.launch {
            repository.getAllPerson().onEach {
                try {
                    _state.value = it
                }catch (e:Exception){
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deletePerson(personEntity: PersonEntity){
        viewModelScope.launch (Dispatchers.IO){
            repository.deletePerson(personEntity)
        }
    }

    fun updatePerson(personEntity: PersonEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePerson(personEntity)
        }
    }
}