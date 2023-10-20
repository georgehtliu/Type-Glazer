import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Composable
fun App(
) {
    val screens = listOf(BottomNavScreen.Home, BottomNavScreen.Profile, BottomNavScreen.Settings)
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
            }
        }
    )
}

