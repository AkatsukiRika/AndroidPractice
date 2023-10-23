package com.tangping.androidpractice.ui.datastore.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.tangping.androidpractice.ProtoDataStore
import java.io.InputStream
import java.io.OutputStream

object ProtoDataStoreSerializer : Serializer<ProtoDataStore> {
    override val defaultValue: ProtoDataStore
        get() = ProtoDataStore.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ProtoDataStore {
        try {
            return ProtoDataStore.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ProtoDataStore, output: OutputStream) {
        t.writeTo(output)
    }
}
