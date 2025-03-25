package com.alfaazplus.sunnah.di

import android.app.Application
import androidx.room.Room
import com.alfaazplus.sunnah.db.AppDatabase
import com.alfaazplus.sunnah.db.ScholarsDatabase
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.repository.hadith.HadithRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
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
    fun provideScholarsDatabase(app: Application): ScholarsDatabase {
        val dbStream = app.assets.open("scholars_info.db.bz2")
        val bz2Stream = BZip2CompressorInputStream(dbStream)

        return Room.databaseBuilder(app, ScholarsDatabase::class.java, "scholars_db").createFromInputStream { bz2Stream }
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideHadithRepository(hadithDb: AppDatabase, scholarsDb: ScholarsDatabase): HadithRepository {
        return HadithRepositoryImpl(hadithDb.hadithDao, scholarsDb.scholarsDao)
    }
}