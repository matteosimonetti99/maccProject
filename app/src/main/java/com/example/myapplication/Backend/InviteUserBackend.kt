
import android.util.Log
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import org.json.JSONObject


object InviteUserBackend {

    fun inviteUser(email: String, eventId: Int, onSuccess: (Boolean) -> Unit, onFailure: (String) -> Unit) {
        // Construct the URL for fetching join requests for a specific event
        val url = "https://maccproject.pythonanywhere.com/inviteUser/${eventId}/${email}"

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
                if (response.code == 501) {
                    onFailure("${response.code}")
                    return
                }
                if (response.code == 502) {
                    onFailure("${response.code}")
                    return
                }
                // Invoke the success callback
                onSuccess(true)
            }
        })
    }
}
