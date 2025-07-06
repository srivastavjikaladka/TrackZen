package com.example.trackzen.db.di

import android.content.Context
import androidx.room.Room
import com.example.trackzen.Repository.MainRepository
import com.example.trackzen.db.RunDao
import com.example.trackzen.db.RunningDatabase
import com.example.trackzen.other.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app, RunningDatabase::class.java, RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase) = db.getRunDao()
    @Singleton
    @Provides
    fun provideMainRepository(runDao: RunDao): MainRepository {
        return MainRepository(runDao)
    }


}