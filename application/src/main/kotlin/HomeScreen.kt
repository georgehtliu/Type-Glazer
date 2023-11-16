
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
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class RaceResultRequest(val userID: Int, val textID: Int, val date: String, val wpm: Int)

@Composable
fun HomeScreen(currentuserId: Int
) {
    Game(currentuserId)
}


@Composable
fun InviteFriends() {
    var text by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var iserror by remember { mutableStateOf(false) }

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Friend's Username") },
            )

            message.let {
                Spacer(modifier = Modifier.height(8.dp))
                if (iserror) {
                    Text(it, color = Color.Red)
                } else{
                    Text(it, color = Color.Green)
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    message = "User @$text has been invited!"
                    text = ""
                    iserror = false
                } else {
                    message = "Invalid Username"
                    iserror = true
                }
            }
        ) {
            Text("Invite")
        }
    }
}

@Composable
fun Game(currentuserId: Int) {
    MaterialTheme {
        var progress by remember { mutableStateOf(0.0f) }
        var passage by remember { mutableStateOf("") }
        var userPosition by remember { mutableStateOf(0) }
        var startTime by remember { mutableStateOf(0L) }
        var wpm by remember { mutableStateOf(0) }
        var youWin by remember { mutableStateOf(false) }
        var wordsTyped by remember { mutableStateOf(0) }
        var totalWords by remember { mutableStateOf(0) }
        val coroutineScope = rememberCoroutineScope()

        // Function to fetch a random passage
        suspend fun fetchRandomPassage() {
            val randomTextEndpoint = "http://localhost:5050/texts/random"
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json()
                }
            }

            try {
                val response: HttpResponse = client.get(randomTextEndpoint)

                // Close the client after the request
                client.close()

                if (response.status == HttpStatusCode.OK) {
                    withContext(Dispatchers.Main) {
                        passage = response.bodyAsText()
                    }
                } else {
                    print("[FAILED] FETCHING RANDOM PASSAGE")
                }
            } catch (e: Exception) {
                // Handle exceptions if needed
                print("[FAILED] FETCHING RANDOM PASSAGE")
            }
        }

        LaunchedEffect(Unit) {
            if (passage.isBlank()) {
                val result = produceState(initialValue = "") {
                    fetchRandomPassage()
                }
                passage = result.value
            }
        }

        fun getTotalWords(passage: String): Int {
            val words = passage.trim().split("\\s+".toRegex())
            return words.size
        }

        fun startNewRace() {
            coroutineScope.launch(Dispatchers.Default) {
                fetchRandomPassage()
            }
            userPosition = 0
            startTime = System.currentTimeMillis() // Set the start time when starting a new race
            wpm = 0
            youWin = false
            wordsTyped = 0
            totalWords = getTotalWords(passage)
        }

//        LaunchedEffect(Unit) {
//            if (passage.isBlank()) {
//                fetchRandomPassage()
//            }
//        }

        // Start a new race when the composable is first displayed
        if (userPosition == 0 && passage.isNotBlank()) {
            startNewRace()
        }

        if (userPosition >= passage.length && !youWin) {
            wordsTyped += 1
            youWin = true

            // Call the function to submit the post request
            coroutineScope.launch(Dispatchers.Default) {
                val success = submitRaceResult(currentuserId, wpm)
                if (success) {
                    print("[SUCCESSFUL] SUBMITTING RACE")
                } else {
                    print("[FAILED] SUBMITTING RACE")
                }
            }
        }

        progress = userPosition.toFloat() / passage.length

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
                    passage = passage
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

            if (youWin) {
                Spacer(modifier = Modifier.height(50.dp))
                Text("Challenge a Friend to the Same Race:")
                Spacer(modifier = Modifier.height(20.dp))
                InviteFriends()
            }

        }
    }
}



suspend fun submitRaceResult(currentuserId: Int, wpm: Int): Boolean {
    val insertRaceEndpoint = "http://localhost:5050/insertRace"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    try {
        // TODO : replace with actual textId
        val userID = currentuserId
        val textID = 2
        val currentDate = LocalDate.now()
        // Define the desired date format
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = currentDate.format(dateFormat)

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
