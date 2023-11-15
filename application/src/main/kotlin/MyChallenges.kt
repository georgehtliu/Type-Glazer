


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ChallengeData(val fromUser: String, val raceId: String)


@Composable
fun MyChallenges() {
    // Dummy data for testing
    val challenges = remember { mutableStateListOf(
        ChallengeData("User1", "Race123"),
        ChallengeData("User2", "Race456"),
        ChallengeData("User3", "Race789")
    ) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn {
            items(challenges) { challenge ->
                ChallengeRow(challenge = challenge)
            }
        }
    }
}

@Composable
fun ChallengeRow(challenge: ChallengeData) {
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
            // Handle accept action
            // You can update the UI or perform other actions here
            // For now, let's remove the challenge from the list
            // challenges.remove(challenge)
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


