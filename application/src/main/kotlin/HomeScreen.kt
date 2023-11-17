import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.time.LocalDateTime
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
        passages[6] = "Success is not the key to happiness. Happiness is the key to success. If you love what you are doing, you will be successful."
        passages[7] = "The only limit to our realization of tomorrow will be our doubts of today."
        passages[8] = "In the middle of every difficulty lies opportunity."
        passages[9] = "The only way to do great work is to love what you do."
        passages[10] = "Believe you can and you're halfway there."
        passages[11] = "Don't watch the clock; do what it does. Keep going."
        passages[12] = "The future belongs to those who believe in the beauty of their dreams."
        passages[13] = "It does not matter how slowly you go as long as you do not stop."
        passages[14] = "The secret of getting ahead is getting started."
        passages[15] = "The harder you work for something, the greater you'll feel when you achieve it."
        passages[16] = "Talent wins games, but teamwork and intelligence win championships."
        passages[17] = "The strength of the team is each individual member. The strength of each member is the team."
        passages[18] = "Soccer is simple, but it is difficult to play simple."
        passages[19] = "In football, the worst blindness is only seeing the ball."
        passages[20] = "The secret of food lies in memory – of thinking and then knowing what the taste of cinnamon or steak is."
        passages[21] = "First we eat, then we do everything else."
        passages[22] = "The only bad workout is the one that didn't happen."
        passages[23] = "The last three or four reps is what makes the muscle grow. This area of pain divides a champion from someone who is not a champion."
        passages[24] = "If you think lifting weights is dangerous, try being weak. Being weak is dangerous."
        passages[25] = "The only place where success comes before work is in the dictionary."
        passages[26] = "The clock is ticking. Are you becoming the person you want to be?"
        passages[27] = "Whether you think you can, or you think you can't, you're right."
        passages[28] = "The successful warrior is the average man, with laser-like focus."
        passages[29] = "Don't limit your challenges. Challenge your limits."
        passages[30] = "Each new day is a new opportunity to improve yourself. Take it and make the most of it."
        passages[31] = "In the world of programming, simplicity and clarity are the key to efficient code."
        passages[32] = "Music gives a soul to the universe, wings to the mind, flight to the imagination and life to everything."
        passages[33] = "Art is not what you see, but what you make others see."
        passages[34] = "The beauty of nature will leave you speechless once you start traveling, but it will make you a storyteller once you finish traveling."
        passages[35] = "Photography is the story I fail to put into words."
        passages[36] = "Life is like riding a bicycle. To keep your balance, you must keep moving."
        passages[37] = "The journey of a thousand miles begins with one step."
        passages[38] = "In the end, it's not the years in your life that count. It's the life in your years."
        passages[39] = "Life is really simple, but we insist on making it complicated."
        passages[40] = "In three words I can sum up everything I've learned about life: it goes on."
        passages[41] = "Life is what happens when you're busy making other plans."
        passages[42] = "Many of life's failures are people who did not realize how close they were to success when they gave up."
        passages[43] = "If you want to live a happy life, tie it to a goal, not to people or things."
        passages[44] = "Never let the fear of striking out keep you from playing the game."
        passages[45] = "The purpose of our lives is to be happy."
        passages[46] = "Life is never fair, and perhaps it is a good thing for most of us that it is not."
        passages[47] = "The biggest adventure you can take is to live the life of your dreams."
        passages[48] = "Life is short, and it's up to you to make it sweet."
        passages[49] = "Life doesn't require that we be the best, only that we try our best."
        passages[50] = "I have found that if you love life, life will love you back."
        passages[51] = "Life is really simple, but men insist on making it complicated."
        passages[52] = "You have within you right now, everything you need to deal with whatever the world can throw at you."
        passages[53] = "Life is a succession of lessons which must be lived to be understood."
        passages[54] = "My mission in life is not merely to survive, but to thrive; and to do so with some passion, some compassion, some humor, and some style."
        passages[55] = "Life is like a coin. You can spend it any way you wish, but you only spend it once."
        passages[56] = "Life is a song - sing it. Life is a game - play it. Life is a challenge - meet it. Life is a dream - realize it. Life is a sacrifice - offer it. Life is love - enjoy it."
        passages[57] = "To live is the rarest thing in the world. Most people exist, that is all."
        passages[58] = "Life is what we make it, always has been, always will be."
        passages[59] = "Life is either a daring adventure or nothing at all."
        passages[60] = "The good life is one inspired by love and guided by knowledge."



        var currentPassageIndex by remember { mutableStateOf(0) }
        var userPosition by remember { mutableStateOf(0) }
        var startTime by remember { mutableStateOf(0L) }
        var wpm by remember { mutableStateOf(0) }
        var youWin by remember { mutableStateOf(false) }
        var showStartButton by remember { mutableStateOf(true) }
        var wordsTyped by remember { mutableStateOf(0) }
        var totalWords by remember { mutableStateOf(0) }
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
                    Button(
                        onClick = { startNewRace() }
                    ) {
                        Text("Start New Race")
                    }
                }

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
        val currentDateTime = LocalDateTime.now()
// Define the desired date format
        val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = currentDateTime.format(dateTimeFormat)
        val requestBody = RaceResultRequest(userID, textID, dateTime, wpm)

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
    progress: Float
) {
    Column {
        Text(text = "Progress: ${(progress * 100).toInt()}%", style = TextStyle(fontSize = 16.sp))
        LinearProgressIndicator(
            color = color,
            progress = progress,
            modifier = Modifier.fillMaxWidth()
        )
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
