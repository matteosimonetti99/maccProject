package com.example.myapplication

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object RegistrationBackend {

    private const val BASE_REGISTER_URL = "https://maccproject.pythonanywhere.com/register"

    // Funzione per effettuare registrazione e ottenere un token
    fun register(username: String, name: String, password: String, onResult: (String?) -> Unit) {

        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()
        val trimmedName = name.trim()

        val client = OkHttpClient()
        val json = JSONObject()
        json.put("username", trimmedUsername)
        json.put("name", trimmedName)
        json.put("password", trimmedPassword)

        Log.d("mytag", "Connessione creata")

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(BASE_REGISTER_URL)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                Log.d("mytag", "risposta NON ricevuta")

                // Gestione dell'errore di connessione
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {

                Log.d("mytag", "risposta ricevuta")

                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    val token = jsonResponse.optString("token", null)

                    // Passa il token al chiamante
                    onResult(token)
                } else {
                    // Gestione degli errori dal backend
                    Log.d("mytag", responseBody.toString())
                    onResult(null)
                }
            }
        })
    }
}
