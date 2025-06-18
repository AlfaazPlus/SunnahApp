package com.alfaazplus.sunnah.di

import android.app.Application
import androidx.room.Room
import com.alfaazplus.sunnah.db.databases.AppDatabase
import com.alfaazplus.sunnah.db.databases.ScholarsDatabase
import com.alfaazplus.sunnah.db.databases.UserDatabase
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.repository.userdata.UserRepository
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
        return Room
            .databaseBuilder(app, AppDatabase::class.java, "sunnah_app_db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideScholarsDatabase(app: Application): ScholarsDatabase {
        val dbStream = app.assets.open("scholars_info.db.bz2")
        val bz2Stream = BZip2CompressorInputStream(dbStream)

        return Room
            .databaseBuilder(app, ScholarsDatabase::class.java, "scholars_db")
            .createFromInputStream { bz2Stream }
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDatabase(app: Application): UserDatabase {
        return Room
            .databaseBuilder(app, UserDatabase::class.java, "user_db")
            .build()
    }

    @Provides
    @Singleton
    fun provideHadithRepository(hadithDb: AppDatabase, scholarsDb: ScholarsDatabase): HadithRepository {
        return HadithRepository(hadithDb.hadithDao, scholarsDb.scholarsDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(hadithDb: AppDatabase, userDb: UserDatabase): UserRepository {
        return UserRepository(hadithDb.hadithDao, userDb.dao)
    }
}