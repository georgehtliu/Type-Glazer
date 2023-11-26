


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ChallengeResponse(val challengeID: Int, val fromUsername: String, val toUserID: Int, val textID: Int, val raceID: Int)

@Serializable
data class ChallengeListResponse(val challenges: List<ChallengeResponse>)

@Serializable
data class ChallengeInfo(val fromUsername: String, val textID: Int, val challengeID: Int)

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
            ChallengeInfo(challengeResponse.fromUsername, challengeResponse.textID, challengeResponse.challengeID)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (challenges.isEmpty()) {
            Text("There are no challenges.")
        } else {
            LazyColumn {
                items(challenges) { challenge ->
                    ChallengeRow(challenge = challenge, onAccept, userState.acceptedChallenge, refreshChallenges)
                }
            }
        }
    }
}




@Composable
fun ChallengeRow(challenge: ChallengeInfo, onAccept: () -> Unit, acceptedChallenge: challengeAcceptedTextId, refreshChallenges: suspend () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display challenge details
        Text(text = "From: ${challenge.fromUsername}")
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Text ID: ${challenge.textID}")

        // Accept and reject buttons
        Spacer(modifier = Modifier.weight(1f))
        ChallengeButton(text = "Accept", onClick = {
            coroutineScope.launch(Dispatchers.Default) {
                acceptedChallenge.textId = challenge.textID
                onAccept()
                deleteChallenge(challenge.challengeID)
            }
        })
        Spacer(modifier = Modifier.width(8.dp))
        ChallengeButton(text = "Reject", onClick = {
            coroutineScope.launch(Dispatchers.Default) {
                val success = deleteChallenge(challenge.challengeID)
                if (success) {
                    refreshChallenges()
                }
            }
        })
    }
}

@Composable
fun ChallengeButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(Color.Gray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White)
    }
}


