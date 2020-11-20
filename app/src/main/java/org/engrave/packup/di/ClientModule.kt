package org.engrave.packup.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.engrave.packup.client.PackupNetworkClient
import org.engrave.packup.client.PkuClient
import org.engrave.packup.data.account.AccountInfoRepository
import javax.inject.Singleton

/**
 * Dependency injections that are relevant to local storage.
 * @see <a href="https://developer.android.google.cn/training/dependency-injection/hilt-jetpack?hl=zh_cn">Hilt</a>
 */
@Module
@InstallIn(SingletonComponent::class)
object ClientModule {

    @Provides
    @Singleton
    fun provideClient(accountInfoRepo: AccountInfoRepository): PackupNetworkClient =
        when (accountInfoRepo.getAccountInfo.schoolAbbr) {
            "pku" -> PkuClient(accountInfoRepo.getAccountInfo)
            else -> PkuClient(accountInfoRepo.getAccountInfo)
        }

}