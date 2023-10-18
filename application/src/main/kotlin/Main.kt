import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
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
    var userPosition by remember { mutableStateOf(0) }

    fun update(currentInput: String) {
        if (currentInput.length < value.length) {
            value = currentInput
            userPosition -= 1
        }
        if (currentInput.length == 0) {
            return
        }
        if (currentInput[currentInput.length - 1] == testString[userPosition]) {
            if (currentInput[currentInput.length - 1] == ' ') {
                value = ""
            } else {
                value = currentInput
            }
            userPosition += 1
        }
    }

    fun buildAnnotatedStringWithUrlHighlighting(
        text: String,
        color: Color
    ): AnnotatedString {
        return buildAnnotatedString {
            append(text)
            text.split("").forEach {
                val startIndex = text.indexOf(it)
                val endIndex = startIndex + it.length
                addStyle(
                    style = SpanStyle(
                        color = color,
                        textDecoration = TextDecoration.None
                    ),
                    start = startIndex, end = endIndex
                )
            }
        }
    }

    class UrlTransformation(
        val color: Color
    ) : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            return TransformedText(
                buildAnnotatedStringWithUrlHighlighting(value, color),
                OffsetMapping.Identity
            )
        }
    }

    Row {
        testString.forEachIndexed {idx, it ->
            println(idx)
            Text(
                text = it.toString(),
                color = if(idx < userPosition) Color.Green else Color.Red,
            )
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