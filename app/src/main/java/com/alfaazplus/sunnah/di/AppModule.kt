package com.alfaazplus.sunnah.di

import android.app.Application
import androidx.room3.Room
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.db.databases.HadithDatabaseLegacy
import com.alfaazplus.sunnah.db.databases.ScholarsDatabase
import com.alfaazplus.sunnah.db.databases.UserDatabase
import com.alfaazplus.sunnah.db.databases.UserDatabaseLegacy
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.repository.userdata.UserRepository
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
    fun provideAppDatabaseLegacy(app: Application): HadithDatabaseLegacy {
        return Room
            .databaseBuilder(app, HadithDatabaseLegacy::class.java, "sunnah_app_db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDatabaseLegacy(app: Application): UserDatabaseLegacy {
        return Room
            .databaseBuilder(app, UserDatabaseLegacy::class.java, "user_db")
            .build()
    }

    @Provides
    @Singleton
    fun provideScholarsDatabase(app: Application): ScholarsDatabase {
        return Room
            .databaseBuilder(app, ScholarsDatabase::class.java, "scholars_db")
            .createFromAsset("scholars_info.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDatabase(app: Application): UserDatabase {
        return Room
            .databaseBuilder(app, UserDatabase::class.java, "user_db_2")
            .build()
    }

    @Provides
    @Singleton
    fun provideHadithDatabase(app: Application): HadithDatabase {
        return Room
            .databaseBuilder(app, HadithDatabase::class.java, "sunnahapp_db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideHadithRepository2(hadithDb: HadithDatabase, scholarsDb: ScholarsDatabase): HadithRepository {
        return HadithRepository(hadithDb, scholarsDb.scholarsDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(hadithDb: HadithDatabase, userDb: UserDatabase): UserRepository {
        return UserRepository(hadithDb.hadithDao, userDb.dao)
    }
}
