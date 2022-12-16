package com.medise.bashga.domain.repository

import com.medise.bashga.data.dao.PersonDao
import com.medise.bashga.data.repository.GymRepository
import com.medise.bashga.domain.model.PersonEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GymRepositoryImpl @Inject constructor(
    private val dao: PersonDao
) : GymRepository {
    override suspend fun insertPerson(person: PersonEntity) {
        dao.insertPerson(person)
    }

    override fun getAllPerson(): Flow<List<PersonEntity>> {
        return dao.getAllPersons()
    }

    override suspend fun deletePerson(person: PersonEntity) {
        dao.deletePerson(person)
    }

    override suspend fun updatePerson(person: PersonEntity) {
        dao.updatePerson(person)
    }
}