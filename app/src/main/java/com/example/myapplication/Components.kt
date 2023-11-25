package com.example.myapplication

import Event
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
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Components {

    companion object {

        @Composable
        fun EventCard(event: Event, onClick: () -> Unit) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(8.dp),
                elevation = 8.dp,
                backgroundColor = Utility.bootstrapInfo // A lighter shade of blue-gray
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
                                    Text(
                                        text = event.date,
                                        style = MaterialTheme.typography.caption,
                                        color = Color.Black,
                                    )
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
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )


                        Text(
                            text = "${event.organizerName}",
                            style = MaterialTheme.typography.body1,
                            color = Color.White
                        )

                        // Button to ask for permission to participate
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(Color.Black)
                        ) {
                            Text(text = "Request an invite")
                        }
                    }
                }
            }
        }
    }
}