// Assuming your package is defined as follows
package com.example.myapplication

// Other necessary imports
import Event
import EventsBackend
import LoginBackend
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
                var showBottomNavigation by remember { mutableStateOf(true) }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Set up the NavHost with the NavController and navigation graph
                    NavHost(
                        navController = navController,
                        startDestination = "loginDestination"
                    ) {
                        composable("loginDestination") {
                            // Pass the NavController to the LoginPage
                            LoginPage(navController)
                            showBottomNavigation = false
                        }
                        composable("homeDestination") { backStackEntry ->
                            // Retrieve the token from the arguments
                            val token = backStackEntry.arguments?.getString("token") ?: ""

                            // Pass the token to the HomePage
                            HomePage(navController)
                            showBottomNavigation = true
                        }
                        composable(
                            "eventDetail/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            // Retrieve the token from the arguments
                            val token = backStackEntry.arguments?.getString("token") ?: ""
                            val id = backStackEntry.arguments?.getInt("id") ?: 0

                            // Pass the token to the HomePage
                            eventDetail(navController, id)
                            showBottomNavigation = true
                        }
                        composable("registerDestination") {
                            // Implement your RegisterPage composable here
                            RegisterPage(navController)
                            showBottomNavigation = false

                        }
                        composable("mapsDestination") {
                            //                        MapPage(navController)
                        }
                        composable("profileDestination") {
                            //                        ProfilePage(navController)
                        }
                        composable("settingsDestination") {
                            //                        SettingsPage(navController)
                        }
                    }

                    // Set up the BottomNavigation
                    if (showBottomNavigation) BottomNavigation(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                        backgroundColor = Color.White
                    ) {
                        // Home Page
                        BottomNavigationItem(
                            selected = navController.currentDestination?.route == "home",
                            onClick = {
                               navController.navigate("homeDestination")
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = {
                                Text(text = "Home")
                            }
                        )

                        // Map Page
                        BottomNavigationItem(
                            selected = navController.currentDestination?.route == "map",
                            onClick = {
                                //                            TODO: importare token e far sì che venga passato
                                //                            navController.navigate("mapsDestination")
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "Map"
                                )
                            },
                            label = {
                                Text(text = "Map")
                            }
                        )

                        // Profile Page
                        BottomNavigationItem(
                            selected = navController.currentDestination?.route == "profile",
                            onClick = {
                                //                            TODO: importare token e far sì che venga passato
                                //                            navController.navigate("profileDestination")
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile"
                                )
                            },
                            label = {
                                Text(text = "Profile")
                            }
                        )

                        // Settings Page
                        BottomNavigationItem(
                            selected = navController.currentDestination?.route == "settings",
                            onClick = {
                                //                            TODO: importare token e far sì che venga passato
                                //                            navController.navigate("settingsDestination")
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = {
                                Text(text = "Settings")
                            }
                        )
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
    var errorMessage by remember { mutableStateOf<String?>(null) }

    MaterialTheme(
        typography = Typography(),
        content = {
            // Create a Box with a custom background color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Utility.bootstrapLight) // Custom background color
            ) {
                // Create a Card with elevation and rounded corners
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = 8.dp,
                    backgroundColor = Color.White
                ) {
                    // Create a Column layout for the content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        //DA RIMUOVERE

                        Button(
                            onClick = {
                                TokenHolder.token = "47358c79536a33cc29477bc094cf79fed4ec6ac242b37e88c34b906679c307b2"
                                navController.navigate("homeDestination")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Utility.bootstrapBlue) // Custom primary color
                        ) {
                            Text("SKIP login")
                        }

                        //DA RIMUOVERE SOPRA

                        if (errorMessage != null) {
                            Utility.ErrorSnackbar(errorMessage = errorMessage)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

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
                                errorMessage = null
                                LoginBackend.login(username, password) { token ->
                                    if (token != null) {
                                        Log.d("mytag", "ho il token!")
                                        Log.d("mytag", "token: " + token.toString())
                                        savedToken = token

                                        // Navigate to the home destination with the obtained token
                                        // Use the MainScope to navigate on the main thread
                                        MainScope().launch {
                                            // Navigate to the home destination with the obtained token
                                            TokenHolder.token = token
                                            navController.navigate("homeDestination")
                                        }
                                    } else {
                                        Log.d("mytag", "NON ho il token!")
                                        errorMessage = "Login failed. Please check your credentials."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Utility.bootstrapBlue) // Custom primary color
                        ) {
                            Text("Login")
                        }

                        Spacer(modifier = Modifier.height(64.dp))

                        Text(
                            text = "Are you new here?",
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        // Create a "Register" button
                        Button(
                            onClick = {
                                navController.navigate("registerDestination")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Utility.bootstrapSecondary) // Custom color for registration button
                        ) {
                            Text("Register")
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun HomePage(navController: NavHostController) {
    // Define mutable state variable to hold events data
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var token = TokenHolder.token


    // Fetch events data from the backend using the provided token
    LaunchedEffect(token) {
        // Make a network request to fetch events data
        // Replace this with your actual API call to retrieve events
        EventsBackend.fetchEvents(token) { result ->
            result.onSuccess { eventsData ->
                events = eventsData
            }
            result.onFailure { error ->
                errorMessage = "Failed to fetch events: ${error.localizedMessage}"
            }
        }
    }

    // Utilize a Surface to contain the content of the home page
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Utility.bootstrapDark // A darker shade of blue-gray
    ) {
        // Utilize Column to organize the content in a column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Utilize TopAppBar for a stylized top bar
            TopAppBar(
                title = {
                    Text(
                        text = "Home Page",
                        style = MaterialTheme.typography.h6,
                        color = Color.White
                    )
                },
                backgroundColor = Utility.bootstrapSecondary // A darker shade of blue-gray
            )

            // Add spacing
            Spacer(modifier = Modifier.height(16.dp))

            // Display error message as a Snackbar
            errorMessage?.let {
                Utility.ErrorSnackbar(errorMessage = errorMessage)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Display events in a list
            if (events.isNotEmpty()) {
                Column {
                    events.forEach { event ->
                        var id = event.id
                        EventCard(
                            event = event,
                            onClick = { navController.navigate("eventDetail/$id") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else {
                Text(
                    text = "No events available.",
                    style = MaterialTheme.typography.body1,
                    color = Color.White
                )
            }
        }
    }
}

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
        // Add content to the card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display event details
            Text(
                text = "Event: ${event.name}",
                style = MaterialTheme.typography.body1,
                color = Color.White
            )

            Text(
                text = "Location: ${event.location}",
                style = MaterialTheme.typography.body1,
                color = Color.White
            )

            Text(
                text = "Date: ${event.date}",
                style = MaterialTheme.typography.body1,
                color = Color.White
            )

            Text(
                text = "Organizer: ${event.organizerName}",
                style = MaterialTheme.typography.body1,
                color = Color.White
            )
        }
    }
}

@Composable
fun RegisterPage(navController: NavHostController) {
    // Define mutable state variables to hold registration details
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Create a Box with a custom background color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Utility.bootstrapLight) // Custom background color
    ) {
        // Create a Card with elevation and rounded corners
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = 8.dp,
            backgroundColor = Color.White
        ) {
            // Create a Column layout for the content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Display error message as a Snackbar
                errorMessage?.let {
                    Utility.ErrorSnackbar(errorMessage = errorMessage)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Display a "Register" title with custom typography
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Create input fields for registration details
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Create a "Register" button with a custom color
                Button(
                    onClick = {
                        errorMessage = null
                        if (password == confirmPassword) {
                            // Call the registration logic (replace with your actual registration logic)
                            RegistrationBackend.register(email, name, password) { token ->
                                if (token != null) {
                                    // Navigate to the home destination after successful registration
                                    MainScope().launch {
                                        TokenHolder.token = token
                                        navController.navigate("homeDestination")
                                    }
                                } else {
                                    errorMessage = "Have you inserted a valid email? If you are already registered go back to login"
                                }
                            }
                        } else {
                            errorMessage = "Passwords do not match."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Utility.bootstrapBlue) // Custom color for the button
                ) {
                    Text("Register")
                }
                Spacer(modifier = Modifier.height(64.dp))

                Button(
                    onClick = {
                        // Navigate back to the login page
                        navController.popBackStack("loginDestination", inclusive = false)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Utility.bootstrapSecondary)
                ) {
                    Text("Back to Login")
                }
            }
        }
    }
}

@Composable
fun eventDetail(navController: NavHostController, id: Int) {
    val coroutineScope = rememberCoroutineScope()
    var token = TokenHolder.token
    Column(modifier = Modifier.padding(16.dp)) {
        /* NON TOCCARE, da finire, Matteo. TODO: prendere dati avendo id evento
        Text(text = event.title, style = MaterialTheme.typography.h5)
        Image(painter = rememberImagePainter(data = event.imageUrl), contentDescription = "Event Image")
        Text(text = event.description, style = MaterialTheme.typography.body1)
         */
        Button(onClick = {
            coroutineScope.launch {
                //sendApiRequest() TODO: inserire entry in db per richiesta invito
            }
            navController.navigate("homeDestination")
        }) {
            Text("Request an Invite")
        }
        Text(text = "Token: $token")
        Text(text = "ID: $id")
    }
}
