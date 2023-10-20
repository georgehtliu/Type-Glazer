import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


sealed class BottomNavScreen(
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavScreen(
        title = "Home",
        icon = Icons.Default.Home
    )

    object Profile : BottomNavScreen(
        title = "Profile",
        icon = Icons.Default.Person
    )

    object Settings : BottomNavScreen(
        title = "Settings",
        icon = Icons.Default.Settings
    )
}

//@Composable
//fun HomeScreen() {
//
//    fun main() = application {
//        Window(onCloseRequest = ::exitApplication) {
//            App()
//        }
//    }



@Composable
fun ProfileScreen() {
//    Text(text = "Profile Screen Content")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "PROFILE",
            color = Color.White
        )
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SETTINGS",
            color = Color.White
        )
    }
}

