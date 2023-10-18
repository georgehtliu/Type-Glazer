import androidx.compose.foundation.layout.Column
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

    var value by remember { mutableStateOf("") }

    fun update(userInput : String) {
        value = userInput
        println(userInput)
    }

    fun buildAnnotatedStringWithUrlHighlighting(
        text: String,
        color: Color
    ): AnnotatedString {
        return buildAnnotatedString {
            append(text)
            text.split(" ").forEach {
                println(it)
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

    TextField(
        value = value,
        onValueChange = { update(it) },
        textStyle = TextStyle(color = Color.Red, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(20.dp),
        visualTransformation = UrlTransformation(MaterialTheme.colors.secondary)
    )
}
object DateVisualTransformation : VisualTransformation {
    fun transform(original: String): String {
        val trimmed: String = original.take(8)
        if (trimmed.length < 4) return trimmed
        if (trimmed.length == 4) return "$trimmed-"
        val (year, monthAndOrDate) = trimmed.chunked(4)
        if (trimmed.length == 5 ) return "$year-$monthAndOrDate"
        if(trimmed.length == 6) return "$year-$monthAndOrDate-"
        val (month, date) = monthAndOrDate.chunked(2)
        return "$year-$month-$date"
    }

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(AnnotatedString(transform(text.text)),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 3) return offset
                    if (offset <= 5) return offset + 1
                    if (offset <= 7) return offset + 2
                    return 10
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= 4) return offset
                    if (offset <= 7) return offset - 1
                    if (offset <= 10) return offset - 2
                    return 8
                }
            })
    }
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