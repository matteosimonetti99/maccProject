
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.myapplication.Backend.InvitesBackend
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Composable function for the Barcode Scanner App
@SuppressLint("StaticFieldLeak")
object BarcodeScannerAppObject {

    // Executor service for camera operations
    private lateinit var cameraExecutor: ExecutorService
    // Application context
    private lateinit var context: Context
    // Array of required camera permissions
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    // Composable function for the Barcode Scanner App
    @OptIn(ExperimentalGetImage::class) @Composable
    fun BarcodeScannerApp(navController: NavHostController, context: Context, eventID: Int) {
        // State variables for camera preview, QR code detection, and detected QR code value
        var previewView by remember { mutableStateOf<PreviewView?>(null) }
        var qrCodeDetected by remember { mutableStateOf(false) }
        var flag by remember { mutableStateOf(false) }
        var detectedQRCodeValue by remember { mutableStateOf<String?>(null) }
        var res by remember { mutableStateOf(0) }
        var borderColor by remember { mutableStateOf(Color.Red) }
        var text by remember { mutableStateOf<String>("Not valid") }

        // Initialize context and executor
        this.context = context
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Launched effect to handle camera initialization
        LaunchedEffect(previewView) {
            Log.d("MyCamera", "LaunchedEffect called.")
            // Get the camera provider instance
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Configure the camera preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView!!.surfaceProvider)
                }

                // Configure the image analysis for QR code detection
                val imageAnalysis = ImageAnalysis.Builder().build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        // Use ML Kit to process the image and detect QR codes
                        val scanner = BarcodeScanning.getClient()
                        val inputImage =
                            InputImage.fromMediaImage(
                                imageProxy.image!!,
                                imageProxy.imageInfo.rotationDegrees
                            )

                        scanner.process(inputImage)
                            .addOnSuccessListener { barcode ->
                                if (!flag && barcode.isNotEmpty() && barcode[0].valueType == Barcode.TYPE_TEXT) {
                                    // If a QR code is detected, handle the result
                                    flag=true
                                    Log.d(
                                        "MyCamera",
                                        "QR Code Detected: ${barcode[0].displayValue}"
                                    )

                                    detectedQRCodeValue = barcode[0].displayValue

                                    // Check the QR code with the backend

                                    InvitesBackend.checkQRcode(eventID, detectedQRCodeValue!!) { result ->
                                        result.onSuccess { result ->
                                            Log.d("checkQRcode", "$result")

                                            // If the QR code is valid, set the flag
                                            qrCodeDetected = true
                                            res=result
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
                                // Close the image proxy when processing is complete
                                imageProxy.close()
                            }
                    }
                }

                try {
                    // Unbind existing camera instances and bind to the lifecycle
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

        // Compose the UI
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (!qrCodeDetected) {
                // If QR code is not detected, show camera preview
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
                // If QR code is detected, show relevant UI
                //Text("QR Code Detected: $detectedQRCodeValue and $res")

                if (res==0) {
                    text="Not valid"
                    borderColor=Color.Red
                }
                else if (res==1) {
                    text="Already used"
                    borderColor=Color(1.0f, 0.647f, 0.0f)
                }
                else {
                    text="Valid"
                    borderColor=Color(0xFF228B22)
                }
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(0.dp, 0.dp, 0.dp, 60.dp)



                    ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .border(BorderStroke(10.dp, borderColor)),


                    contentAlignment = Alignment.Center
                    ) {
                        Text(text = text, color = Color.Black, fontSize = 24.sp)

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.align(Alignment.BottomCenter).padding(0.dp, 0.dp, 0.dp, 30.dp)
                        ) {
                            Text("Go back")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
    }
}
