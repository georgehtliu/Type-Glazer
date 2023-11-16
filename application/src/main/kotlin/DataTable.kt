import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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

data class RaceInfo(val raceNumber: Int, val date: String, val wpm: Int)

@Serializable
data class RaceResponse(val raceID: Int, val userID: Int, val textID: Int, val date: String, val wpm: Int)

@Serializable
data class RaceListResponse(val races: List<RaceResponse>)

var RaceInfoList = mutableListOf<RaceInfo>()

suspend fun getRaces(currentuserId: Int): Boolean {
    val getRacesEndpoint = "http://localhost:5050/races?userId=$currentuserId"
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    try {
        val responseBody: String = client.get(getRacesEndpoint).body()

        val raceListResponse: RaceListResponse = Json.decodeFromString(responseBody)
        val raceResList: List<RaceResponse> = raceListResponse.races

        // Sort the raceResList based on raceID
        val sortedRaceResList = raceResList.sortedBy { it.raceID }

        // Populate RaceInfoList with data from sortedRaceResList
        RaceInfoList = sortedRaceResList.mapIndexed { index, raceResponse ->
            RaceInfo(index + 1, raceResponse.date, raceResponse.wpm)
        }.toMutableList()

        client.close()
        return true
    } catch (e: Exception) {
        // Handle exceptions if needed
        return false
    }
}

@Composable
fun DataTable(currentUserState: UserState) {

    var localRaceInfoList by remember { mutableStateOf(listOf<RaceInfo>()) }

    LaunchedEffect(Unit) {
        val success = getRaces(currentUserState.currentUser.userId)
        if (success) {
            localRaceInfoList = RaceInfoList
        }
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Display the time graph
            TimeGraphCanvas(localRaceInfoList)

            Spacer(modifier = Modifier.height(16.dp))

            // Display the table
            LazyColumn(
                Modifier.padding(8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RowItem("Race Number", true)
                        RowItem("Date", true)
                        RowItem("WPM", true)
                    }

                    Divider(
                        color = Color.LightGray
                    )
                }

                items(localRaceInfoList) { raceInfo ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RowItem(raceInfo.raceNumber.toString(), false)
                        RowItem(raceInfo.date, false)
                        RowItem(raceInfo.wpm.toString(), false)
                    }
                    Divider(
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.RowItem(text: String, title: Boolean) {
    val weight: Float = 0.2f
    Text(
        text = text,
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier.weight(weight).padding(10.dp)
    )
}

@Composable
fun TimeGraphCanvas(raceData: List<RaceInfo>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray)
    ) {
        val maxX = raceData.size.toFloat()
        val maxY = raceData.maxByOrNull { it.wpm }?.wpm?.toFloat() ?: 1f

        // Draw x-axis
        drawLine(start = Offset(0f, size.height), end = Offset(size.width, size.height), color = Color.Black)

        // Draw y-axis
        drawLine(start = Offset(0f, 0f), end = Offset(0f, size.height), color = Color.Black)

        // Draw data points
        raceData.forEachIndexed { index, data ->
            val x = (index + 1).toFloat() / maxX * size.width
            val y = size.height - (data.wpm.toFloat() / maxY * size.height)

            drawCircle(color = Color.Blue, radius = 8f, center = Offset(x, y))
        }
    }
}