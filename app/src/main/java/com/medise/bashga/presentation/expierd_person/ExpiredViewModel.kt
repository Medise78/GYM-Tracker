package com.medise.bashga.presentation.expierd_person

import android.app.AlarmManager
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.medise.bashga.data.repository.ExactRepository
import com.medise.bashga.data.repository.GymRepository
import com.medise.bashga.domain.model.PersonEntity
import com.medise.bashga.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpiredViewModel @Inject constructor(
    private val repository: GymRepository,
    private val exactRepository: ExactRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _state = mutableStateOf<List<PersonEntity>>(emptyList())
    val state: State<List<PersonEntity>> get() = _state


    init {
        fetch()
    }

    fun fetch() = viewModelScope.launch {
        repository.getAllPerson().onEach {
            try {
                _state.value = it
            } catch (e: Exception) {
                Log.e("Error" , e.localizedMessage.toString())
            }
        }.launchIn(viewModelScope)
    }

    fun deletePerson(personEntity: PersonEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePerson(personEntity)
        }
    }

    fun updatePerson(personEntity: PersonEntity) = viewModelScope.launch {
        repository.updatePerson(personEntity)
    }

    fun scheduleRepeatingAlarm() {
        val alarmTimeMillis: Long = convertToAlarmTimeMillis(24, 0)
        val intervalMillis: Long = AlarmManager.INTERVAL_DAY
        exactRepository.scheduleRepeatingAlarm(RepeatingAlarm(alarmTimeMillis, intervalMillis))
    }

    fun clearRepeating(){
        exactRepository.clearRepeatingAlarm()
    }
}