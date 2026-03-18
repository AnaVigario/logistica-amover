package com.example.myamover.data.network


import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class RoutesApiClient(
    private val apiKey: String,
    private val http: OkHttpClient = OkHttpClient()
) {
    suspend fun computeEncodedPolyline(points: List<LatLng>): String = withContext(Dispatchers.IO) {
        require(points.size >= 2)

        val origin = points.first()
        val dest = points.last()
        val intermediates = points.drop(1).dropLast(1)

        val body = JSONObject().apply {
            put("origin", wp(origin))
            put("destination", wp(dest))
            if (intermediates.isNotEmpty()) {
                val arr = JSONArray()
                intermediates.forEach { arr.put(wp(it)) }
                put("intermediates", arr)
            }
            put("travelMode", "DRIVE")
            put("polylineQuality", "OVERVIEW")
        }

        val req = Request.Builder()
            .url("https://routes.googleapis.com/directions/v2:computeRoutes")
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Goog-Api-Key", apiKey)
            .addHeader("X-Goog-FieldMask", "routes.polyline.encodedPolyline")
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()

        http.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) error("Routes API error: ${resp.code}")
            val txt = resp.body?.string().orEmpty()
            val json = JSONObject(txt)
            val route0 = json.getJSONArray("routes").getJSONObject(0)
            route0.getJSONObject("polyline").getString("encodedPolyline")
        }
    }

    private fun wp(p: LatLng): JSONObject =
        JSONObject().apply {
            put("location", JSONObject().apply {
                put("latLng", JSONObject().apply {
                    put("latitude", p.latitude)
                    put("longitude", p.longitude)
                })
            })
        }
}

fun decodePolyline(encoded: String): List<LatLng> {
    val poly = mutableListOf<LatLng>()
    var index = 0
    var lat = 0
    var lng = 0

    while (index < encoded.length) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)

        val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)

        val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng

        poly.add(LatLng(lat / 1E5, lng / 1E5))
    }
    return poly
}

/** ~50 nodes: chunking. Máximo por pedido ~ 27 pontos (orig + 25 interm + dest). */
suspend fun computePolylineChunked(
    client: RoutesApiClient,
    allPoints: List<LatLng>
): List<LatLng> {
    if (allPoints.size < 2) return emptyList()

    val maxPoints = 27
    if (allPoints.size <= maxPoints) {
        val enc = client.computeEncodedPolyline(allPoints)
        return decodePolyline(enc)
    }

    val out = mutableListOf<LatLng>()
    var start = 0

    while (start < allPoints.lastIndex) {
        val endExclusive = (start + maxPoints).coerceAtMost(allPoints.size)
        val segment = allPoints.subList(start, endExclusive)

        val enc = client.computeEncodedPolyline(segment)
        val decoded = decodePolyline(enc)

        if (out.isEmpty()) out.addAll(decoded) else out.addAll(decoded.drop(1))

        start = endExclusive - 1 // overlap
    }

    return out
}