package com.example.myapplication.Backend

import Event

import android.location.Location
import android.util.Log
import com.example.myapplication.DataHolders.PositionHolder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException


object EventsBackend {

    private const val BASE_URL = "https://maccproject.pythonanywhere.com"

    // Function to fetch events data from the backend
    fun fetchEvents(token: String, onResult: (Result<List<Event>>) -> Unit) {

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/events")
            .header("Authorization", token)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                // Handle connection failure
                onResult(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val events = parseEvents(responseBody)
                    onResult(Result.success(events))
                } else {
                    // Handle errors from the backend
                    Log.d("mytag", responseBody.toString())
                    onResult(Result.failure(IOException("Failed to fetch events")))
                }
            }
        })
    }

    // Parse the JSON response to a list of events
    private fun parseEvents(response: String): List<Event> {
        val jsonArray = JSONArray(response)
        val events = mutableListOf<Event>()

        for (i in 0 until jsonArray.length()) {
            val eventObject = jsonArray.optJSONObject(i)
            //distance computation
            val latitude = eventObject.optDouble("latitude")
            val longitude = eventObject.optDouble("longitude")

            val res= FloatArray(1);
            Location.distanceBetween(latitude, longitude, PositionHolder.lastPostion.latitude, PositionHolder.lastPostion.longitude, res)

            val distanceInMeters = res[0]


            val event = Event(
                id = eventObject.optInt("id"),
                name = eventObject.optString("name"),
                latitude = latitude,
                longitude = longitude,
                date = eventObject.optString("datetime"),
                organizerName = eventObject.optString("organizer_name"),
                description = eventObject.optString("description"),
                encoded_image = eventObject.optString("encoded_image"),
                distance = distanceInMeters,
            )

            Log.d("buggettone", "event: $event")

            events.add(event)
        }

        return events
    }
}
