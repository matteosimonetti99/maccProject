package com.example.myapplication.Backend

import android.util.Log
import com.example.myapplication.DataHolders.InformationHolder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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

    // Function to fetch invite hash from the Flask server
    fun fetchInviteHashFromAPI(inviteID: String, onResult: (Result<String>) -> Unit) {

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/getqr/$inviteID")
            .header("Authorization", InformationHolder.token)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                // Handle connection failure
                onResult(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("QRcode", "Response body: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val qrCode = jsonObject.getString("qr_code")
                        Log.d("QRcode", "QR Code: $qrCode")
                        onResult(Result.success(qrCode))
                    } catch (e: JSONException) {
                        Log.e("QRcode", "Failed to parse JSON response", e)
                        onResult(Result.failure(e))
                    }
                } else {
                    // Handle errors from the Flask server
                    Log.e("QRcode", "Unsuccessful response: ${response.code}")
                    onResult(Result.failure(IOException("Failed to fetch invite hash")))
                }
            }
        })
    }

    /*fun markInviteAsUsed(inviteID: Int, onResult: (Result<Boolean>) -> Unit) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/markInviteAsUsed/$inviteID")
            .header("Authorization", InformationHolder.token)
            .post("".toRequestBody(null)) // Empty body for POST request
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle connection failure
                onResult(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("markInviteAsUsed", "Response body: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val message = jsonObject.getString("message")
                        Log.d("markInviteAsUsed", "Message: $message")
                        onResult(Result.success(true))
                    } catch (e: JSONException) {
                        Log.e("markInviteAsUsed", "Failed to parse JSON response", e)
                        onResult(Result.failure(e))
                    }
                } else {
                    // Handle errors from the Flask server
                    Log.e("markInviteAsUsed", "Unsuccessful response: ${response.code}")
                    onResult(Result.failure(IOException("Failed to mark invite as used")))
                }
            }
        })
    }*/

    fun requestAnInvite(eventID: Int, inviteeID: Int, onResult: (Result<Boolean>) -> Unit) {
        val client = OkHttpClient()

        val jsonBody = JSONObject().apply {
            put("event_id", eventID)
            put("invitee_id", inviteeID)
        }
        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$BASE_URL/createInvite")
            .header("Authorization", InformationHolder.token)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle connection failure
                onResult(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("requestAnInvite", "Response body: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val message = jsonObject.getString("message")
                        Log.d("requestAnInvite", "Message: $message")
                        onResult(Result.success(true))
                    } catch (e: JSONException) {
                        Log.e("requestAnInvite", "Failed to parse JSON response", e)
                        onResult(Result.failure(e))
                    }
                } else {
                    // Handle errors from the Flask server
                    Log.e("requestAnInvite", "Unsuccessful response: ${response.code}")
                    onResult(Result.failure(IOException("Failed to request an invite")))
                }
            }
        })
    }

    fun checkQRcode(eventID: Int, qrCode: String, onResult: (Result<Int>) -> Unit) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/checkqr/$eventID/$qrCode")
            .header("Authorization", InformationHolder.token)
            .get()
            .build()
        Log.d("checkQrCode", "$BASE_URL/checkqr/$eventID/$qrCode")


        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                // Handle connection failure
                onResult(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("checkQRCode", "Response body: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val isCorrect = jsonObject.getInt("result")
                        Log.d("checkQRCode", "isCorrect: $isCorrect")
                        onResult(Result.success(isCorrect))
                    } catch (e: JSONException) {
                        Log.e("checkQRCode", "Failed to parse JSON response", e)
                        onResult(Result.failure(e))
                    }
                } else {
                    // Handle errors from the Flask server
                    Log.e("checkQRCode", "Unsuccessful response: ${response.code}")
                    onResult(Result.failure(IOException("Failed to check invite hash")))
                }
            }
        })
    }
}
