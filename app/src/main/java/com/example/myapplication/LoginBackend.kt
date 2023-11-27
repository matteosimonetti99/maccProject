import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object LoginBackend {

    private const val BASE_LOGIN_URL = "https://maccproject.pythonanywhere.com/login"

    // Funzione per effettuare il login e ottenere un token
    fun login(username: String, password: String, onResult: (String?, Int?, String?) -> Unit) {

        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()

        val client = OkHttpClient()
        val json = JSONObject()
        json.put("username", trimmedUsername)
        json.put("password", trimmedPassword)

        Log.d("mytag", "Connessione creata")

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(BASE_LOGIN_URL)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                Log.d("mytag", "risposta NON ricevuta")
                onResult(null, null, null)
            }

            override fun onResponse(call: Call, response: Response) {

                Log.d("mytag", "risposta ricevuta")

                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    val token = jsonResponse.optString("token", null)
                    val role = jsonResponse.optString("role", null)
                    val userID = jsonResponse.optInt("user_id", -1)

                    // Passa il token al chiamante
                    onResult(token, userID, role)
                } else {
                    // Gestione degli errori dal backend
                    Log.d("mytag", responseBody.toString())
                    onResult(null, null, null)
                }
            }
        })
    }
}
