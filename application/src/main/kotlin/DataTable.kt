import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun DataTable(currentuserId: Int) {

    var localRaceInfoList by remember { mutableStateOf(listOf<RaceInfo>()) }

    LaunchedEffect(Unit) {
        val success = getRaces(currentuserId)
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
            // Display the time graph
            Spacer(modifier = Modifier.height(16.dp))
            TimeGraphCanvas(localRaceInfoList)
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
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val maxX = raceData.size.toFloat()
        val maxY = raceData.maxByOrNull { it.wpm }?.wpm?.toFloat() ?: 1f

        val paddingStart = 40f
        val paddingEnd = 40f
        val plotWidth = size.width - paddingStart - paddingEnd

        // Draw x-axis
        drawLine(start = Offset(paddingStart, size.height), end = Offset(size.width - paddingEnd, size.height), color = Color.Black)

        // Draw y-axis
        drawLine(start = Offset(paddingStart, 0f), end = Offset(paddingStart, size.height), color = Color.Black)

        // Draw data points
        raceData.forEachIndexed { index, data ->
            val x = paddingStart + ((index.toFloat() / (maxX - 1)) * plotWidth)
            val y = size.height - ((data.wpm.toFloat() / maxY) * (size.height - 20))

            drawCircle(color = Color.Blue, radius = 3f, center = Offset(x, y))

            val dateText =
                textMeasurer.measure(
                    AnnotatedString("Race ${data.raceNumber}"),
                    TextStyle(fontSize = 10.sp)
                )

            drawText(
                textLayoutResult = dateText,
                topLeft = Offset(x, size.height + 16f),
            )

            val yLabelInterval = maxY / 5
            for (i in 0..5) {
                val label = (yLabelInterval * i).toInt().toString()
                val labelLayout = textMeasurer.measure(
                    AnnotatedString(label),
                    TextStyle(fontSize = 10.sp)
                )
                // val mark = size.height - (i * (size.height / 5))
                val mark = size.height - ((i * yLabelInterval / maxY) * (size.height - 20))
                drawText(
                    textLayoutResult = labelLayout,
                    topLeft = Offset(paddingStart - 20f, mark - 8f)
                )

                drawLine(start = Offset(paddingStart - 5f, mark), end = Offset(paddingStart, mark), color = Color.Black)
            }

            if (index > 0) {
                val prevX = paddingStart + (((index - 1).toFloat() / (maxX - 1)) * plotWidth)
                val prevY = size.height - ((raceData[index - 1].wpm.toFloat() / maxY) * (size.height - 20))
                drawLine(start = Offset(prevX, prevY), end = Offset(x, y), color = Color.Blue)
            }

        }
    }
}