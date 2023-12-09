
import android.util.Log
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import org.json.JSONObject


object JoinRequestsBackend {

    fun fetchJoinRequests(token: String, eventId: Int, onSuccess: (List<String>) -> Unit, onFailure: (String) -> Unit) {
        // Construct the URL for fetching join requests for a specific event
        val url = "https://maccproject.pythonanywhere.com/invitesList/${eventId}"

        // Prepare the request
        val request = Request.Builder().url(url).build()

        // Make the network request
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
                onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                // Check the response code
                if (response.code != 200) {
                    onFailure("Request failed with code ${response.code}")
                    return
                }

                // Parse the response body as a JSON array of strings
                val joinRequestsJson = response.body?.string() ?: ""
                val joinRequestsArray = JSONArray(joinRequestsJson)

                // Convert the JSONArray to a List<String>
                val joinRequests: MutableList<String> = mutableListOf()
                for (i in 0 until joinRequestsArray.length()) {
                    val email = joinRequestsArray.getString(i)
                    joinRequests.add(email)
                }

                // Invoke the success callback
                onSuccess(joinRequests)
            }
        })
    }

    fun HandleJoinRequests(email: String, eventId: Int, accept: Int) {
        // Construct the URL for fetching join requests for a specific event
        val url = "https://maccproject.pythonanywhere.com/handleInvite/${eventId}/${email}/${accept}"
        Log.d("mytag", url)

        // Prepare the request
        val request = Request.Builder().url(url).build()

        // Make the network request
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
            }

            override fun onResponse(call: Call, response: Response) {
                // Check the response code
                if (response.code != 200) {
                    return
                }


            }
        })
    }


}
