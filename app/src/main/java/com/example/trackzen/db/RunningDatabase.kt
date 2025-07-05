package com.example.trackzen.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Run::class], version = 1)
@TypeConverters(Converter::class)
abstract class RunningDatabase: RoomDatabase() {
    abstract fun getRunDao(): RunDao

}