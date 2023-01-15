package com.medise.bashga.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.medise.bashga.data.converter.DateTimeConverter
import com.medise.bashga.data.dao.PersonDao
import com.medise.bashga.domain.model.PersonEntity

@Database(
    entities = [PersonEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(DateTimeConverter::class)
abstract class PersonDataBase:RoomDatabase() {

    abstract fun personDao():PersonDao

    companion object{
        const val DB_NAME = "GYM_DB"
    }
}