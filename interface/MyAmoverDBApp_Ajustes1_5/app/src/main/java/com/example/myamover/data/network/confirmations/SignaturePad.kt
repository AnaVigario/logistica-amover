package com.example.myamover.data.network.confirmations

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import android.graphics.Canvas as AndroidCanvas

private data class Stroke(val points: MutableList<Offset>)

@Composable
fun SignaturePad(
    modifier: Modifier = Modifier,
    onSaved: (Uri) -> Unit
) {
    val strokes = remember { mutableStateListOf<Stroke>() }
    var currentStroke by remember { mutableStateOf<Stroke?>(null) }
    val context = LocalContext.current


    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.White)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val s = Stroke(mutableListOf(offset))
                                strokes.add(s)
                                currentStroke = s
                            },
                            onDrag = { change, _ ->
                                currentStroke?.points?.add(change.position)
                            },
                            onDragEnd = { currentStroke = null }
                        )
                    }
            ) {
                strokes.forEach { s ->
                    val pts = s.points
                    for (i in 0 until (pts.size - 1)) {
                        drawLine(
                            color = Color.Black,
                            start = pts[i],
                            end = pts[i + 1],
                            strokeWidth = 5f
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { strokes.clear() }) { Text("Limpar") }

            Button(onClick = {
                val uri = saveSignatureToPng(context, strokes)
                onSaved(uri)
            }) { Text("Guardar assinatura") }
        }
    }
}

private fun saveSignatureToPng(context: Context, strokes: List<Stroke>): Uri {
    val width = 900
    val height = 350
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bmp)
    canvas.drawColor(android.graphics.Color.WHITE)

    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true   // ✅ Kotlin: true (não True)
    }

    strokes.forEach { s ->
        val pts = s.points
        for (i in 0 until (pts.size - 1)) {
            val a = pts[i]
            val b = pts[i + 1]
            canvas.drawLine(a.x, a.y, b.x, b.y, paint)
        }
    }

    val file = File(context.cacheDir, "signature_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use { out ->
        bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
