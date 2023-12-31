


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ChallengeResponse(val challengeID: Int, val fromUsername: String, val toUserID: Int, val textID: Int, val raceID: Int)

@Serializable
data class ChallengeListResponse(val challenges: List<ChallengeResponse>)

@Serializable
data class ChallengeInfo(val fromUsername: String, val textID: Int, val challengeID: Int, val challengeRaceID: Int)

var ChallengeList = mutableListOf<ChallengeInfo>()

suspend fun getChallenges(currentuserId: Int): Boolean {
    val getChallengesEndpoint = "http://localhost:5050/challenges/get?userId=$currentuserId"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    try {

        println("sending request")

        val responseBody: String = client.get(getChallengesEndpoint).body()

        println(responseBody)

        val challengeListResponse: ChallengeListResponse = Json.decodeFromString("""{
    "challenges": $responseBody
}""")
        println(challengeListResponse)
        val challengeResList: List<ChallengeResponse> = challengeListResponse.challenges
        println(challengeResList)

        // Sort the raceResList based on raceID
        val sortedChallengeResList = challengeResList.sortedBy { it.challengeID }

        println(sortedChallengeResList)

        ChallengeList = sortedChallengeResList.mapIndexed { index, challengeResponse ->
            ChallengeInfo(challengeResponse.fromUsername, challengeResponse.textID, challengeResponse.challengeID, challengeResponse.raceID)
        }.toMutableList()

        println(ChallengeList)

        client.close()
        return true
    } catch (e: Exception) {
        // Handle exceptions if needed
        return false
    }
}

suspend fun deleteChallenge(challengeID: Int) : Boolean {
    val getChallengesEndpoint = "http://localhost:5050/challenges/delete?challengeId=$challengeID"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    try {

        println("sending delete request")

        val response: HttpResponse = client.delete(getChallengesEndpoint).body()

        client.close()
        return response.status.value in 200..299
    } catch (e: Exception) {
        // Handle exceptions if needed
        return false
    }
}


@Composable
fun MyChallenges(onAccept: () -> Unit, userState: UserState, ) {
    var challenges by remember { mutableStateOf(listOf<ChallengeInfo>()) }

    val coroutineScope = rememberCoroutineScope()

    val refreshChallenges = suspend {
        val success = getChallenges(userState.currentUser.userId)
        if (success) {
            println("here are the challenges")
            println(ChallengeList)
            challenges = ChallengeList
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            refreshChallenges()
        }
    }

    val myAlignment : Alignment = if (challenges.isEmpty()) {
        Alignment.Center
    } else {
        Alignment.TopStart
    }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp, 16.dp, 16.dp, 75.dp),
            contentAlignment = myAlignment,
        ) {
            if (challenges.isEmpty()) {
                Text("There are no incoming challenges.")
            } else {
                LazyColumn {
                    items(challenges) { challenge ->
                        ChallengeRow(
                            challenge = challenge,
                            onAccept,
                            userState.acceptedChallenge,
                            userState.acceptedChallengeRace,
                            refreshChallenges
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun ChallengeRow(challenge: ChallengeInfo, onAccept: () -> Unit, acceptedChallenge: challengeAcceptedTextId, acceptedChallengeRace: challengeAcceptedRaceId, refreshChallenges: suspend () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display challenge details
        Text(text = "From: ${challenge.fromUsername}")

        // Accept and reject buttons
        Spacer(modifier = Modifier.weight(1f))
        ChallengeButton(
            text = "Accept", onClick = {
            coroutineScope.launch(Dispatchers.Default) {
                acceptedChallenge.textId = challenge.textID
                acceptedChallengeRace.challengeRaceId = challenge.challengeRaceID
                val success = deleteChallenge(challenge.challengeID)
                if (success) {
                    refreshChallenges()
                }
                onAccept()
            }
        },
        color = Color(0xffadf7a4),
        textColor = Color(0xff00ad0e),
        )

        Spacer(modifier = Modifier.width(8.dp))
        ChallengeButton(
            text = "Reject", onClick = {
            coroutineScope.launch(Dispatchers.Default) {
                val success = deleteChallenge(challenge.challengeID)
                if (success) {
                    refreshChallenges()
                }
            }
        },
        color = Color(0xffffcccf),
        textColor = Color(0xffca1e17),
        )
    }
}

@Composable
fun ChallengeButton(text: String, onClick: () -> Unit, color: Color, textColor: Color) {
    Button(
        colors = ButtonDefaults.buttonColors(contentColor = textColor, backgroundColor = color),
        onClick = { onClick() }
    ) {
        Text(text = text)
    }
}


