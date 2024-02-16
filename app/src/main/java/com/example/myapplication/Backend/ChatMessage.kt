
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

// Data class for chat messages. Adjust according to your actual data structure.
data class ChatMessage(
    val id: Int,
    val userId: Int,
    val userName: String,
    val message: String,
    val timestamp: String // Consider using a proper date/time type
)

object ChatRepository {

    private const val BASE_URL = "https://maccproject.pythonanywhere.com"

    fun getMessages(eventId: Int, token: String, onResult: (Result<List<ChatMessage>>) -> Unit) {

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/api/events/$eventId/messages")
            .header("Authorization", token)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) onResult(Result.failure(IOException("Failed to fetch chat messages")))
                    Log.d("ChatDebug", "superato il controllo")
                    val responseBody = it.body?.string()
                    Log.d("ChatDebug", "superato il controllo 2")
                    val messages = parseMessages(responseBody ?: "")
                    Log.d("ChatDebug", "messages: $messages")
                    onResult(Result.success(messages))
                }
            }
        })
    }

    fun postMessage(eventId: Int, userId: Int, message: String, token: String, onResult: (Result<Unit>) -> Unit) {

        val client = OkHttpClient()

        val json = JSONObject()
            .put("user_id", userId)
            .put("message", message)
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/api/events/$eventId/messages")
            .header("Authorization", token)
            .post(json)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onResult(Result.success(Unit))
                } else {
                    Log.d("ChatDebug", response.body?.string() ?: "")
                    onResult(Result.failure(IOException("Failed to post chat message")))
                }
            }
        })
    }

    private fun parseMessages(json: String): List<ChatMessage> {
        Log.d("ChatDebug", "json: $json")

        val jsonArray = JSONArray(json)
        val messages = mutableListOf<ChatMessage>()

        Log.d("ChatDebug", "jsonArray: $jsonArray")
        Log.d("ChatDebug", "messages: ${messages}")

        for (i in 0 until jsonArray.length()) {
            jsonArray.optJSONObject(i)?.let {
                val message = ChatMessage(
                    id = it.getInt("id"),
                    userId = it.getInt("user_id"), // Changed from "userId" to "user_id"
                    userName = it.getString("user_name"), // Changed from "userName" to "user_name"
                    message = it.getString("message"),
                    timestamp = it.getString("timestamp")
                )

                Log.d("ChatDebug", "message: $message")
                messages.add(message)
            }
        }

        Log.d("ChatDebug", "Fatto!")

        return messages
    }
}


