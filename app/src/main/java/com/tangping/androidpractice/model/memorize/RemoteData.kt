package com.tangping.androidpractice.model.memorize

import com.google.gson.annotations.SerializedName

data class RemoteData(
    val items: MutableList<RemoteDataItem> = mutableListOf()
) {
    fun containsFile(fileName: String) = items.map { it.fileName }.contains(fileName)
}

data class RemoteDataItem(
    @SerializedName("file_name")
    var fileName: String,
    var url: String
)