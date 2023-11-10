import android.util.Log
import okhttp3.*
import java.io.IOException

object TestConnection {

    private const val BASE_URL = "https://maccproject.pythonanywhere.com/test"

    // Funzione per testare la connessione al backend
    fun testConnection(onResult: (Boolean) -> Unit) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(BASE_URL)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                e.printStackTrace()
                // Gestione dell'errore di connessione
                onResult(false)

            }

            override fun onResponse(call: Call, response: Response) {
                // Verifica se la risposta è di successo (status code 200)
                val isSuccessful = response.isSuccessful

                // Passa il risultato al chiamante
                onResult(isSuccessful)

                Log.d("mytag", "funziona!")

            }
        })
    }
}
