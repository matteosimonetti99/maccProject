
import android.location.Location
import android.util.Log
import com.example.myapplication.DataHolders.PositionHolder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

data class Event(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val date: String,
    val organizerName: String,
    val encoded_image: String,
    val description: String?,
    val distance: Float?
)

object EventDetailsBackend {

    private const val BASE_URL = "https://maccproject.pythonanywhere.com"

    // Function to fetch event details from the backend
    fun fetchEventDetails(token: String, eventId: Int, onResult: (Result<Event>) -> Unit) {

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/events/$eventId")
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
                Log.d("mytag", "Response Body: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    val event = parseEvent(responseBody, eventId)
                    onResult(Result.success(event))
                } else {
                    // Handle errors from the backend
                    Log.d("mytag", responseBody.toString())
                    onResult(Result.failure(IOException("Failed to fetch event details")))
                }
            }
        })
    }

    // Parse the JSON response to an event
    private fun parseEvent(response: String, idEvento: Int): Event {
        val eventObject = JSONObject(response)

        val latitude = eventObject.optDouble("latitude")
        val longitude = eventObject.optDouble("longitude")

        val res= FloatArray(1);
        Location.distanceBetween(latitude, longitude, PositionHolder.lastPostion.latitude, PositionHolder.lastPostion.longitude, res)

        val distanceInMeters = res[0]

        return Event(
            id = idEvento,
            name = eventObject.optString("name"),
            latitude = eventObject.optDouble("latitude"),  // Assuming latitude is a double value
            longitude = eventObject.optDouble("longitude"),
            date = eventObject.optString("datetime"),
            organizerName = eventObject.optString("organizer_name"),
            encoded_image = eventObject.optString("encoded_image"),
            description = eventObject.optString("description", null), // Assuming description can be null
            distance = distanceInMeters
        )
    }
}
