
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class SignupRequest(val username: String, val email: String, val password: String)

data class userId(var userId: Int)

enum class LoginStatus {
    NONE,
    SUCCESS,
    FAILURE
}

suspend fun login(username: String, password: String, loginuserId: userId): Boolean {
    // Check if the provided username and password match the predefined values
    if (username == "testuser" && password == "testpassword") {
        return true
    }
    // If the provided values don't match, perform the actual login logic
    val loginEndpoint = "http://localhost:5050/login"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    try {
        val response: HttpResponse = client.post(loginEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username, password))
        }

        // Close the client after the request
        client.close()
        loginuserId.userId = response.bodyAsText().toInt()
        return response.status.value in 200..299
    } catch (e: Exception) {
        // Handle exceptions if needed
        return false
    }
}

suspend fun signup(username: String, password: String, signupuserId: userId): Boolean {
    // If the provided values don't match, perform the actual login logic
    val signupEndpoint = "http://localhost:5050/createNewUser"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    try {
        val response: HttpResponse = client.post(signupEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(SignupRequest(username, "$username@gmail.com", password))
        }

        // Close the client after the request
        client.close()
        signupuserId.userId = response.bodyAsText().toInt()
        return response.status.value in 200..299
    } catch (e: Exception) {
        // Handle exceptions if needed
        return false
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Composable
fun App(
) {
    var onIntroScreen by remember { mutableStateOf(true) }
    var currentUser = userId(-1)
    if (onIntroScreen) {
        IntroScreen(onDismiss = {onIntroScreen = false}, currentUser)
    } else {
        ShowMainScreens()
    }
}

@Composable
fun ShowMainScreens(
) {
    val screens = listOf(BottomNavScreen.Home, BottomNavScreen.Profile, BottomNavScreen.Settings, BottomNavScreen.Data, BottomNavScreen.MyChallenges)
    var selected by remember{ mutableStateOf(screens.first()) }

    Scaffold(
        bottomBar = {
            BottomNavigation {
                screens.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = screen == selected,
                        onClick = { selected = screen },
                        modifier = Modifier.padding(8.dp)
                    )
                } }
        },
        content = {
            when (selected) {
                BottomNavScreen.Home -> HomeScreen()
                BottomNavScreen.Profile -> ProfileScreen()
                BottomNavScreen.Settings -> SettingsScreen()
                BottomNavScreen.Data -> DataTable()
                BottomNavScreen.MyChallenges -> MyChallenges()
            }
        }
    )
}

@Composable
fun IntroScreen(onDismiss: () -> Unit, currentuserId: userId) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignInMode by remember { mutableStateOf(true) }
    var loginStatus by remember { mutableStateOf(LoginStatus.NONE) }
    var errorMessage by remember { mutableStateOf("") }
    var loginuserId by remember {mutableStateOf(-1)}

    val coroutineScope = rememberCoroutineScope()

    Scaffold (
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome to the Farm Racer!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("We're glad to have you here.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Simply sign in and begin competing in typing competitions with your favorite farm animals")
                    Spacer(modifier = Modifier.height(16.dp))
                    SignUpPrompt(username, password, { username = it }, { password = it }, isSignInMode)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (loginStatus == LoginStatus.FAILURE) {
                        Text(errorMessage)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            if (isSignInMode) {
                                coroutineScope.launch(Dispatchers.Default) {
                                    val success = login(username, password, currentuserId)
                                    if (success) {
                                        onDismiss.invoke()
                                    } else {
                                        errorMessage = "Invalid username or password. Please try again."
                                        loginStatus = LoginStatus.FAILURE
                                    }
                                }
                            } else {
                                coroutineScope.launch(Dispatchers.Default) {
                                    val success = signup(username, password, currentuserId)
                                    if (success) {
                                        onDismiss.invoke()
                                    } else {
                                        errorMessage = "Sign up failed. GG"
                                        loginStatus = LoginStatus.FAILURE
                                    }
                                }
                            }
                        }) {
                            if (isSignInMode) {
                                Text("Sign In")
                            } else {
                                Text("Sign up")
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            isSignInMode = !isSignInMode
                        }) {
                            if (isSignInMode) {
                                Text("Sign Up as a new user")
                            } else {
                                Text("Sign in as an existing user")
                            }
                        }
                    }
                }
            }
        }
    )
}





