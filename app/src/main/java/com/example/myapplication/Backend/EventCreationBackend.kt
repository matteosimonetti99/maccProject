package com.example.myapplication.Backend

import android.util.Log
import com.example.myapplication.DataHolders.InformationHolder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object EventCreationBackend {

    private const val BASE_REGISTER_URL = "https://maccproject.pythonanywhere.com/eventCreation"

    // Funzione per effettuare registrazione e ottenere un token
    fun register(
        datetimeReal: String,
        base64Image: String,
        latitude: Double,
        longitude: Double,
        eventName: String,
        description: String
    ) {

        val client = OkHttpClient()
        val json = JSONObject()
        json.put("datetime", datetimeReal)
        json.put("base64_image", base64Image)
        json.put("latitude", latitude)
        json.put("longitude", longitude)
        json.put("event_name", eventName)
        json.put("description", description)
        json.put("token", InformationHolder.token)

        Log.d("mytag", "Connessione creata")

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(BASE_REGISTER_URL)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.d("mytag", "Error during API call: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val statusCode = response.code
                val isSuccess = response.isSuccessful
                val responseBody = response.body?.string() ?: ""

                Log.d("mytag", "Response: Status Code $statusCode, Success: $isSuccess, Body: $responseBody")
            }
        })
    }
}
