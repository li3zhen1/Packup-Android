package org.engrave.packup.data.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class ElectiveStatusPersistence(val openTime: Long, val lotteryTime: Long, val shutdownTime: Long) {
    val isValid get() = openTime == 0L || lotteryTime == 0L || shutdownTime == 0L
}

class ElectiveStatusPersistenceRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val electiveStatusDataStore: DataStore<Preferences> =
        context.createDataStore(name = "elective_status.ds")

    private val electiveStatusInfoFlow = electiveStatusDataStore.data.catch { e ->
        if (e is IOException) emit(emptyPreferences()) else throw e
    }.map {
        ElectiveStatusPersistence(
            it[OPEN_TIME_FIELD] ?: 0,
            it[LOTTERY_TIME_FIELD] ?: 0,
            it[SHUT_DOWN_TIME_FIELD] ?: 0
        )
    }

    suspend fun getElectiveStatusInfoFlow() = electiveStatusInfoFlow.first()

    suspend fun setElectiveStatusInfoFlow(

    ) = electiveStatusDataStore.edit {

    }

    companion object {
        val OPEN_TIME_FIELD = preferencesKey<Long>("open")
        val LOTTERY_TIME_FIELD = preferencesKey<Long>("lottery")
        val SHUT_DOWN_TIME_FIELD = preferencesKey<Long>("shutdown")
    }
}