package com.medise.bashga.data.converter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

object DateTimeConverter {

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toZonedDateTime(date:String?):LocalDate?{
        return if (date.isNullOrEmpty()) null
        else LocalDate.parse(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromZonedDateTime(date: LocalDate?):String?{
        return if (date != null)
            DateTimeFormatter.ISO_DATE.format(date)
        else null
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap):String?{
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG , 100 , baos)
        val byte = baos.toByteArray()
        val temp = Base64.getEncoder().encodeToString(byte)
        if (temp == null)
            return null
        else return temp
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toBitmap(image:String?):Bitmap?{
        try {
            val encodeByte = Base64.getDecoder().decode(image)
            val bitmap = BitmapFactory.decodeByteArray(encodeByte , 0 , encodeByte.size)
            if (bitmap == null)
                return null
            else return bitmap
        }catch (e:Exception){
            e.message
            return null
        }
    }

    @TypeConverter
    fun fromUri(image:String?):Uri?{
        return if (image == null) null else Uri.parse(image)
    }

    @TypeConverter
    fun toUri(uri: Uri?):String?{
        return uri?.toString()
    }



}
