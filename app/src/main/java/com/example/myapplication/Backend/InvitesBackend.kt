package com.example.myapplication.Backend

import Invite
import android.util.Log
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

object InvitesBackend {

    private const val BASE_URL = "https://maccproject.pythonanywhere.com"

    // Function to fetch events data from the backend
    fun fetchInvites(token: String, userID : Int, onResult: (Result<List<Invite>>) -> Unit) {

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/invites/$userID")
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
                    val invites = parseInvites(responseBody)
                    onResult(Result.success(invites))
                } else {
                    // Handle errors from the backend
                    Log.d("invitesDebug", responseBody.toString())
                    onResult(Result.failure(IOException("Failed to fetch invites")))
                }
            }
        })
    }

    // Parse the JSON response to a list of invites
    private fun parseInvites(response: String): List<Invite> {
        val jsonArray = JSONArray(response)
        val invites = mutableListOf<Invite>()

        for (i in 0 until jsonArray.length()) {
            val inviteObject = jsonArray.optJSONObject(i)
            val invite = Invite(
                inviteID = inviteObject.optInt("inviteID"),
                userID = inviteObject.optInt("userID"),
                eventID = inviteObject.optInt("eventID"),
                status = inviteObject.optString("status")

            )
            Log.d("invitesDebug", "invite: $invite")

            invites.add(invite)
        }

        return invites
    }
}
