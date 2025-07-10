package com.example.trackzen.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converter {
    @TypeConverter
    fun toBitmap(bytes: ByteArray?): Bitmap? {
        return bytes?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    }
   @TypeConverter
   fun fromBitmap(bmp: Bitmap?): ByteArray? {
       return bmp?.let {
           val outputStream = ByteArrayOutputStream()
           it.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
           outputStream.toByteArray()
       }
   }
}