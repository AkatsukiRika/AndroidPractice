package com.tangping.androidpractice.ui.datastore

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tangping.androidpractice.ProtoDataStore
import com.tangping.androidpractice.R
import com.tangping.androidpractice.ui.datastore.proto.ProtoDataStoreSerializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.demoDataStore: DataStore<Preferences> by preferencesDataStore(name = "demo")
val Context.protoDataStore: DataStore<ProtoDataStore> by dataStore(
    fileName = "proto_data_store.pb",
    serializer = ProtoDataStoreSerializer
)

@HiltViewModel
class DataStoreDemoViewModel @Inject constructor() : ViewModel() {
    companion object {
        const val TYPE_INT = "Int"
        const val TYPE_LONG = "Long"
        const val TYPE_BOOLEAN = "Boolean"
        const val TYPE_STRING = "String"
        const val TYPE_DOUBLE = "Double"
        const val TYPE_FLOAT = "Float"
    }

    var viewStates by mutableStateOf(DataStoreDemoState())
        private set

    fun dispatch(context: Context, event: DataStoreDemoEvent) {
        when (event) {
            is DataStoreDemoEvent.WriteData -> {
                writeData(context, event.type, event.key, event.value)
            }

            is DataStoreDemoEvent.ReadData -> {
                readData(context, event.type, event.key)
            }

            is DataStoreDemoEvent.ChangeMode -> {
                viewStates = viewStates.copy(mode = event.mode)
            }
        }
    }

    private fun writeData(context: Context, type: String, key: String, value: String) {
        if (key.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.key_empty_toast), Toast.LENGTH_SHORT).show()
            return
        }
        val isProto = viewStates.mode == DataStoreDemoMode.PROTO
        when (type) {
            TYPE_INT -> {
                val intValue = value.toIntOrNull()
                if (intValue == null) {
                    Toast.makeText(context, context.getString(R.string.value_type_toast), Toast.LENGTH_SHORT).show()
                    return
                }
                viewModelScope.launch {
                    if (isProto) {
                        context.protoDataStore.updateData { protoDataStore ->
                            protoDataStore.toBuilder()
                                .putIntData(key, intValue)
                                .build()
                        }
                    } else {
                        val prefKey = intPreferencesKey(key)
                        context.demoDataStore.edit {
                            it[prefKey] = intValue
                        }
                    }
                    Toast.makeText(context, context.getString(R.string.write_data_success), Toast.LENGTH_SHORT).show()
                }
            }

            TYPE_LONG -> {
                val longValue = value.toLongOrNull()
                if (longValue == null) {
                    Toast.makeText(context, context.getString(R.string.value_type_toast), Toast.LENGTH_SHORT).show()
                    return
                }
                viewModelScope.launch {
                    if (isProto) {
                        context.protoDataStore.updateData { protoDataStore ->
                            protoDataStore.toBuilder()
                                .putLongData(key, longValue)
                                .build()
                        }
                    } else {
                        val prefKey = longPreferencesKey(key)
                        context.demoDataStore.edit {
                            it[prefKey] = longValue
                        }
                    }
                    Toast.makeText(context, context.getString(R.string.write_data_success), Toast.LENGTH_SHORT).show()
                }
            }

            TYPE_BOOLEAN -> {
                val booleanValue = value.toBooleanStrictOrNull()
                if (booleanValue == null) {
                    Toast.makeText(context, context.getString(R.string.value_type_toast), Toast.LENGTH_SHORT).show()
                    return
                }
                viewModelScope.launch {
                    if (isProto) {
                        context.protoDataStore.updateData { protoDataStore ->
                            protoDataStore.toBuilder()
                                .putBooleanData(key, booleanValue)
                                .build()
                        }
                    } else {
                        val prefKey = booleanPreferencesKey(key)
                        context.demoDataStore.edit {
                            it[prefKey] = booleanValue
                        }
                    }
                    Toast.makeText(context, context.getString(R.string.write_data_success), Toast.LENGTH_SHORT).show()
                }
            }

            TYPE_STRING -> {
                viewModelScope.launch {
                    if (isProto) {
                        context.protoDataStore.updateData { protoDataStore ->
                            protoDataStore.toBuilder()
                                .putStringData(key, value)
                                .build()
                        }
                    } else {
                        val prefKey = stringPreferencesKey(key)
                        context.demoDataStore.edit {
                            it[prefKey] = value
                        }
                    }
                    Toast.makeText(context, context.getString(R.string.write_data_success), Toast.LENGTH_SHORT).show()
                }
            }

            TYPE_DOUBLE -> {
                val doubleValue = value.toDoubleOrNull()
                if (doubleValue == null) {
                    Toast.makeText(context, context.getString(R.string.value_type_toast), Toast.LENGTH_SHORT).show()
                    return
                }
                viewModelScope.launch {
                    if (isProto) {
                        context.protoDataStore.updateData { protoDataStore ->
                            protoDataStore.toBuilder()
                                .putDoubleData(key, doubleValue)
                                .build()
                        }
                    } else {
                        val prefKey = doublePreferencesKey(key)
                        context.demoDataStore.edit {
                            it[prefKey] = doubleValue
                        }
                    }
                    Toast.makeText(context, context.getString(R.string.write_data_success), Toast.LENGTH_SHORT).show()
                }
            }

            TYPE_FLOAT -> {
                val floatValue = value.toFloatOrNull()
                if (floatValue == null) {
                    Toast.makeText(context, context.getString(R.string.value_type_toast), Toast.LENGTH_SHORT).show()
                    return
                }
                viewModelScope.launch {
                    if (isProto) {
                        context.protoDataStore.updateData { protoDataStore ->
                            protoDataStore.toBuilder()
                                .putFloatData(key, floatValue)
                                .build()
                        }
                    } else {
                        val prefKey = floatPreferencesKey(key)
                        context.demoDataStore.edit {
                            it[prefKey] = floatValue
                        }
                    }
                    Toast.makeText(context, context.getString(R.string.write_data_success), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun readData(context: Context, type: String, key: String) {
        if (key.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.key_empty_toast), Toast.LENGTH_SHORT).show()
            return
        }
        val isProto = viewStates.mode == DataStoreDemoMode.PROTO
        when (type) {
            TYPE_INT -> {
                viewModelScope.launch {
                    try {
                        val flow: Flow<Int> = if (isProto) {
                            context.protoDataStore.data.map { it.intDataMap.getOrDefault(key, 0) }
                        } else {
                            val prefKey = intPreferencesKey(key)
                            context.demoDataStore.data.map { preferences ->
                                preferences[prefKey] ?: 0
                            }
                        }
                        viewStates = viewStates.copy(
                            readValue = flow.first().toString()
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            TYPE_LONG -> {
                viewModelScope.launch {
                    try {
                        val flow: Flow<Long> = if (isProto) {
                            context.protoDataStore.data.map { it.longDataMap.getOrDefault(key, 0L) }
                        } else {
                            val prefKey = longPreferencesKey(key)
                            context.demoDataStore.data.map { preferences ->
                                preferences[prefKey] ?: 0L
                            }
                        }
                        viewStates = viewStates.copy(
                            readValue = flow.first().toString()
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            TYPE_BOOLEAN -> {
                viewModelScope.launch {
                    try {
                        val flow: Flow<Boolean> = if (isProto) {
                            context.protoDataStore.data.map { it.booleanDataMap.getOrDefault(key, false) }
                        } else {
                            val prefKey = booleanPreferencesKey(key)
                            context.demoDataStore.data.map { preferences ->
                                preferences[prefKey] ?: false
                            }
                        }
                        viewStates = viewStates.copy(
                            readValue = flow.first().toString()
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            TYPE_STRING -> {
                viewModelScope.launch {
                    try {
                        val flow: Flow<String> = if (isProto) {
                            context.protoDataStore.data.map { it.stringDataMap.getOrDefault(key, "") }
                        } else {
                            val prefKey = stringPreferencesKey(key)
                            context.demoDataStore.data.map { preferences ->
                                preferences[prefKey] ?: ""
                            }
                        }
                        viewStates = viewStates.copy(
                            readValue = flow.first()
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            TYPE_DOUBLE -> {
                viewModelScope.launch {
                    try {
                        val flow: Flow<Double> = if (isProto) {
                            context.protoDataStore.data.map { it.doubleDataMap.getOrDefault(key, 0.0) }
                        } else {
                            val prefKey = doublePreferencesKey(key)
                            context.demoDataStore.data.map { preferences ->
                                preferences[prefKey] ?: 0.0
                            }
                        }
                        viewStates = viewStates.copy(
                            readValue = flow.first().toString()
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            TYPE_FLOAT -> {
                viewModelScope.launch {
                    try {
                        val flow: Flow<Float> = if (isProto) {
                            context.protoDataStore.data.map { it.floatDataMap.getOrDefault(key, 0f) }
                        } else {
                            val prefKey = floatPreferencesKey(key)
                            context.demoDataStore.data.map { preferences ->
                                preferences[prefKey] ?: 0f
                            }
                        }
                        viewStates = viewStates.copy(
                            readValue = flow.first().toString()
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

data class DataStoreDemoState(
    val readValue: String = "",
    val mode: DataStoreDemoMode = DataStoreDemoMode.PREFERENCES
)

enum class DataStoreDemoMode {
    PREFERENCES, PROTO
}

sealed class DataStoreDemoEvent {
    data class WriteData(val type: String, val key: String, val value: String) : DataStoreDemoEvent()

    data class ReadData(val type: String, val key: String) : DataStoreDemoEvent()

    data class ChangeMode(val mode: DataStoreDemoMode) : DataStoreDemoEvent()
}