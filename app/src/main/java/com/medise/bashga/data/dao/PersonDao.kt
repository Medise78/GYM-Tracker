package com.medise.bashga.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.medise.bashga.domain.model.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity)

    @Query("SELECT * FROM persons ORDER BY start_day ASC")
    fun getAllPersons(): Flow<List<PersonEntity>>

    @Delete
    suspend fun deletePerson(person: PersonEntity)

    @Update
    suspend fun updatePerson(person: PersonEntity)
}