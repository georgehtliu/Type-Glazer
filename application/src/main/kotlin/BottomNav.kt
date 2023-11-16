
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


sealed class BottomNavScreen(
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavScreen(
        title = "Home",
        icon = Icons.Default.Home
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
