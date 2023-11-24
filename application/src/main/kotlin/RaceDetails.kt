import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class Challenge(
    val username: String,
    val score: Int
)

@Composable
fun ChallengeDetails() {
    val userDetails = Challenge(username = "bobsmith", score = 80)
    val othersDetails =  listOf(
        Challenge(username = "bob", score = 80),
        Challenge(username = "samsmith", score = 75),
        Challenge(username = "janesmith", score = 90),
        Challenge(username = "smith", score = 85)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RowItem("Player", true)
                RowItem("WPM", true)
                RowItem("Status", true)
            }

            Divider(
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        items(othersDetails) { detail ->
                ChallengeRow(userDetails, detail)
                Divider(
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

@Composable
fun ChallengeRow(user: Challenge, other: Challenge) {
    val (userStatus, otherStatus) = when {
        user.score > other.score -> Pair("Win", "Lose")
        user.score < other.score -> Pair("Lose", "Win")
        else -> Pair("Tie", "Tie")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RowItem(user.username, false)
        RowItem(user.score.toString(), false)
        StatusCell(userStatus)
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RowItem(other.username, false)
        RowItem(other.score.toString(), false)
        StatusCell(otherStatus)
    }
}

@Composable
fun RowScope.StatusCell(
    text: String,
    alignment: TextAlign = TextAlign.Center,
) {
    val color = when (text) {
        "Win" -> Color(0xffadf7a4)
        "Lose" -> Color(0xffffcccf)
        else -> Color(0xffd0e5f6)
    }

    val textColor = when (text) {
        "Win" -> Color(0xff00ad0e)
        "Lose" -> Color(0xffca1e17)
        else -> Color(0xff0a6dbe)
    }

    Box(
        modifier = Modifier
            .weight(.2f)
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        Box(
            modifier = Modifier
                .background(color, shape = CircleShape)
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                textAlign = alignment,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}



