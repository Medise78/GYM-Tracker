package com.medise.bashga.presentation.home_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medise.bashga.data.repository.GymRepository
import com.medise.bashga.domain.model.PersonEntity
import com.medise.bashga.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GymRepository
) : ViewModel() {
    private val _state = mutableStateOf<HomeState>(HomeState())
    val state: State<HomeState> get() = _state

    init {
        fetchAllPerson()
    }

    fun fetchAllPerson() = viewModelScope.launch {
        repository.getAllPerson().onEach {
            try {
                _state.value = HomeState(
                    success = it
                )
            } catch (e: Exception) {
                _state.value = HomeState(
                    error = e.message
                )
            }
        }.launchIn(viewModelScope)
    }

    fun deletePerson(personEntity: PersonEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePerson(personEntity)
        }
    }

    fun insertAllPersons(personEntity: PersonEntity){
        viewModelScope.launch {
            repository.insertPerson(personEntity)
        }
    }
}

data class HomeState(
    val success:List<PersonEntity> = emptyList(),
    val error:String? = ""
)