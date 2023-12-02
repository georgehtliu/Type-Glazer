
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
        title = "Race",
        icon = Icons.Default.PlayArrow
    )

    object Data : BottomNavScreen (
        title = "Race Data",
        icon = Icons.Default.DateRange
    )

    object MyChallenges : BottomNavScreen (
        title = "My Challenges",
        icon = Icons.Default.Favorite
    )

    object RaceDetails : BottomNavScreen (
        title = "Head to Head",
        icon = Icons.Default.Star
    )

    object Settings : BottomNavScreen(
        title = "Log Out",
        icon = Icons.Default.ArrowBack
    )
}
