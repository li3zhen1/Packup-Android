package org.engrave.packup.data.account


import android.content.Context
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject

data class AccountInfo(var studentId: String, var password: String, var schoolAbbr: String?)

class AccountInfoRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val accountDataStore =
        context.createDataStore(name = "account.ds")

    private var accountInfoTemp: AccountInfo? = null

    private val accountInfoFlow: Flow<AccountInfo> = accountDataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }.map {
            AccountInfo(
                it[STUDENT_ID_FIELD].orEmpty(),
                it[PASSWORD_FIELD].orEmpty(),
                it[SCHOOL_ABBR]
            )
        }

    public val getAccountInfo
        get() = accountInfoTemp ?: runBlocking { readAccountInfo() }

    suspend fun readAccountInfo() = accountInfoFlow.first()

    suspend fun setAccountInfo(studentId: String, password: String, schoolAbbr: String?) {
        accountInfoTemp = AccountInfo(studentId, password, schoolAbbr)
        accountDataStore.edit {
            it[STUDENT_ID_FIELD] = studentId
            it[PASSWORD_FIELD] = password
            schoolAbbr?.let { abbr ->
                it[SCHOOL_ABBR] = abbr
            }
        }
    }

    companion object {
        val STUDENT_ID_FIELD = preferencesKey<String>("student_id")
        val PASSWORD_FIELD = preferencesKey<String>("password")
        val SCHOOL_ABBR = preferencesKey<String>("abbr")
    }
}