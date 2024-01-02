package com.example.myapplication

import Event
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        invite = fetchedInvite
                    }
                } catch (e: Exception) {
                    Log.d("mytag", "Failed fetching the invite")
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
                                        /*
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
                        invite?.let { StatusButton(invite!!.status) }
                    }
                }
            }

        }

        @Composable
        fun StatusButton(status: String) {
            Button(
                onClick = { /* Handle button click if needed */ },
                modifier = Modifier
                    .padding(8.dp)
                    .background(getButtonColor(status), shape = RoundedCornerShape(4.dp))
            ) {
                Text(text = status, color = Color.White)
            }
        }

        private fun getButtonColor(status: String): Color {
            return when (status.lowercase(Locale.getDefault())) {
                "accepted" -> Color.Green
                "pending" -> Color.Yellow
                else -> Color.Gray
            }
        }
    }
}