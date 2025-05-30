package com.example.lupath

import android.content.Context
import com.example.lupath.data.database.AppDatabase
import com.example.lupath.data.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideMountainDao(database: AppDatabase): MountainDao {
        return database.mountainDao()
    }

    @Singleton
    @Provides
    fun provideHikePlanDao(appDatabase: AppDatabase): HikePlanDao {
        return appDatabase.hikePlanDao()
    }

    @Singleton
    @Provides
    fun provideChecklistItemDao(appDatabase: AppDatabase): ChecklistItemDao {
        return appDatabase.checklistItemDao()
    }

    @Singleton
    @Provides
    fun provideCampsiteDao(appDatabase: AppDatabase): CampsiteDao {
        return appDatabase.campsiteDao()
    }

    @Singleton
    @Provides
    fun provideTrailDao(appDatabase: AppDatabase): TrailDao {
        return appDatabase.trailDao()
    }

    @Singleton
    @Provides
    fun provideGuidelineDao(appDatabase: AppDatabase): GuidelineDao {
        return appDatabase.guidelineDao()
    }
}