


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

data class ChallengeData(val fromUser: String, val raceId: String)
suspend fun getRaceTextID(raceID: Int): Int {

    // If the provided values don't match, perform the actual login logic
    val getRaceEndpoint = "http://localhost:5050/getRace?raceID=$raceID"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    try {
        val response: HttpResponse = client.get(getRaceEndpoint)
        // Close the client after the request
        client.close()
        return response.bodyAsText().toInt()
    } catch (e: Exception) {
        // Handle exceptions if needed
        print(e)
        return -1
    }
}


@Composable
fun MyChallenges(onAccept: () -> Unit, acceptedChallenge: challengeAcceptedTextId, ) {
    // Dummy data for testing
    val challenges = remember { mutableStateListOf(
        ChallengeData("User1", "1"),
        ChallengeData("User2", "2"),
        ChallengeData("User3", "3")
    ) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn {
            items(challenges) { challenge ->
                ChallengeRow(challenge = challenge, onAccept, acceptedChallenge)
            }
        }
    }
}

@Composable
fun ChallengeRow(challenge: ChallengeData, onAccept: () -> Unit, acceptedChallenge: challengeAcceptedTextId) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display challenge details
        Text(text = "From: ${challenge.fromUser}")
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Race ID: ${challenge.raceId}")

        // Accept and reject buttons
        Spacer(modifier = Modifier.weight(1f))
        ChallengeButton(text = "Accept", onClick = {
            coroutineScope.launch(Dispatchers.Default) {
                acceptedChallenge.textId = getRaceTextID(challenge.raceId.toInt())
                onAccept()
            }
        })
        Spacer(modifier = Modifier.width(8.dp))
        ChallengeButton(text = "Reject", onClick = {
            // Handle reject action
            // You can update the UI or perform other actions here
            // For now, let's remove the challenge from the list
            // challenges.remove(challenge)
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


