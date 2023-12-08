package com.example.myapplication.Backend

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

data class Invite(
    val inviteID: Int,
    val userID: Int,
    val eventID : Int,
    val status : String
)

object InviteDetailsBackend {

    private const val BASE_URL = "https://maccproject.pythonanywhere.com"

    // Function to fetch invites data from the backend based on user ID and event ID
    fun fetchInvite(token: String, userID: Int, eventID: Int, onResult: (Result<Invite>) -> Unit) {

        Log.d("inviteDetailDebug", "token = $token \n userID = $userID, eventID = $eventID")

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/invites/$userID/$eventID")
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
                Log.d("invitesDebug", responseBody.toString())

                if (response.isSuccessful && responseBody != null) {
                    if (responseBody.trim().isNotEmpty()) { // Check for non-empty response
                        try {
                            val invite = parseInvite(responseBody)
                            onResult(Result.success(invite))
                        } catch (e: Exception) {
                            Log.e("invitesDebug", "Error parsing invite", e)
                            onResult(Result.failure(e))
                        }
                    } else {
                        // Handle empty response
                        Log.e("invitesDebug", "Empty response body")
                        onResult(Result.failure(IOException("Empty response body")))
                    }
                } else {
                    // Handle errors from the backend
                    Log.e("invitesDebug", "Failed to fetch invites. Response code: ${response.code}")
                    onResult(Result.failure(IOException("Failed to fetch invites")))
                }
            }
        })
    }

    private fun parseInvite(response: String): Invite {
        try {
            val inviteObject = JSONObject(response)

            Log.d("invitesDebug", "invite: $inviteObject")

            return Invite(
                inviteID = inviteObject.optInt("inviteID"),
                userID = inviteObject.optInt("userID"),
                eventID = inviteObject.optInt("eventID"),
                status = inviteObject.optString("status")
            )
        } catch (e: JSONException) {
            // Handle JSON parsing error
            throw IOException("Failed to parse invite")
        }
    }
}
