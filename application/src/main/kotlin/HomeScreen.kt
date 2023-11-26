
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
@Serializable
data class InsertResultRequest(val fromRaceID: Int, val toRaceID: Int)

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
                } else {
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

        //
        // 0 to 19: Easy
        // 20 to 39: Medium
        // 40 to 59: Hard
        //

        passages[0] = "Meow meow"
        passages[1] = "Woof woof"
        passages[2] = "Moooooo I am a cow"
        passages[3] = "Cheese pizza is my favorite."
        passages[4] = "I love glazed donuts"
        passages[5] = "LeBron is better than MJ!"
        passages[6] = "Triangle is a polygon with 3 sides."
        passages[7] = "The speed limit is 80 mph."
        passages[8] = "CS 346 is so good"
        passages[9] = "Mark Liu George Liu Ray Hao"
        passages[10] = "I am going to shower!"
        passages[11] = "It is 8pm now!"
        passages[12] = "Lazeez chicken on the rocks"
        passages[13] = "Harvey's milkshake!!!"
        passages[14] = "Burger king mmmmm!!"
        passages[15] = "1 2 3 4 5"
        passages[16] = "Talent wins games"
        passages[17] = "My favorite team is Toronto Bluejays"
        passages[18] = "Hey everyone!"
        passages[19] = "ha ha ha ja ja ja"
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
        passages[40] = "By the pricking of my thumbs, something wicked this way comes. In the shadows of night, where the veil 'twixt reality and illusion is thin, Macbeth, that tragic figure, traverses the murky corridors of his fate. The spectral cauldron of ambition boils o'er with dire prophecies, as the fateful incantations of the Weird Sisters permeate the air like an ominous refrain. 'Is this a dagger which I see before me?' he wonders, as the spectral hands of guilt and treachery clutch at his tormented soul."
        passages[41] = "Hark! A tempest brews in yon celestial realm, where ethereal sprites cavort in twilight's argent glow. Amidst the sylvan glades, whence the enigmatic moon weaves a silken tapestry, fair Puck, that mischievous imp of Midsummer's lore, spins whimsical verses, casting eldritch charms upon the starlit ebon sky."
        passages[42] = "Cinnabon, the aromatic haven where the sweet symphony of cinnamon and dough converges, is not merely a treat; it's an indulgent journey into the heart of comfort and decadence. Stepping into Cinnabon is like entering a fragrant sanctuary, where the scent of freshly baked rolls lures you into a world of blissful anticipation"
        passages[43] = "Computers, the silent architects of our digital age, transcend mere machines; they are the catalysts of innovation, the engines propelling us into the future. With each keystroke and pixel, computers weave the intricate tapestry of our interconnected world, transforming ideas into reality and data into insights."
        passages[44] = "Gyubee, a culinary sanctuary where the art of grilling meets the joy of shared moments, invites you to embark on a journey of sizzling flavors and communal delight. Stepping into Gyubee is like entering a realm where time slows, and the aroma of premium meats grilling on tabletop barbecue grates becomes a tantalizing prelude to a feast."
        passages[45] = "The Chinese Communist Party (CCP), a force at the helm of a nation's destiny, stands as a multifaceted entity shaping the narrative of a country steeped in history and innovation. Like a guardian of the Middle Kingdom's aspirations, the CCP has navigated a complex path, overseeing a dramatic transformation from an agrarian society to an economic powerhouse."
        passages[46] = "Lionel Messi, the maestro of the soccer pitch, is more than a player; he is a symphony of skill, a virtuoso whose every touch on the ball is a brushstroke on the canvas of football history. In the rhythmic dance between Messi and the ball, we witness the fusion of artistry and athleticism, a masterpiece painted with precision and passion."
        passages[47] = "East Side Mario's, a culinary haven where the spirit of Italy dances on every plate, invites you to savor the flavors of amore in a vibrant and welcoming atmosphere. Stepping into this gastronomic haven is like crossing a threshold into an Italian trattoria, where the aroma of garlic, tomatoes, and simmering sauces envelops you in a warm embrace."
        passages[48] = "In the push and pull of each repetition, the dumbbell chest press becomes a dialogue between body and iron, forging a connection that goes beyond the physical. The burn in the muscles becomes a melody of perseverance, a composition of sweat and determination that plays out on the canvas of the gym."
        passages[49] = "Kevin Durant, the towering titan on the basketball court, exudes a brilliance that extends far beyond the game. Yet, even a superstar like Durant may find his legs whispering a plea for hydration in the form of ashy skin. In the symphony of athletic prowess, the subtle notes of dryness become a reminder that even the most celebrated figures are, at their essence, human."
        passages[50] = "Josh Giddey, the basketball prodigy from down under, has emerged as a phenom on the global stage, weaving his own narrative of skill, vision, and unparalleled court presence. With a basketball IQ that belies his youth, Giddey conducts the game like a seasoned maestro orchestrating a symphony."
        passages[51] = "In his prime, Rose was a blur of explosive athleticism and finesse, a point guard whose crossovers left defenders in his wake and whose acrobatic finishes at the rim defied gravity. The MVP title he earned in 2011 was not just an accolade; it was a recognition of a player who had become the heartbeat of a city and a symbol of hope for fans worldwide."
        passages[52] = "Gordon Ramsay, the culinary maestro with a palate as sharp as his wit, brings to the kitchen an alchemy of passion, precision, and unapologetic authenticity. His culinary prowess is a symphony of flavors, orchestrated with the finesse of a seasoned conductor. Behind the tough exterior and no-nonsense demeanor lies a chef driven by an unwavering commitment to excellence."
        passages[53] = "Ray Allen, the maestro of the three-point arc, etched his name in basketball history with a shot that defied the limits of time and precision. As the seconds dwindled in Game 6 of the NBA Finals, the Miami Heat trailed the San Antonio Spurs. The ball found its way to Allen beyond the arc, and with the cool composure that defined his career, he released a shot that would be eternally etched in the annals of the sport."
        passages[54] = "In the delightful dance of flavors, pizza and donuts take center stage as the culinary duo that turns ordinary moments into extraordinary memories. A slice of pizza, adorned with a mosaic of toppings, is a savory symphony that tantalizes the taste buds, while a donut, glazed to perfection, is a sweet indulgence that brings joy with every bite. Together, they create a harmonious blend of savory and sweet, a gastronomic pas de deux that transcends the ordinary."
        passages[55] = "In the heart of the arena, the atmosphere was electric, with the buzz of excited fans reverberating through the air. The tension was palpable as the two teams battled fiercely on the court, each possession a crucial moment in the game. The star player dribbled down the court, showcasing incredible ball-handling skills that left defenders in their wake. The crowd erupted into cheers as the player executed a flawless slam dunk, adding to the highlight reel of the night."
        passages[56] = "In the quiet embrace of a midnight library, James found inspiration beneath the soft glow of reading lamps. The hushed whispers of book pages turning mingled with the tap-tap-tap of his keyboard as he wove a mystery that mirrored the enigmatic atmosphere of the space. The creaking of leather chairs and the distant footsteps of the lone librarian created an ambiance that wrapped around his writing, turning the library into a nocturnal muse for his literary endeavors."
        passages[57] = "Amidst the aromatic chaos of a busy kitchen, Chef Elena perched on a stool, laptop open beside her cutting board. Her culinary adventures unfolded through a digital narrative, the sizzle of pans and the hiss of simmering sauces providing a backdrop for her gastronomic tales. The rhythmic chopping of vegetables and the occasional clatter of plates formed a culinary symphony, complementing the savory stories she penned with each culinary creation."
        passages[58] = "As the train rumbled along the tracks, Alex carved out a mobile writing haven at a quaint window seat. The rhythmic clatter of wheels on steel served as a steady beat for his prose, capturing the fleeting landscapes that blurred past his view. The sound of the train became an integral part of his narrative, a journey in both words and motion, with each paragraph mirroring the changing scenes outside his window."
        passages[59] = "The old typewriter, a relic from a bygone era, sat on the antique desk in the corner of the room. Its keys clacked with a nostalgic resonance as Michael channeled the spirit of classic literature, transported back in time by the mechanical symphony of the vintage machine. The ticking of the typewriter became a metronome for his words, each keystroke a deliberate step into a literary realm that echoed the elegance of days past."

        var currentPassageIndex by remember { mutableStateOf(0) }
        var userPosition by remember { mutableStateOf(0) }
        var startTime by remember { mutableStateOf(0L) }
        var wpm by remember { mutableStateOf(0) }
        var savedWpm by remember { mutableStateOf(0) }
        var youWin by remember { mutableStateOf(false) }
        var showStartButton by remember { mutableStateOf(true) }
        var wordsTyped by remember { mutableStateOf(0) }
        var totalWords by remember { mutableStateOf(0) }
        var raceID by remember { mutableStateOf(-1) }
        var difficulty by remember { mutableStateOf(0)}
        val coroutineScope = rememberCoroutineScope()

        fun getTotalWords(passage: String): Int {
            val words = passage.trim().split("\\s+".toRegex())
            return words.size
        }

        fun getPassageIndex(challengeTextId : Int): Int {
            if (challengeTextId != -1) {
                return challengeTextId
            } else {
                return difficulty * 20 + (0 until 20).random()
            }

        }

        @Composable
        fun getDifficultyButtonColor(buttonDifficulty: Int): ButtonColors {
            if (difficulty == buttonDifficulty) {
                return ButtonDefaults.buttonColors(backgroundColor = Color.Green)
            } else {
                return ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            }
        }

        @Composable
        fun difficultyButtonGroup(): Unit {
            if (UserState.acceptedChallenge.textId == -1) {
                return (
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        Button(
                            onClick = { difficulty = 0; },
                            colors = getDifficultyButtonColor(0)
                        ) {
                            Text("Easy")
                        }
                        Button(
                            onClick = { difficulty = 1; },
                            colors = getDifficultyButtonColor(1)
                        ) {
                            Text("Medium")
                        }
                        Button(
                            onClick = { difficulty = 2; },
                            colors = getDifficultyButtonColor(2)
                        ) {
                            Text("Hard")
                        }
                    }
                )
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
                savedWpm = wpm
                val success = submitRaceResult(currentUserState.currentUser.userId, savedWpm, currentPassageIndex)
                if (success != -1) {
                    println("[SUCCESSFUL] SUBMITTING RACE")
                    raceID = success
                    if (currentUserState.acceptedChallengeRace.challengeRaceId != -1) {
                        submitResult(currentUserState.acceptedChallengeRace.challengeRaceId, raceID)
                        currentUserState.acceptedChallengeRace.challengeRaceId = -1
                    }
                } else {
                    println("[FAILED] SUBMITTING RACE")
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
                    onClick = { startNewRace(); showStartButton = false; currentUserState.acceptedChallenge.textId = -1}
                ) {
                    Text("Start Race")
                }
                difficultyButtonGroup()
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
                    difficultyButtonGroup()
                }

                if (youWin) {
                    Text("WPM: $savedWpm")
                } else {
                    Text("WPM: $wpm")
                }

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

// Aet user ID by username
suspend fun getUserIDByUsername(username: String): Int {
    val getUserIDEndpoint = "http://localhost:5050/users/id?username=$username"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    try {
        val response: HttpResponse = client.get(getUserIDEndpoint)
        client.close()
        // Return the user ID
        return Json.decodeFromString(response.bodyAsText())
    } catch (e: Exception) {
        // Handle exceptions if needed
        return -1
    }
}

suspend fun submitChallenge(currentuserId: Int, challengeuserName: String, currenttextID: Int, currentraceID: Int): Boolean {
    // Check if the user is trying to challenge themselves
    // If so, don't allow it
    if (currentuserId == getUserIDByUsername(challengeuserName)) {
        return false
    }

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

suspend fun submitResult(fromRaceId: Int, toRaceId: Int): Boolean {
    val insertResultEndpoint = "http://localhost:5050/insertResult"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    try {

        val response: HttpResponse = client.post(insertResultEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(InsertResultRequest(fromRaceId, toRaceId))
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
        // If they deleted (1 character, multiple characters)
        if (currentInput.length < prevValue.length) {
            val diff = prevValue.length - currentInput.length
            if(diff == 1) {
                // Single letter deleted case
                value = currentInput
                errorPosition -= 1
                if (errorPosition < userPosition) {
                    userPosition = errorPosition
                }

            } else {
                // Multiple letters deleted case
                value = ""
                userPosition -= (diff - (errorPosition - userPosition))
                errorPosition -= diff
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

    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Green)) {
                append(passage.substring(0, userPosition))
            }
            withStyle(style = SpanStyle(background = Color.Red.copy(alpha = 0.6f))) {
                append(passage.substring(userPosition, errorPosition))
            }
            append(passage.substring(errorPosition))
        },
        modifier = Modifier.padding(0.dp, 50.dp, 20.dp, 20.dp)
    )

    TextField(
        value = value,
        onValueChange = { update(it) },
        textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(0.dp, 50.dp, 20.dp, 20.dp),
    )
}

