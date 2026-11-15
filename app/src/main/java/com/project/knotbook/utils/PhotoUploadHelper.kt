package com.project.knotbook.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun createPhotoPart(
    context: Context,
    uri: Uri
): MultipartBody.Part {

    val contentResolver = context.contentResolver

    val mimeType = contentResolver.getType(uri)
        ?: "image/jpeg"

    val bytes = contentResolver.openInputStream(uri)?.use {
        it.readBytes()
    } ?: throw IllegalArgumentException("Unable to read selected image")

    val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        name = "file",
        filename = "knotbook_${System.currentTimeMillis()}.jpg",
        body = requestBody
    )
}