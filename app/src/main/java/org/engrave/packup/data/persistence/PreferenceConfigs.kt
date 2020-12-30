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

const val DEFAULT_END_OF_WEEK = false
const val DEFAULT_AUTO_DETECT = true
const val DEFAULT_FREQ_DEADLINE_CRAWLER = 60
const val DEFAULT_FREQ_CLASS_INFO_CRAWLER = 0

data class ApplicationPreferenceConfigs(
    var useSaturdayAsEndOfWeek: Boolean = DEFAULT_END_OF_WEEK,
    var autoDetectSubmission: Boolean = DEFAULT_AUTO_DETECT,
    var frequencyOfDeadlineCrawler: Int = DEFAULT_FREQ_DEADLINE_CRAWLER,
    var frequencyOfClassInfoCrawler: Int = DEFAULT_FREQ_CLASS_INFO_CRAWLER, //No Auto Crawl
) {

}

class ApplicationPreferenceConfigsRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferenceConfigDataStore: DataStore<Preferences> =
        context.createDataStore(name = "preference.ds")
    private val preferenceConfigsFlow = preferenceConfigDataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }.map {
            ApplicationPreferenceConfigs(
                it[USE_SATURDAY_END_OF_WEEK] ?: DEFAULT_END_OF_WEEK,
                it[AUTO_DETECT_SUBMISSION] ?: DEFAULT_AUTO_DETECT,
                it[FREQ_DEADLINE_CRAWLER] ?: DEFAULT_FREQ_DEADLINE_CRAWLER,
                it[FREQ_CLASS_INFO_CRAWLER] ?: DEFAULT_FREQ_CLASS_INFO_CRAWLER
            )
        }

    suspend fun getPreferenceConfigs() = preferenceConfigsFlow.first()


    // TODO: Handle IO Exception
    suspend fun setUseSaturdayAsEndOfWeek(value: Boolean) = preferenceConfigDataStore.edit {
        it[USE_SATURDAY_END_OF_WEEK] = value
    }

    suspend fun setAutoDetectSubmission(value: Boolean) = preferenceConfigDataStore.edit {
        it[AUTO_DETECT_SUBMISSION] = value
    }

    suspend fun setFrequencyOfDeadlineCrawler(value: Int) = preferenceConfigDataStore.edit {
        it[FREQ_DEADLINE_CRAWLER] = value
    }

    suspend fun setFrequencyOfClassInfoCrawler(value: Int) = preferenceConfigDataStore.edit {
        it[FREQ_CLASS_INFO_CRAWLER] = value
    }

    companion object {
        private val USE_SATURDAY_END_OF_WEEK = preferencesKey<Boolean>("endOfWeek")
        private val AUTO_DETECT_SUBMISSION = preferencesKey<Boolean>("detectSubmission")


        private val FREQ_DEADLINE_CRAWLER = preferencesKey<Int>("freqDdl")
        private val FREQ_CLASS_INFO_CRAWLER = preferencesKey<Int>("freqClassInfo")
    }
}