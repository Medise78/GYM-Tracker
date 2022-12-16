package com.medise.bashga.presentation.date_pay_expired

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medise.bashga.data.repository.GymRepository
import com.medise.bashga.domain.model.PersonEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DatePayExpiredViewModel @Inject constructor(
    private val repository: GymRepository
) : ViewModel() {

    private val _state = mutableStateOf<List<PersonEntity>>(emptyList())
    val state: State<List<PersonEntity>> get() = _state

    init {
        fetch()
    }

    fun fetch() = viewModelScope.launch {
        repository.getAllPerson().onEach {
            _state.value = it
        }.launchIn(viewModelScope)
    }

    fun updatePerson(personEntity: PersonEntity) = viewModelScope.launch {
        repository.updatePerson(personEntity)
    }

    fun deletePerson(personEntity: PersonEntity) = viewModelScope.launch {
        repository.deletePerson(personEntity)
    }
}