package com.tangping.androidpractice.ui.datastore

import android.content.Context
import android.widget.Toast
import androidx.datastore.core.DataStore
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
import com.tangping.androidpractice.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.demoDataStore: DataStore<Preferences> by preferencesDataStore(name = "demo")

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

    fun dispatch(context: Context, event: DataStoreDemoEvent) {
        when (event) {
            is DataStoreDemoEvent.WriteData -> {
                writeData(context, event.type, event.key, event.value)
            }
        }
    }

    private fun writeData(context: Context, type: String, key: String, value: String) {
        if (key.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.key_empty_toast), Toast.LENGTH_SHORT).show()
            return
        }
        when (type) {
            TYPE_INT -> {
                val intValue = value.toIntOrNull()
                if (intValue == null) {
                    Toast.makeText(context, context.getString(R.string.value_type_toast), Toast.LENGTH_SHORT).show()
                    return
                }
                viewModelScope.launch {
                    val prefKey = intPreferencesKey(key)
                    context.demoDataStore.edit {
                        it[prefKey] = intValue
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
                    val prefKey = longPreferencesKey(key)
                    context.demoDataStore.edit {
                        it[prefKey] = longValue
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
                    val prefKey = booleanPreferencesKey(key)
                    context.demoDataStore.edit {
                        it[prefKey] = booleanValue
                    }
                    Toast.makeText(context, context.getString(R.string.write_data_success), Toast.LENGTH_SHORT).show()
                }
            }

            TYPE_STRING -> {
                viewModelScope.launch {
                    val prefKey = stringPreferencesKey(key)
                    context.demoDataStore.edit {
                        it[prefKey] = value
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
                    val prefKey = doublePreferencesKey(key)
                    context.demoDataStore.edit {
                        it[prefKey] = doubleValue
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
                    val prefKey = floatPreferencesKey(key)
                    context.demoDataStore.edit {
                        it[prefKey] = floatValue
                    }
                    Toast.makeText(context, context.getString(R.string.write_data_success), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

sealed class DataStoreDemoEvent {
    data class WriteData(val type: String, val key: String, val value: String) : DataStoreDemoEvent()
}