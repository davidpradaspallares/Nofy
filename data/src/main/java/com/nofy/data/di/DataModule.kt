package com.nofy.data.di

import android.content.Context
import androidx.room.Room
import com.nofy.data.local.NofyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): NofyDatabase {
        return Room.databaseBuilder(
            context,
            NofyDatabase::class.java,
            "nofy.db"
        ).build()
    }
}
