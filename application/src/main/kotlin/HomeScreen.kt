
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class RaceResultRequest(val userID: Int, val textID: Int, val date: String, val wpm: Int)

@Composable
fun HomeScreen(
) {
    Game()
}

@Composable
fun Game() {
    MaterialTheme {
        var progress by remember { mutableStateOf(0.0f) }
        var passages by remember {
            mutableStateOf(
                listOf(
                    "The quick brown fox jumped over the lazy dog and cat and mouse and fish",
                    "The sun is shining, the birds are singing, and the flowers are blooming",
                    "Education is the most powerful weapon which you can use to change the world",
                    "Happiness is not something ready-made. It comes from your own actions",
                    "You miss 100% of the shots you don't take.",
                    "The road less traveled is often the path to success",
                )
            )
        }
        var currentPassageIndex by remember { mutableStateOf(0) }
        var userPosition by remember { mutableStateOf(0) }
        var startTime by remember { mutableStateOf(0L) }
        var wpm by remember { mutableStateOf(0) }
        var youWin by remember { mutableStateOf(false) }
        var wordsTyped by remember { mutableStateOf(0) }
        var totalWords by remember { mutableStateOf(0) }
        val coroutineScope = rememberCoroutineScope()

        fun getTotalWords(passage: String): Int {
            val words = passage.trim().split("\\s+".toRegex())
            return words.size
        }

        fun startNewRace() {
            currentPassageIndex = (0 until passages.size).random()
            userPosition = 0
            startTime = System.currentTimeMillis() // Set the start time when starting a new race
            wpm = 0
            youWin = false
            wordsTyped = 0
            totalWords = getTotalWords(passages[currentPassageIndex])
        }

        // Start a new race when the composable is first displayed
        if (currentPassageIndex == 0 && userPosition == 0) {
            startNewRace()
        }

        if (userPosition >= passages[currentPassageIndex].length && !youWin) {
            wordsTyped += 1
            youWin = true

            // Call the function to submit the post request
            coroutineScope.launch(Dispatchers.Default) {
                val success = submitRaceResult(wpm)
                if (success) {
                    print("[SUCCESSFUL] SUBMITTING RACE")
                } else {
                    print("[FAILED] SUBMITTING RACE")
                }
            }
        }

        progress = userPosition.toFloat() / passages[currentPassageIndex].length

        if (startTime > 0) {
            val elapsedTime = (System.currentTimeMillis() - startTime) / 60000.0
            if (elapsedTime > 0) {
                wpm = (wordsTyped / elapsedTime).toInt()
            } else {
                wpm = 0
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.size(100.dp))

            ProgressBar(
                color = Color.Black,
                dotRadius = 2f,
                spacing = 8f,
                lineWidth = 2f,
                progress = progress
            )

            Spacer(modifier = Modifier.size(30.dp))

            if (!youWin) {
                Typer(
                    passage = passages[currentPassageIndex]
                ) { typedCharacters, newStartTime, wordCount ->
                    userPosition = typedCharacters
                    if (newStartTime == 0L) {
                        // Do not start a new coroutine here, just update the start time
                        startTime = System.currentTimeMillis()
                    }
                    wordsTyped = wordCount
                }
            } else {
                Text("You Win!")
                Button(
                    onClick = { startNewRace() }
                ) {
                    Text("Start New Race")
                }
            }

            Text("Words typed: $wordsTyped / ${totalWords}")
            Text("WPM: $wpm")
        }
    }
}



suspend fun submitRaceResult(wpm: Int): Boolean {
    val insertRaceEndpoint = "http://localhost:5050/insertRace"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    try {
        // TODO : replace with actual userId and stuff
        val userID = 2
        val textID = 2
        val date = "2022-12-12"

        val response: HttpResponse = client.post(insertRaceEndpoint) {
            contentType(ContentType.Application.Json)

            println(RaceResultRequest(userID, textID, date, wpm))

            setBody(RaceResultRequest(userID, textID, date, wpm))
        }

        // Close the client after the request
        client.close()

        // Handle the response if needed
        return response.status.value in 200..299
    } catch (e: Exception) {
        // Handle exceptions if needed
        return false
    }
}

@Composable
fun ProgressBar(
    color: Color = Color.Black,
    dotRadius: Float = 2f,
    spacing: Float = 8f,
    lineWidth: Float = 2f,
    progress: Float
) {
    Canvas(
        modifier = Modifier.fillMaxWidth()
    ) {
        val startY = size.height / 2
        val endX = size.width * progress
        var currentX = 0f

        while (currentX < endX) {
            drawCircle(
                color = color,
                radius = dotRadius,
                center = Offset(currentX, startY),
                style = Stroke(width = lineWidth)
            )
            currentX += spacing
        }
    }
}


@Composable
fun Typer(
    passage: String,
    onCharactersTyped: (Int, Long, Int) -> Unit
) {
    var value by remember { mutableStateOf("") }
    var userPosition by remember { mutableStateOf(0) }
    var errorPosition by remember { mutableStateOf(0) }
    var prevValue by remember { mutableStateOf("") }
    var wordCount by remember { mutableStateOf(0) }

    fun update(currentInput: String) {
        if (currentInput.length < prevValue.length) {
            value = currentInput
            errorPosition -= 1
            if (errorPosition < userPosition) {
                userPosition = errorPosition
            }
        } else if (currentInput.length == 0) {
            errorPosition = userPosition
            value = currentInput
        } else if (currentInput[currentInput.length - 1] == passage[userPosition] && userPosition == errorPosition) {
            if (currentInput[currentInput.length - 1] == ' ') {
                value = ""
                wordCount += 1
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
        onCharactersTyped(userPosition, errorPosition.toLong(), wordCount)
    }

    Row {
        passage.forEachIndexed { idx, it ->
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
