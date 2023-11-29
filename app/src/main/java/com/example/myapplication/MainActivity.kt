// Assuming your package is defined as follows
package com.example.myapplication

// Other necessary imports
import Event
import EventDetailsBackend
import EventsBackend
import Invite
import InvitesBackend
import LoginBackend
import android.annotation.SuppressLint
import android.app.Activity
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateUtils.formatDateTime
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.Components.Companion.eventCard
import com.example.myapplication.Components.Companion.inviteCard
import com.google.android.gms.cast.framework.media.ImagePicker
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.window.Dialog
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import coil.compose.rememberImagePainter
import com.example.myapplication.PositionHolder
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import android.content.ContentResolver
import androidx.core.content.ContentProviderCompat.requireContext


class MainActivity : ComponentActivity() {
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                PositionHolder.UpdateLastPosition(applicationContext.applicationContext,this@MainActivity);
            } else {
                // L'utente ha negato il permesso di localizzazione
                // Puoi gestire questo caso di conseguenza
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Use the default MaterialTheme
            MaterialTheme {
                // Create a NavController
                val navController = rememberNavController()
                var showBottomNavigation by remember { mutableStateOf("") }

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
                            showBottomNavigation = ""
                        }
                        composable("homeDestination") { backStackEntry ->
                            HomePage(navController)
                            showBottomNavigation = "user"
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

                            eventDetail(navController, id)
                        }
                        composable("registerDestination") {
                            // Implement your RegisterPage composable here
                            RegisterPage(navController)
                        }
                        composable("mapsDestination") {
                            ComposeMap(navController, this@MainActivity)
                        }
                        composable("myInvitesDestination") {
                            myInvitesPage(navController)
                        }



                        //Manager section
                        composable("HomePageManager") {
                            HomePageManager(navController)
                            showBottomNavigation = "manager"
                        }
                        composable("EventCreation") {
                            EventCreation(navController)
                        }
                    }


                    //USER BOTTOMBAR
                    if (showBottomNavigation=="user") BottomNavigation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        backgroundColor = Color.White
                    ) {
                        // Home Page
                        BottomNavigationItem(
                            selected = navController.currentDestination?.route == "homeDestination",
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
                            selected = navController.currentDestination?.route == "mapsDestination",
                            onClick = {
                                navController.navigate("mapsDestination")
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
                            selected = navController.currentDestination?.route == "myInvitesDestination",
                            onClick = {
                                navController.navigate("myInvitesDestination")
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "My Invites"
                                )
                            },
                            label = {
                                Text(text = "My Invites")
                            }
                        )
                    }






                    //MANAGER BOTTOMBAR
                    else if (showBottomNavigation=="manager")
                        BottomNavigation(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            backgroundColor = Color.White
                        ) {
                            // Home Page
                            BottomNavigationItem(
                                selected = navController.currentDestination?.route == "HomePageManager",
                                onClick = {
                                    navController.navigate("HomePageManager")
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
                                selected = navController.currentDestination?.route == "EventCreation",
                                onClick = {
                                    navController.navigate("EventCreation")
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Event,
                                        contentDescription = "Event"
                                    )
                                },
                                label = {
                                    Text(text = "Event Creation")
                                }
                            )

                            // Profile Page
                            BottomNavigationItem(
                                selected = navController.currentDestination?.route == "ManagerInvites",
                                onClick = {
                                    navController.navigate("ManagerInvites")
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Mail,
                                        contentDescription = "Invites"
                                    )
                                },
                                label = {
                                    Text(text = "Invites")
                                }
                            )
                        }






                }
            }

        }

    }
}

@Composable
fun ComposeMap(navController: NavHostController, activity: MainActivity) {


    var currentPosition by remember { mutableStateOf(PositionHolder.lastPostion) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var token = InformationHolder.token


    //val currentPosition = LatLng(loca)
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
    }}


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPosition, 8f)
    }

    val mapUiSettings = MapUiSettings(
        zoomControlsEnabled = false,
        zoomGesturesEnabled = true,
        myLocationButtonEnabled = true,
        mapToolbarEnabled = false
    )
    val mapProperties = MapProperties(
        maxZoomPreference = 12.0f,
        minZoomPreference = 2f,
        isMyLocationEnabled = true
    )


   GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = mapUiSettings,
        properties = mapProperties,
        //onMyLocationClick = {location -> cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(location.latitude,location.longitude), 8f)},


    ) { events.forEach { event ->

        val marker = LatLng(event.latitude,event.longitude)
        var desc = ""
        if(!event.description.isNullOrBlank())
             desc = event.description.toString()
        MarkerInfoWindow(
            state = MarkerState(position = marker),
        ) { marker ->
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.onPrimary,
                        shape = RoundedCornerShape(35.dp, 35.dp, 35.dp, 35.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = event.name,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.h3,
                        color = MaterialTheme.colors.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    //.........................Text : description
                    Text(
                        text = desc,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.primary,
                    )
                    //.........................Spacer
                    Spacer(modifier = Modifier.height(24.dp))

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
    var savedID by remember { mutableStateOf(-1) }
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
                                InformationHolder.token = "47358c79536a33cc29477bc094cf79fed4ec6ac242b37e88c34b906679c307b2"
                                InformationHolder.userID = 3
                                navController.navigate("homeDestination")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Utility.bootstrapBlue) // Custom primary color
                        ) {
                            Text("SKIP login")
                        }

                        Button(
                            onClick = {
                                InformationHolder.token = "1cb0395ab04ba4d3a7e61dfe57126a11996f0fea30683b8a30693c7d40d6a977"
                                InformationHolder.userID = 1
                                navController.navigate("HomePageManager")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Utility.bootstrapBlue) // Custom primary color
                        ) {
                            Text("SKIP login admin")
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
                                LoginBackend.login(username, password) { token, userID, role ->
                                    if (token != null) {
                                        savedToken = token
                                        if (userID != null) {
                                            savedID = userID
                                        }

                                        // Navigate to the home destination with the obtained token
                                        // Use the MainScope to navigate on the main thread
                                        MainScope().launch {
                                            // Navigate to the home destination with the obtained token
                                            InformationHolder.token = token
                                            if (userID != null) {
                                                InformationHolder.userID = userID
                                            }
                                            if (role=="user") navController.navigate("homeDestination")
                                            else if (role=="manager") navController.navigate("HomePageManager")
                                        }
                                    } else {
                                        Log.d("mytag", "NON ho il token!")
                                        errorMessage =
                                            "Login failed. Please check your credentials."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Utility.bootstrapBlue) // Custom primary color
                        ) {
                            Text(
                                text = "Login",
                                color = Color.White // Set the text color to white
                            )
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
                            Text(
                                text = "Register",
                                color = Color.White // Set the text color to white
                            )
                        }
                    }
                }
            }
        }
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun myInvitesPage(navController: NavHostController) {
    var invites by remember { mutableStateOf<List<Invite>>(emptyList()) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var token = InformationHolder.token
    var userID = InformationHolder.userID
    var fetched by remember { mutableStateOf(false) }


    // Create a MutableStateFlow to accumulate events over time
    val eventsFlow = remember { MutableStateFlow<List<Event>>(emptyList()) }

    // Fetch events data from the backend using the provided token
    LaunchedEffect(token) {
        // Make a network request to fetch events data
        InvitesBackend.fetchInvites(token, userID) { result ->
            result.onSuccess { invitesData ->
                invites = invitesData
                fetched=true


                for (invite in invites) {
                    EventDetailsBackend.fetchEventDetails(
                        token,
                        invite.eventID
                    ) { eventResult ->
                        eventResult.onSuccess { eventDetails ->
                            // Update the MutableStateFlow with the current state
                            eventsFlow.value = eventsFlow.value + eventDetails
                        }
                        eventResult.onFailure { error ->
                            errorMessage =
                                "Failed to fetch invite details: ${error.localizedMessage}"
                        }
                    }
                }

                Log.d("invitesDebug", eventsFlow.value.toString())
            }
            result.onFailure { error ->
                errorMessage = "Failed to fetch events: ${error.localizedMessage}"
            }
        }
    }

    // Collect the eventsFlow and update the eventsState
    LaunchedEffect(eventsFlow.value) {
        eventsFlow.collect { newEvents ->
            events = newEvents
            Log.d("invitesDebug", events.toString())
        }
    }

    /// Display the UI
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Utility.bootstrapDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TopAppBar
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.h6,
                        color = Color.White
                    )
                },
                backgroundColor = Utility.bootstrapSecondary
            )

            // Add spacing
            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(
                onClick = {
                    InformationHolder.token=""
                    navController.navigate("loginDestination")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Logout", color = Color.White)
            }

            // Add spacing
            Spacer(modifier = Modifier.height(16.dp))

            // TopAppBar
            TopAppBar(
                title = {
                    Text(
                        text = "My events",
                        style = MaterialTheme.typography.h6,
                        color = Color.White
                    )
                },
                backgroundColor = Utility.bootstrapSecondary
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
                        inviteCard(
                            event = event,
                            onClick = { navController.navigate("eventDetail/$id") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else if (fetched==true) {
                Text(
                    text = "No events available.",
                    style = MaterialTheme.typography.body1,
                    color = Color.White
                )
            } else {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.body1,
                    color = Color.White
                )
            }
        }
    }
}


//TODO: aggiungi parte utente
//@Composable
//fun UserProfileSection(user: User?) {
//    // Customize this based on the user data you receive
//    if (user != null) {
//        // Display user profile information, e.g., name, email, etc.
//        Text(text = "Name: ${user.name}", color = Color.White)
//        Text(text = "Email: ${user.email}", color = Color.White)
//
//        // Display profile picture if available
//        user.profilePictureUrl?.let { url ->
//            ProfilePicture(url)
//        }
//    }
//}



@Composable
fun HomePage(navController: NavHostController) {
    // Define mutable state variable to hold events data
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var token = InformationHolder.token
    var fetched by remember { mutableStateOf(false) }



    // Fetch events data from the backend using the provided token
    LaunchedEffect(token) {
        // Make a network request to fetch events data
        EventsBackend.fetchEvents(token) { result ->
            result.onSuccess { eventsData ->
                events = eventsData
                fetched=true
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
                        eventCard(
                            event = event,
                            onClick = { navController.navigate("eventDetail/$id") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else if (fetched==true) {
                Text(
                    text = "No events available.",
                    style = MaterialTheme.typography.body1,
                    color = Color.White
                )
            } else {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.body1,
                    color = Color.White
                )
            }
        }
    }
}
@Composable
fun EventCreation(navController: NavHostController) {
    // Define mutable state variables to hold event creation details
    var eventName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var datetime by remember { mutableStateOf(LocalDateTime.now()) }
    var datetime2 by remember { mutableStateOf(LocalDateTime.now()) }
    var pictureUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var dateready by remember { mutableStateOf(false) }
    var dateready2 by remember { mutableStateOf(false) }



    // Create a Box with a custom background color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray) // A darker shade of blue-gray
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
                // Display error message


                // Display an "Event Creation" title with custom typography
                Text(
                    text = "Event Creation",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Create input fields for event creation details
                TextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Event Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                if (showDatePicker) {
                    DatePickerDialog(
                        selectedDate = datetime,
                        onDateChange = { newDate ->
                            datetime = newDate
                            showDatePicker = false
                            dateready=true
                            Log.d("mytag", "${datetime}")
                        },
                        onDismissRequest = { showDatePicker = false }
                    )
                }
                Button(onClick = { showDatePicker = true }) {
                    Text("Select Date")
                }
                if(dateready) Text("${datetime.toLocalDate()}")


                if (showTimePicker) {
                    val context = LocalContext.current
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, datetime2.hour)
                        set(Calendar.MINUTE, datetime2.minute)
                    }
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            datetime2 = LocalDateTime.of(datetime2.toLocalDate(), LocalTime.of(hour, minute))
                            showTimePicker = false
                            dateready2=true
                            Log.d("mytag", "${datetime2}")
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }
                Button(onClick = { showTimePicker = true }) {
                    Text("Select Time")
                }
                if(dateready2) Text("${datetime2.toLocalTime()}")



                // Add a way to upload a picture
                ImageUploadButton(onImageSelected = { uri ->
                    pictureUri = uri
                    //todo: scegli formato immagine standard
                })




                val contentResolver = LocalContext.current.contentResolver


                // Create an "Create Event" button with a custom color
                Button(
                    onClick = {
                        //todo: onclick send tutto ad api che salva dati in db e foto in images
                        errorMessage = null


                        val datetimeReal=LocalDateTime.of(datetime.toLocalDate(),datetime2.toLocalTime())
                        val base64Image=Utility.convertImageUriToBase64(contentResolver,pictureUri)
                        Log.d("mytag", "${base64Image}")




                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue) // Custom color for the button
                ) {
                    Text(
                        text = "Create event",
                        color = Color.White // Set the text color to white
                    )
                }
            }
        }
    }
}


@Composable
fun DatePickerDialog(
    selectedDate: LocalDateTime,
    onDateChange: (LocalDateTime) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismissRequest) {
        val calendar = Calendar.getInstance().apply {
            time = Date.from(selectedDate.atZone(ZoneId.systemDefault()).toInstant())
        }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0)
                onDateChange(newDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}




@Composable
fun ImageUploadButton(onImageSelected: (Uri) -> Unit) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Create an activity result launcher for the image picker
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            onImageSelected(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Display selected image
        if (imageUri != null) {
            Image(
                painter = rememberImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        // Button to launch the image picker
        Button(
            onClick = {
                launcher.launch("image/*")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Select Image")
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
                            RegistrationBackend.register(email, name, password) { token, userID ->
                                if (token != null) {
                                    // Navigate to the home destination after successful registration
                                    MainScope().launch {
                                        InformationHolder.token = token
                                        if (userID != null) {
                                            InformationHolder.userID = userID
                                        }
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
    var token = InformationHolder.token
    var event by remember { mutableStateOf(Event(0, "", 0.0, 0.0, "", "", "", null)) }
    val coroutineScope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }


    // Fetch events data from the backend using the provided token
    LaunchedEffect(token) {
        // Make a network request to fetch event details
        EventDetailsBackend.fetchEventDetails(token, id) { result ->
            result.onSuccess { eventData ->
                event = eventData
            }
            result.onFailure { error ->
                errorMessage = "Failed to fetch event details: ${error.localizedMessage}"
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
                        text = "Event name",
                        style = MaterialTheme.typography.h6,
                        color = Color.White
                    )
                },
                backgroundColor = Utility.bootstrapSecondary // A darker shade of blue-gray
            )

            // Add spacing
            Spacer(modifier = Modifier.height(16.dp))


            if (event.encoded_image != null && event.encoded_image != "") {

                Text(text = event.name, style = MaterialTheme.typography.h5)

                val image = Utility.base64ToBitmap(event.encoded_image)

                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "contentDescription"
                )
                Text(text = event.description ?: "", style = MaterialTheme.typography.body1)
                //todo: pulsante deve avere testo in base a stato invito
            } else {
                Text(
                    text = "Loading details",
                    style = MaterialTheme.typography.body1,
                    color = Color.White
                )
            }

            Button(onClick = {
                coroutineScope.launch {
                    //sendApiRequest() TODO: inserire entry in db per richiesta invito
                }
                navController.navigate("homeDestination")
            },
                colors = ButtonDefaults.buttonColors(backgroundColor = Utility.bootstrapBlue) // Change the background color to red
            ) {
                Text(
                    text = "Request an invite",
                    style = MaterialTheme.typography.h6,
                    color = Color.White
                )
            }
        }
    }
}

//MANAGER COMPOSABLES

@Composable
//TODO: HomePageManager
fun HomePageManager(navController: NavHostController) {
    // Define mutable state variable to hold events data
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var token = InformationHolder.token
    var fetched by remember { mutableStateOf(false) }



    // Fetch events data from the backend using the provided token
    LaunchedEffect(token) {
        // Make a network request to fetch events data
        EventsBackend.fetchEvents(token) { result ->
            result.onSuccess { eventsData ->
                events = eventsData
                fetched=true
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
                        text = "Manager Home Page, da fare, per ora copiata da user",
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
                        eventCard(
                            event = event,
                            onClick = { navController.navigate("eventDetail/$id") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else if (fetched==true) {
                Text(
                    text = "No events available.",
                    style = MaterialTheme.typography.body1,
                    color = Color.White
                )
            } else {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.body1,
                    color = Color.White
                )
            }
        }
    }
}

