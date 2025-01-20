package com.example.passporter.di

import android.content.Context
import androidx.room.Room
import com.example.passporter.data.local.dao.BorderDao
import com.example.passporter.data.local.database.BorderDatabase
import com.example.passporter.data.mapper.BorderPointMapper
import com.example.passporter.data.mapper.BorderUpdateMapper
import com.example.passporter.data.remote.api.FirestoreService
import com.example.passporter.data.repository.BorderRepositoryImpl
import com.example.passporter.domain.repository.BorderRepository
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
    fun provideBorderDatabase(
        @ApplicationContext context: Context
    ): BorderDatabase = Room.databaseBuilder(
        context,
        BorderDatabase::class.java,
        "border_database"
    ).build()

    @Provides
    @Singleton
    fun provideBorderDao(
        database: BorderDatabase
    ): BorderDao = database.borderDao()

    @Provides
    @Singleton
    fun provideFirestoreService(): FirestoreService = FirestoreService()

    @Provides
    @Singleton
    fun provideBorderRepository(
        firestoreService: FirestoreService,
        borderDao: BorderDao,
        borderPointMapper: BorderPointMapper,
        borderUpdateMapper: BorderUpdateMapper,
        dispatcherProvider: DispatcherProvider
    ): BorderRepository = BorderRepositoryImpl(
        firestoreService,
        borderDao,
        borderPointMapper,
        borderUpdateMapper,
        dispatcherProvider
    )
}