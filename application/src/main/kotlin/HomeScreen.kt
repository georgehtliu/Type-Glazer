
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
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class RaceResultRequest(val userID: Int, val textID: Int, val date: String, val wpm: Int)

@Serializable
data class ChallengeRequest(val fromUserID: Int, val toUsername: String, val textID: Int, val raceID: Int)
@Serializable
data class submitRaceResponse(val raceID: Int, val userID: Int, val textID: Int)

@Composable
fun HomeScreen(currentUserState: UserState
) {
    Game(currentUserState)
}

@Composable
fun InviteFriends(currentuserID: Int, currenttextID: Int, currentraceID: Int) {
    var text by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var iserror by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                coroutineScope.launch(Dispatchers.Default) {
                    val success = submitChallenge(currentuserID, text, currenttextID, currentraceID)
                    if (success) {
                        print("[SUCCESSFUL] SUBMITTING CHALLENGE")
                        message = "User @$text has been invited!"
                        text = ""
                        iserror = false
                    } else {
                        print("[FAILED] SUBMITTING CHALLENGE")
                        message = "Invalid Username"
                        iserror = true
                    }
                }
            }
        ) {
            Text("Invite")
        }
    }
}

@Composable
fun Game(currentUserState: UserState) {
    MaterialTheme {
        var progress by remember { mutableStateOf(0.0f) }
        val passages: HashMap<Int, String> = HashMap<Int, String>()
        passages[0] = "The quick brown fox jumped over the lazy dog and cat and mouse and fish"
        passages[1] = "The sun is shining, the birds are singing, and the flowers are blooming"
        passages[2] = "Education is the most powerful weapon which you can use to change the world"
        passages[3] = "Happiness is not something ready-made. It comes from your own actions"
        passages[4] = "You miss 100% of the shots you don't take."
        passages[5] = "The road less traveled is often the path to success"

        var currentPassageIndex by remember { mutableStateOf(0) }
        var userPosition by remember { mutableStateOf(0) }
        var startTime by remember { mutableStateOf(0L) }
        var wpm by remember { mutableStateOf(0) }
        var youWin by remember { mutableStateOf(false) }
        var showStartButton by remember { mutableStateOf(true) }
        var wordsTyped by remember { mutableStateOf(0) }
        var totalWords by remember { mutableStateOf(0) }
        var submittedRace by remember { mutableStateOf(false) }
        var raceID by remember { mutableStateOf(-1) }
        val coroutineScope = rememberCoroutineScope()

        fun getTotalWords(passage: String): Int {
            val words = passage.trim().split("\\s+".toRegex())
            return words.size
        }

        fun getPassageIndex(challengeTextId : Int): Int {
            if (challengeTextId != -1) {
                return challengeTextId
            } else {
                return (0 until passages.size).random()
            }

        }

        fun startNewRace() {
            currentPassageIndex = getPassageIndex(currentUserState.acceptedChallenge.textId)
            userPosition = 0
            startTime = System.currentTimeMillis() // Set the start time when starting a new race
            wpm = 0
            youWin = false
            wordsTyped = 0
            totalWords = passages[currentPassageIndex]?.let { getTotalWords(it) }!!
        }

        if (userPosition >= passages[currentPassageIndex]?.length!! && !youWin) {
            wordsTyped += 1
            youWin = true

            // Call the function to submit the post request
            coroutineScope.launch(Dispatchers.Default) {
                val success = submitRaceResult(currentUserState.currentUser.userId, wpm, currentPassageIndex)
                if (success != -1) {
                    print("[SUCCESSFUL] SUBMITTING RACE")
                    raceID = success
                } else {
                    print("[FAILED] SUBMITTING RACE")
                }
            }
        }

        progress = userPosition.toFloat() / passages[currentPassageIndex]?.length!!

        if (startTime > 0) {
            val elapsedTime = (System.currentTimeMillis() - startTime) / 60000.0
            if (elapsedTime > 0) {
                wpm = (wordsTyped / elapsedTime).toInt()
            } else {
                wpm = 0
            }
        }

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,) {

            if (showStartButton) {
                Button(
                    onClick = { startNewRace(); showStartButton = false; currentUserState.acceptedChallenge.textId = -1 }
                ) {
                    Text("Start Race")
                }
            } else {
                ProgressBar(
                    color = Color.Black,
                    dotRadius = 2f,
                    spacing = 8f,
                    lineWidth = 2f,
                    progress = progress
                )

                Spacer(modifier = Modifier.size(30.dp))

                if (!youWin) {
                    passages[currentPassageIndex]?.let {
                        Typer(
                            passage = it,
                        ) { typedCharacters, newStartTime, wordCount ->
                            userPosition = typedCharacters
                            if (newStartTime == 0L) {
                                // Do not start a new coroutine here, just update the start time
                                startTime = System.currentTimeMillis()
                            }
                            wordsTyped = wordCount
                        }
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
                    InviteFriends(currentUserState.currentUser.userId, currentPassageIndex, raceID)
                }
            }

        }
    }
}


suspend fun submitChallenge(currentuserId: Int, challengeuserName: String, currenttextID: Int, currentraceID: Int): Boolean {
    val insertChallengeEndpoint = "http://localhost:5050/challenges/send"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    try {
        val fromUserID = currentuserId
        val toUsername = challengeuserName
        val textID = currenttextID
        val raceID = currentraceID


        val response: HttpResponse = client.post(insertChallengeEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(ChallengeRequest(fromUserID, toUsername, textID, raceID))
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

suspend fun submitRaceResult(currentuserId: Int, wpm: Int, currenttextID: Int): Int {
    val insertRaceEndpoint = "http://localhost:5050/insertRace"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    try {
        val userID = currentuserId
        val textID = currenttextID
        val currentDate = LocalDate.now()
        // Define the desired date format
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = currentDate.format(dateFormat)
        val requestBody = RaceResultRequest(userID, textID, date, wpm)

        val response: String = client.post(insertRaceEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.bodyAsText()

        // Close the client after the request
        client.close()
        val submitRaceResponseDecoded: submitRaceResponse = Json.decodeFromString(response)
        // Handle the response if needed
        return submitRaceResponseDecoded.raceID
    } catch (e: Exception) {
        // Handle exceptions if needed
        return -1
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
