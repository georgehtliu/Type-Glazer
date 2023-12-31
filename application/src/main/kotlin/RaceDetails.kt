import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

data class Challenge(
    val username: String,
    val score: Int
)
@Serializable
data class ResultInfo(val user1ID: Int, val user2ID: Int, val user1WPM: Int, val user2WPM: Int, val username1: String, val username2: String)

@Serializable
data class ResultResponse(val resultID: Int, val user1ID: Int, val user2ID: Int, val user1WPM: Int, val user2WPM: Int, val username1: String, val username2: String)
@Serializable
data class ResultListResponse(val results: List<ResultResponse>)

var ResultInfoList = mutableListOf<ResultInfo>()

suspend fun getResults(currentuserId: Int): Boolean {
    val getResultsEndpoint = "http://localhost:5050/getResult?userID=$currentuserId"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    try {

        print("hello 0")


        val responseBody: String = client.get(getResultsEndpoint).body()

        print("helo 1")
        print(responseBody)

        val resultListResponse: ResultListResponse = Json.decodeFromString("""{
    "results": $responseBody
}""")

        print("helo 2")
        val resultResList: List<ResultResponse> = resultListResponse.results

        print("helo 3")

        print(resultResList)

        // Populate RaceInfoList with data from sortedRaceResList
        ResultInfoList = resultResList.mapIndexed { index, resultResponse ->
            ResultInfo(resultResponse.user1ID, resultResponse.user2ID, resultResponse.user1WPM, resultResponse.user2WPM, resultResponse.username1, resultResponse.username2)
        }.toMutableList()

        client.close()
        return true
    } catch (e: Exception) {
        print("AN ERROR OCCUREED")
        print(e)
        // Handle exceptions if needed
        return false
    }
}

@Composable
fun ChallengeDetails(currentUserState: UserState) {

    var localResultInfoList by remember { mutableStateOf(listOf<ResultInfo>()) }
    var noRaces by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val success = getResults(currentUserState.currentUser.userId)
        if (success) {
            localResultInfoList = ResultInfoList
            noRaces = localResultInfoList.isEmpty()
        }
    }

    val userDetails: MutableList<Challenge> = mutableListOf()
    val otherDetails: MutableList<Challenge> = mutableListOf()

    for (result in localResultInfoList) {
        userDetails += Challenge(username = result.username1, score = result.user1WPM)
        otherDetails += Challenge(username = result.username2, score = result.user2WPM)
    }


    val myverticalAlignment = if (noRaces || localResultInfoList.isEmpty()) {
        Arrangement.Center
    } else {
        Arrangement.Top
    }

    MaterialTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 16.dp, 16.dp, 75.dp),
            verticalArrangement = myverticalAlignment,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (noRaces || localResultInfoList.isEmpty()) {
                item {
                    Text("There is no head to head data. Challenge your friends to see your results!")
                }
            } else {
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

                items(userDetails.size) { index ->
                    ChallengeRow(userDetails[index], otherDetails[index])
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
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



