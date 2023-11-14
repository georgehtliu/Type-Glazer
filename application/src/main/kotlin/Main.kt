import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import java.io.File
import kotlin.system.exitProcess

fun main() = application {
    var initialState = ""
    File("test.txt").forEachLine { initialState = it }
    val position = initialState.split(";")[0]
    val xPos = position.split(",")[0].split("(")[1].split(".")[0].toInt()
    val yPos = position.split(",")[1].split(".")[0].split(" ")[1].toInt()
    val size = initialState.split(";")[1]
    val width = size.split(".")[0].toInt()
    val height = size.split(".")[2].split(" ")[2].toInt()
    val state = rememberWindowState(height = height.dp, width = width.dp, position = WindowPosition.Absolute(xPos.dp, yPos.dp))

    fun close() {
        File("test.txt").writeText(state.position.toString() + ";" + state.size.toString())
        exitProcess(0)
    }
    Window(onCloseRequest = { close() }, state) {
        App()
    }
}

@Composable
fun App(
) {
    var onIntroScreen by remember { mutableStateOf(true) }
    if (onIntroScreen) {
        IntroScreen(onDismiss = {onIntroScreen = false})
    } else {
        ShowMainScreens()
    }
}

@Composable
fun ShowMainScreens(
) {
    val screens = listOf(BottomNavScreen.Home, BottomNavScreen.Profile, BottomNavScreen.Settings, BottomNavScreen.Data )
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
            }
        }
    )
}

@Composable
fun IntroScreen(onDismiss: () -> Unit) {
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
                    Text("Simply sign up/in and begin competing in typing competitions with your favorite farm animals")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss) {
                        Text("Continue")
                    }
                }
            }
        }
    )
}

