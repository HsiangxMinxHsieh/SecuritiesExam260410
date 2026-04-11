package com.timmy.datastorelibs.repo

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject

/**
 * DataStore 讀寫封裝，支援同步與非同步存取。
 */
class DataStoreRepository @Inject constructor(context: Application) {


    private val Context._dataStore: DataStore<Preferences> by preferencesDataStore(
        name = DATASTORE_NAME,
    )

    val dataStore: DataStore<Preferences> = context._dataStore

    private suspend fun <T> saveData(key: Preferences.Key<T>, value: T) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[key] = value
        }
    }

    inline fun <reified T> readData(key: Preferences.Key<T>, defaultValue: T = T::class.java.getDeclaredConstructor().newInstance()): Flow<T> =
        dataStore.data
            .catch { cause ->
                if (cause is IOException) {
                    cause.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw cause
                }
            }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }

    companion object {
        const val DATASTORE_NAME = "DataStore"

        // 偏好鍵一覽

        val sampleStringKey = stringPreferencesKey("user_info_response_Key")
        val sampleDataKey = stringPreferencesKey("Sample_Data_Response_Key")
        val getDataIntervalLongKey = longPreferencesKey("GET_DATA_INTERVAL_LONG_KEY")
        val sampleIntKey = intPreferencesKey("Sample_Int_Key")
        val sampleBooleanKey = booleanPreferencesKey("Sample_boolean_Key")

    }

    // 已封裝之項目
//    var sampleData: BBUData
//        get() = runBlocking {
//            kotlin.runCatching {
//                readData(sampleDataKey).firstOrNull()
//                    ?.toDataBean<BBUData>()
//            }.getOrNull() ?: BBUData()
//        }
//        set(value) = runBlocking { saveData(sampleDataKey, value.toJson()) }

    var sampleString: String
        get() = runBlocking { readData(sampleStringKey).first() }
        set(value) = runBlocking { saveData(sampleStringKey, value) }

    var sampleBoolean: Boolean
        get() = runBlocking { readData(sampleBooleanKey).first() }
        set(value) = runBlocking { saveData(sampleBooleanKey, value) }

    var sampleInt: Int
        get() = runBlocking { readData(sampleIntKey).first() }
        set(value) = runBlocking { saveData(sampleIntKey, value) }

    var getDataInterval: Long
        get() = runBlocking { readData(getDataIntervalLongKey, 0L).first() }
        set(value) = runBlocking { saveData(getDataIntervalLongKey, value) }

}
