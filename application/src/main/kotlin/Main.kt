import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Composable
fun App() {

    // we want to call a suspending function to query the data
    // we need to do this from a coroutine scope
//    val coroutinescope = rememberCoroutineScope()
//    var queryResults by remember { mutableStateOf("")}
//
//    val runQueryOnClick: () -> Unit = {
//        coroutinescope.launch {
//            queryResults = queryWebsite()
//        }
//    }
//
//    // the actual UI, the button just calls the query above
//    MaterialTheme {
//        Column {
//            Button(onClick = runQueryOnClick) {
//                Text("Run")
//            }
//            Text(queryResults)
//            BottomNavBar()
//        }
//    }
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

suspend fun queryWebsite(): String {
    val site = "https://ktor.io/"
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(site)
    client.close()

    if (response.status.value in 200..299) {
        return "SITE: ${site} \nSTATUS: ${response.status.value}\nHEADER: ${response.headers}\nCONTENT: ${response.body<String>().length} bytes\n"
    } else
        return "STATUS: ${response.status.value}"
}