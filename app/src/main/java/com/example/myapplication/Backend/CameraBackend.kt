import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.myapplication.Backend.InvitesBackend
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Composable function for the Barcode Scanner App
object BarcodeScannerAppObject {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var context: Context
    private const val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    @Composable
    fun BarcodeScannerApp(context: Context) {
        var previewView by remember { mutableStateOf<PreviewView?>(null) }
        var qrCodeDetected by remember { mutableStateOf(false) }
        var detectedQRCodeValue by remember { mutableStateOf<String?>(null) }

        this.context = context
        cameraExecutor = Executors.newSingleThreadExecutor()

        LaunchedEffect(previewView) {
            Log.d("MyCamera", "LaunchedEffect called.")
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView!!.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder().build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        val scanner = BarcodeScanning.getClient()
                        val inputImage =
                            InputImage.fromMediaImage(
                                imageProxy.image!!,
                                imageProxy.imageInfo.rotationDegrees
                            )

                        scanner.process(inputImage)
                            .addOnSuccessListener { barcode ->
                                if (barcode.isNotEmpty() && barcode[0].valueType == Barcode.TYPE_TEXT) {
                                    Log.d(
                                        "MyCamera",
                                        "QR Code Detected: ${barcode[0].displayValue}"
                                    )

                                    detectedQRCodeValue = barcode[0].displayValue

                                    InvitesBackend.checkQRcode(4, detectedQRCodeValue!!) { result ->
                                        result.onSuccess { result ->

                                            Log.d("checkQRcode", "$result")

                                            if (result == true) {
                                                qrCodeDetected = true
                                            }

                                            //todo: Aggiungi qui che l'invito viene rimosso dal db

                                        }
                                        result.onFailure { error ->
                                            Log.d(
                                                "checkQRcode",
                                                "Failed to fetch invite details: ${error.localizedMessage}"
                                            )
                                        }
                                    }

                                }
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        context as androidx.lifecycle.LifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                    Log.d("MyCamera", "Camera bound to lifecycle.")
                } catch (exc: Exception) {
                    Log.e("MyCamera", "Error binding camera to lifecycle: ${exc.message}")
                }
            }, ContextCompat.getMainExecutor(context))
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (!qrCodeDetected) {
                AndroidView(
                    modifier = Modifier.fillMaxSize().background(Color.Black),
                    factory = { context ->
                        val view = PreviewView(context).apply {
                            id = View.generateViewId()
                        }
                        previewView = view
                        Log.d("MyCamera", "Camera preview view created.")
                        view
                    }
                )
            } else {

                Text("QR Code Detected: $detectedQRCodeValue")

                Text("Benvenuto!")

                //TODO:Aggiungi pulsante per tornare alla home
            }
        }
    }
}
