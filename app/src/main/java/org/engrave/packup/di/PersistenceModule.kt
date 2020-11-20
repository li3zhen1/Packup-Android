package org.engrave.packup.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import org.engrave.packup.data.AppDatabase
import org.engrave.packup.data.account.AccountInfoRepository
import javax.inject.Singleton


/**
 * Dependency injections that are relevant to local storage.
 * @see <a href="https://developer.android.google.cn/training/dependency-injection/hilt-jetpack?hl=zh_cn">Hilt</a>
 */
@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    private const val APP_DATABASE_NAME = "scheduler.db"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) = Room
        .databaseBuilder(
            context,
            AppDatabase::class.java,
            APP_DATABASE_NAME
        )
        .build()

    @Provides
    @Singleton
    fun provideClassInfoDao(appDatabase: AppDatabase) = appDatabase.classInfoDao()

    @Provides
    @Singleton
    fun provideDeadlineDao(appDatabase: AppDatabase) = appDatabase.deadlineDao()

}