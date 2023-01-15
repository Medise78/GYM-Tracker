package com.medise.bashga.presentation.half_price_screen

import android.util.Log
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
class HalfPriceViewModel @Inject constructor(
    private val repository: GymRepository
) : ViewModel() {

    private val _state = mutableStateOf<List<PersonEntity>>(emptyList())
    val state: State<List<PersonEntity>> get() = _state

    init {
        fetchData()
    }

    fun fetch() {
        fetchData()
    }

    private fun fetchData() = viewModelScope.launch {
        try {
            repository.getAllPerson().onEach {
                _state.value = it
            }.launchIn(viewModelScope)
        } catch (e: Exception) {
            Log.e("half_price", e.message.toString())
        }
    }

    fun updatePerson(personEntity: PersonEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.updatePerson(personEntity)
    }

    fun deletePerson(personEntity: PersonEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deletePerson(personEntity)
    }
}