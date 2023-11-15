
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


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

    object Data : BottomNavScreen (
        title = "Data",
        icon = Icons.Default.DateRange
    )

    object MyChallenges : BottomNavScreen (
        title = "MyChallenges",
        icon = Icons.Default.Favorite
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



//@Composable
//fun ProfileScreen() {
////    Text(text = "Profile Screen Content")
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Blue),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = "PROFILE",
//            color = Color.White
//        )
//    }
//}

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