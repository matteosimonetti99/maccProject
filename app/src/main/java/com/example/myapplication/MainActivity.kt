// Assuming your package is defined as follows
package com.example.myapplication

// Other necessary imports
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Use the default MaterialTheme
            MaterialTheme {
                // Create a NavController
                val navController = rememberNavController()

                // Set up the NavHost with the NavController and navigation graph
                NavHost(
                    navController = navController,
                    startDestination = "loginDestination"
                ) {
                    composable("loginDestination") {
                        // Pass the NavController to the LoginPage
                        LoginPage(navController)
                    }
                    composable(
                        "homeDestination/{token}",
                        arguments = listOf(navArgument("token") { type = NavType.StringType })
                    ) { backStackEntry ->
                        // Retrieve the token from the arguments
                        val token = backStackEntry.arguments?.getString("token") ?: ""

                        // Pass the token to the HomePage
                        HomePage(token)
                    }
                }
            }
        }
    }
}

@Composable
fun LoginPage(navController: NavHostController) {
    // Define two mutable state variables to hold username and password
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Dichiarazione della variabile di stato per tracciare l'autenticazione
    var savedToken by remember { mutableStateOf("") }

    MaterialTheme(
        typography = Typography(),
        content = {
            // Create a Box with a custom background color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0E0E0)) // Custom background color
            ) {
                // Create a Card with elevation and rounded corners
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = 8.dp
                ) {
                    // Create a Column layout for the content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Display a "Login" title with custom typography
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Create an input field for the username with a label
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )

                        // Create an input field for the password with a label and password visual transformation
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )

                        // Create a "Login" button with a custom primary color
                        Button(
                            onClick = {
                                LoginBackend.login(username, password) { token ->
                                    if (token != null) {
                                        Log.d("mytag", "ho il token!")
                                        Log.d("mytag", "token: " + token.toString())
                                        savedToken = token

                                        // Navigate to the home destination with the obtained token
                                        // Use the MainScope to navigate on the main thread
                                        MainScope().launch {
                                            // Navigate to the home destination with the obtained token
                                            navController.navigate("homeDestination/$token")
                                        }
                                    } else {
                                        Log.d("mytag", "NON ho il token!")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1976D2)) // Custom primary color
                        ) {
                            Text("Login")
                        }
                    }
                }
            }
        }
    )
}



@Composable
fun HomePage(token: String) {
    // Utilizza un Surface per contenere il contenuto della home page
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF607D8B) // A darker shade of blue-gray
    ) {
        // Utilizza Column per organizzare il contenuto in una colonna
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Utilizza TopAppBar per una barra superiore stilizzata
            TopAppBar(
                title = {
                    Text(
                        text = "Home Page",
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFFE0E0E0)
                    )
                },
                backgroundColor = Color(0xFF455A64) // A darker shade of blue-gray
            )

            // Aggiunge uno spazio
            Spacer(modifier = Modifier.height(16.dp))

            // Utilizza una Card per un contenitore stilizzato
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = 8.dp,
                backgroundColor = Color(0xFF78909C) // A lighter shade of blue-gray
            ) {
                // Aggiunge il contenuto della Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Utilizza un'icona Material Design
                    Icon(
                        imageVector = Icons.Rounded.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFFE0E0E0)
                    )

                    // Aggiunge spaziatura
                    Spacer(modifier = Modifier.height(16.dp))

                    // Aggiunge testo di benvenuto
                    Text(
                        text = "Welcome to the Home Page!",
                        style = MaterialTheme.typography.body1,
                        color = Color(0xFFE0E0E0)
                    )

                    // Aggiunge spaziatura
                    Spacer(modifier = Modifier.height(16.dp))

                    // Visualizza il token
                    Text(
                        text = "Token: $token",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFFE0E0E0)
                    )
                }
            }
        }
    }
}
