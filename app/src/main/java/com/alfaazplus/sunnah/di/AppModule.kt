package com.alfaazplus.sunnah.di

import android.app.Application
import androidx.room.Room
import com.alfaazplus.sunnah.db.AppDatabase
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.repository.hadith.HadithRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideHadithDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "sunnah_app_db").build()
    }

    @Provides
    @Singleton
    fun provideHadithRepository(db: AppDatabase): HadithRepository {
        return HadithRepositoryImpl(db.hadithDao)
    }
}