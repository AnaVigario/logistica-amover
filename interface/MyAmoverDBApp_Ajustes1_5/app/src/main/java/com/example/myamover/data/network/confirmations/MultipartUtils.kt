package com.example.myamover.data.network.confirmations

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Converte uma imagem JPEG (Uri) para MultipartBody.Part
 */
fun uriToJpegPart(
    context: Context,
    uri: Uri,
    partName: String
): MultipartBody.Part {
    val bytes = context.contentResolver
        .openInputStream(uri)!!
        .use { it.readBytes() }

    val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        partName,
        "photo.jpg",
        requestBody
    )
}

/**
 * Converte uma imagem PNG (Uri) para MultipartBody.Part
 * (usado para assinatura)
 */
fun uriToPngPart(
    context: Context,
    uri: Uri,
    partName: String
): MultipartBody.Part {
    val bytes = context.contentResolver
        .openInputStream(uri)!!
        .use { it.readBytes() }

    val requestBody = bytes.toRequestBody("image/png".toMediaTypeOrNull())



    return MultipartBody.Part.createFormData(
        partName,
        "signature.png",
        requestBody
    )


}