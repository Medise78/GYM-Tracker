package com.medise.bashga.domain.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.medise.bashga.data.converter.DateTimeConverter
import com.medise.bashga.util.ActivityPerson
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "persons"
)
data class PersonEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "person_image")
    val personImage: String? = null,
    @ColumnInfo(name = "f_name")
    val name: String? = null,
    @ColumnInfo(name = "l_name")
    val lastName: String? = null,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean? = null,
    @TypeConverters(DateTimeConverter::class)
    @ColumnInfo(name = "start_day")
    var startDay: String? = null,
    @TypeConverters(DateTimeConverter::class)
    @ColumnInfo(name = "end_day")
    val endDay: String? = null,
    @ColumnInfo(name = "pay_status")
    var payStatus: ActivityPerson = ActivityPerson.NOTPAID,
    @ColumnInfo(name = "is_payed")
    val isPayed: Boolean? = null,
    @ColumnInfo(name = "remain_date")
    var remainDate:String? = ""
)
