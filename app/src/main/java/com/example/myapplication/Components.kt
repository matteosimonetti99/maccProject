package com.example.myapplication

import Event
import JoinRequestsBackend
import android.graphics.Bitmap
import Invite
import InviteDetailsBackend
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.Backend.Invite
import com.example.myapplication.Backend.InviteDetailsBackend
import com.example.myapplication.Backend.InvitesBackend.fetchInviteHashFromAPI
import com.example.myapplication.DataHolders.InformationHolder
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class Components {

    companion object {

        @Composable
        fun eventCard(event: Event, onClick: () -> Unit) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(8.dp),
            ) {
                // Use Row to create a two-column layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Column for the left side (image and date)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Decode base64 string to Bitmap
                        val decodedBitmap = Utility.base64ToBitmap(event.encoded_image)

                        // Display event image using Image composable
                        if (decodedBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp) // Set the desired size for the image
                                    .clip(shape = RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    bitmap = decodedBitmap.asImageBitmap(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )

                                // Display date in the upper left corner
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                                        .padding(4.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ){
                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                                        val date = LocalDateTime.parse(event.date, formatter)

                                        val monthAbbreviation = date.month.getDisplayName(
                                            java.time.format.TextStyle.SHORT,
                                            Locale.ENGLISH
                                        )

                                        Text(

                                            text = monthAbbreviation,
                                            style = MaterialTheme.typography.h5.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            ),
                                            color = Color.Black,

                                        )
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            style = MaterialTheme.typography.h5.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            ),
                                            color = Color.Black,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Column for the right side (event details and permission button)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Display bold and larger event name
                        Text(
                            text = event.name,
                            style = MaterialTheme.typography.h5.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp
                            ),
                            color = Color.Black
                        )
                        //event desc
                        Text(
                            text = "by ${event.organizerName}",
                            style = MaterialTheme.typography.h5.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            ),
                            color = Color.Gray
                        )

                        Text(
                            text = event.description.toString(),
                            style = MaterialTheme.typography.h5.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            ),
                            color = Color.Black
                        )
                        val km = event.distance?.div(1000f)
                        Text(
                            text = "$km km away",
                            style = MaterialTheme.typography.h5.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = Color.Black
                        )
                    }
                }
            }
        }

        @Composable
        fun inviteCard(event: Event, onClick: () -> Unit) {
            var invite by remember { mutableStateOf<Invite?>(null) }

            LaunchedEffect(Unit) {
                try {
                    InviteDetailsBackend.fetchInvite(
                        token = InformationHolder.token,
                        userID = InformationHolder.userID,
                        eventID = event.id
                    ) { result ->
                        val fetchedInvite = result.getOrThrow()
                        if (fetchedInvite.inviteID != -1) {
                            invite = fetchedInvite
                        }
//                        Log.d("fetchInvite", "Fetched the invite in my invites page")

                    }
                } catch (e: Exception) {
                    Log.d("mytag", "Failed fetching the invite in my invites page")
                }
            }

            Log.d("inviteTag", invite.toString())

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(8.dp),
            ) {
                // Use Row to create a two-column layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Column for the left side (image and date)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Decode base64 string to Bitmap
                        val decodedBitmap = Utility.base64ToBitmap(event.encoded_image)

                        // Display event image using Image composable
                        if (decodedBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp) // Set the desired size for the image
                                    .clip(shape = RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    bitmap = decodedBitmap.asImageBitmap(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )

                                // Display date in the upper left corner
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                                        .padding(4.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ){
                                        Log.d("ae", event.date)
                                        //event date e null!!, non so perchè
                                        /* TODO: uncomment
                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                                        val date = LocalDateTime.parse(event.date, formatter)

                                        val monthAbbreviation = date.month.getDisplayName(
                                            java.time.format.TextStyle.SHORT,
                                            Locale.ENGLISH
                                        )

                                        Text(

                                            text = monthAbbreviation,
                                            style = MaterialTheme.typography.h5.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            ),
                                            color = Color.Black,

                                            )
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            style = MaterialTheme.typography.h5.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            ),
                                            color = Color.Black,
                                        )
*/
                                    }
                                }
                            }
                        }
                    }

                    // Column for the right side (event details and permission button)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Display bold and larger event name
                        Text(
                            text = event.name,
                            style = MaterialTheme.typography.h5.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp
                            ),
                            color = Color.Black
                        )
                        //event desc
                        Text(
                            text = event.description.toString(),
                            style = MaterialTheme.typography.h5.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            ),
                            color = Color.Black
                        )
                        val km = event.distance?.div(1000f)
                        Text(
                            text = "$km km away",
                            style = MaterialTheme.typography.h5.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = Color.Black
                        )

                        Text(
                            text = event.name,
                            style = MaterialTheme.typography.h5.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )

                        // Display the status button
                        Log.d("inviteTag", invite.toString())

                        invite?.let { StatusButton(invite!!) }

                    }
                }
            }

        }

        @Composable
        fun requestInviteButton()  {
            // Display button
            Button(
                onClick = {
                    //todo: requestInviteButton
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(color = Color.Cyan, shape = RoundedCornerShape(4.dp))
            )
            {
                Text(text = "request invite")
            }

        }

        @Composable
        fun StatusButton(invite: Invite) {
            var showQRCode by remember { mutableStateOf(false) }
            var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

            val buttonColor = getButtonColor(invite.status)

            if (showQRCode) {

                LaunchedEffect(invite.inviteID) {
                    try {
                        // Use the generateQRCodeBitmap function with the onResult callback
                        generateQRCodeBitmap(invite.inviteID.toString(), size = 200) { result ->
                            if (result.isSuccess) {
                                // Update the qrCodeBitmap state with the fetched bitmap
                                qrCodeBitmap = result.getOrThrow()
                            } else {
                                // Handle the failure, log the error, or perform other actions
                                Log.e("QRcode", "Error fetching QR code: ${result.exceptionOrNull()?.message}", result.exceptionOrNull())
                            }
                        }
                    } catch (e: Exception) {
                        // Handle exceptions outside of the callback
                        Log.e("QRcode", "Error fetching QR code: ${e.message}", e)
                    }
                }

                if (showQRCode) {
                    AlertDialog(
                        onDismissRequest = {
                            // Close the dialog when onDismissRequest is called
                            showQRCode = false
                        },
                        title = { Text(text = "QR Code") },
                        text = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(qrCodeBitmap),
                                    contentDescription = null,
                                    modifier = Modifier.size(150.dp)
                                )
                            }
                        },
                        buttons = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = {
                                    // Close the dialog when the button is clicked
                                    showQRCode = false
                                }) {
                                    Text("Close")
                                }
                            }
                        }
                    )
                }
            }

            // Display button
            Button(
                onClick = {
                    // Handle button click
                    if (invite.status == "accepted") {
                        showQRCode = true
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(buttonColor, shape = RoundedCornerShape(4.dp))
            ) {
                if (invite.status == "accepted") {
                    Text(text = "get the QR code", color = Color.White)

                }
                else {
                    Text(text = invite.status, color = Color.White)
                }
            }
        }

        private fun getButtonColor(status: String): Color {
            return when (status.lowercase(Locale.getDefault())) {
                "accepted" -> Color.Green
                "pending" -> Color.Yellow
                else -> Color.Gray
            }
        }


        // Function to generate a QR code bitmap using ZXing
        fun generateQRCodeBitmap(inviteID: String, size: Int, onResult: (Result<Bitmap>) -> Unit) {

            // Fetch the SHA-256 hash of the invite from the Flask API
            fetchInviteHashFromAPI(inviteID) { hashResult ->
                if (hashResult.isSuccess) {
                    try {
                        val inviteHash = hashResult.getOrThrow()

                        // Generate QR code based on the retrieved SHA-256 hash
                        val writer = QRCodeWriter()
                        val bitMatrix = writer.encode(inviteHash, BarcodeFormat.QR_CODE, size, size)
                        val width = bitMatrix.width
                        val height = bitMatrix.height
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                        for (x in 0 until width) {
                            for (y in 0 until height) {
                                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb())
                            }
                        }

                        onResult(Result.success(bitmap))
                    } catch (e: Exception) {
                        // Handle exceptions as needed
                        onResult(Result.failure(e))
                    }
                } else {
                    // Propagate the failure from fetchInviteHashFromAPI
                    onResult(Result.failure(hashResult.exceptionOrNull() ?: Exception("Failed to fetch invite hash")))
                }
            }
        }


        @Composable
        fun ErrorSnackbar(
            errorMessage: String?,
            modifier: Modifier = Modifier
        ) {
            Snackbar(
                modifier = modifier
                    .absoluteOffset(y = -16.dp) // Adjust the offset as needed
                    .fillMaxWidth(),
                action = {
                    // Add an action if needed
                },
                backgroundColor = Utility.bootstrapRed, // Bootstrap danger color
                elevation = 8.dp,
                contentColor = Color.White // Text color
            ) {
                Text(errorMessage ?: "Default Error Message", color = Color.White)
            }
        }
        @Composable
        fun SuccessSnackbar(
            successMessage: String?,
            modifier: Modifier = Modifier
        ) {
            Snackbar(
                modifier = modifier
                    .absoluteOffset(y = -16.dp) // Adjust the offset as needed
                    .fillMaxWidth(),
                action = {
                    // Add an action if needed
                },
                backgroundColor = Utility.bootstrapGreen, // Bootstrap danger color
                elevation = 8.dp,
                contentColor = Color.White // Text color
            ) {
                Text(successMessage ?: "Default Success Message", color = Color.White)
            }
        }
        @Composable
        fun JoinRequestItem(email: String, id: Int, navController: NavHostController) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                backgroundColor = Color.Gray, // Customize the background color
                elevation = 8.dp // Add elevation for a card-like appearance
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Email: $email", color = Color.White) // Set text color

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons for accepting and refusing the invite
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                JoinRequestsBackend.HandleJoinRequests(email,id,1)
                                navController.popBackStack()
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Accept")
                        }

                        Button(
                            onClick = {
                                JoinRequestsBackend.HandleJoinRequests(email,id,0)
                                navController.popBackStack()
                            }
                        ) {
                            Text("Refuse")
                        }
                    }
                }
            }
        }
    }
}