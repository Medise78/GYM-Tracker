package com.medise.bashga.data.repository

import com.medise.bashga.domain.model.PersonEntity
import kotlinx.coroutines.flow.Flow

interface GymRepository {

    suspend fun insertPerson(person: PersonEntity)

    fun getAllPerson():Flow<List<PersonEntity>>

    suspend fun deletePerson(person: PersonEntity)

    suspend fun updatePerson(person: PersonEntity)

}