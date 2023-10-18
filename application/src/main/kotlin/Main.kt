import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
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
    val coroutinescope = rememberCoroutineScope()
    var queryResults by remember { mutableStateOf("")}

    val runQueryOnClick: () -> Unit = {
        coroutinescope.launch {
            queryResults = queryWebsite()
        }
    }

    // the actual UI, the button just calls the query above
    MaterialTheme {
        StyledTextField()
    }
}

@Composable
fun StyledTextField() {

    val testString = "The quick brown fox jumped over the lazy dog and cat and mouse and fish";
    var value by remember { mutableStateOf("") }
    var prevValue by remember { mutableStateOf("") }
    var userPosition by remember { mutableStateOf(0) }
    var errorPosition by remember { mutableStateOf(0) }

    fun update(currentInput: String) {
        if (currentInput.length < prevValue.length) {
            value = currentInput
            errorPosition -= 1
            if (errorPosition < userPosition) {
                userPosition = errorPosition
            }
            println(errorPosition)
        } else if (currentInput.length == 0) {
            errorPosition = userPosition
            value = currentInput
        } else if (currentInput[currentInput.length - 1] == testString[userPosition] && userPosition == errorPosition) {
            if (currentInput[currentInput.length - 1] == ' ') {
                value = ""
            } else {
                value = currentInput
            }
            userPosition += 1
            errorPosition += 1
        } else {
            value = currentInput
            errorPosition += 1
        }
        prevValue = value
    }

    Row {
        testString.forEachIndexed { idx, it ->
            Surface(
                color = if (idx < userPosition) Color.Transparent else if (idx < errorPosition) Color.Red.copy(alpha = 0.6f) else Color.Transparent,
                modifier = Modifier.wrapContentSize()
            ) {
                Text(
                    text = it.toString(),
                    color = if (idx < userPosition) Color.Green else Color.Black,
                )
            }
        }
    }

    TextField(
        value = value,
        onValueChange = { update(it) },
        textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(0.dp, 50.dp, 20.dp, 20.dp),
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