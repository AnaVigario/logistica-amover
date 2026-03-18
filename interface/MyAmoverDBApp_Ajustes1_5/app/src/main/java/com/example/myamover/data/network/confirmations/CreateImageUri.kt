package com.example.myamover.data.network.confirmations

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
