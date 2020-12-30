package org.engrave.packup.data.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.reflect.KClass

typealias PersistentMap = Map<String, Any?>

abstract class PersistentData(val map: PersistentMap) {
    abstract fun byMap(pMap: Map<Preferences.Key<*>, Any>): PersistentData
}

class DataStorePreference<T : PersistentData>(
    context: Context,
    initialPersistentData: T
) {
    private val dataStore: DataStore<Preferences> =
        context.createDataStore(name = initialPersistentData::class.simpleName!!)

    val dataFlow = dataStore.data.catch {

    }.map {
        it.asMap()
    }

}

class A(dataMap: PersistentMap) : PersistentData(dataMap) {
    val name: String by map
    val age: Int by map
    override fun byMap(pMap: Map<Preferences.Key<*>, Any>) = A(
        pMap.mapKeys {
            it.key.name
        }
    )
}
@Deprecated("折腾不动了")
suspend inline fun <reified T : PersistentData> storePersistentData(context: Context, data: T): T {
    val dataStore: DataStore<Preferences> = context.createDataStore(name = data::class.simpleName!!)
    val dataFlow = dataStore.data.map {
        data.byMap(
            it.asMap()
        )
    }
    return dataFlow.first() as T
}
@Deprecated("折腾不动了")
suspend inline fun <reified T : PersistentData> restorePersistentData(
    context: Context,
    data: T,
    modifyMap: Map<String, Any>
) = context.createDataStore(name = data::class.simpleName!!).edit {
    modifyMap.forEach { entry ->
        it[preferencesKey(entry.key)] = entry.value
    }
}