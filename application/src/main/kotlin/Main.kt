import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
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

