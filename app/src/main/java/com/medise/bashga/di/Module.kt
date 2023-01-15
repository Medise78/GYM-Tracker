package com.medise.bashga.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.medise.bashga.data.PersonDataBase
import com.medise.bashga.data.repository.ExactRepository
import com.medise.bashga.data.repository.GymRepository
import com.medise.bashga.domain.repository.ExactRepositoryImpl
import com.medise.bashga.domain.repository.GymRepositoryImpl
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@dagger.Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideGymDb(
        @ApplicationContext context: Context
    ): PersonDataBase {
        return Room.databaseBuilder(
            context,
            PersonDataBase::class.java,
            PersonDataBase.DB_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRepository(dataBase: PersonDataBase , @ApplicationContext context: Context , sharedPreferences: SharedPreferences): GymRepository {
        return GymRepositoryImpl(dataBase.personDao())
    }

    @Provides
    @Singleton
    fun provideExactRepository(@ApplicationContext context: Context , sharedPreferences: SharedPreferences):ExactRepository{
        return ExactRepositoryImpl(context, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context):SharedPreferences{
        return context.getSharedPreferences("alarm_shared" , Context.MODE_PRIVATE)
    }
}