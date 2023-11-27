import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
import kotlin.math.max

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
    var noRaces by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val success = getRaces(currentUserState.currentUser.userId)
        if (success) {
            localRaceInfoList = RaceInfoList
            noRaces = localRaceInfoList.isEmpty()
        }
    }

    val myverticalAlignment = if (noRaces || localRaceInfoList.isEmpty()) {
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
            horizontalAlignment = Alignment.CenterHorizontally) {
            if (noRaces || localRaceInfoList.isEmpty()) {
                item {
                    Text("There is no race data. Play some games to see your progress!")
                }
            } else {
                    // Display the table
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

                // Display the time graph
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    TimeGraphCanvas(localRaceInfoList)
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
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 60.dp)
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

        // Draw x-axis label
        val xAxisLabel = textMeasurer.measure(
            AnnotatedString("Race Number"),
            TextStyle(fontSize = 10.sp)
        )
        drawText(
            textLayoutResult = xAxisLabel,
            topLeft = Offset(size.width / 2, size.height + 40f),
        )

        // Draw title
        val title = textMeasurer.measure(
            AnnotatedString("Typing Progress"),
            TextStyle(fontSize = 16.sp)
        )
        drawText(
            textLayoutResult = title,
            topLeft = Offset(size.width / 2, 30f),
        )

        // Calculate x-axis increment
        val increment = max(1, (maxX / 10).toInt())

        // Draw data points
        raceData.forEachIndexed { index, data ->
            val x = if (maxX > 1) {
                paddingStart + ((index.toFloat() / (maxX - 1)) * plotWidth)
            } else {
                size.width / 2
            }

            val y = size.height - ((data.wpm.toFloat() / maxY) * (size.height - 20))

            drawCircle(color = Color.Blue, radius = 3f, center = Offset(x, y))

            if ((index + 1) % increment == 0) {
                val raceNumber = textMeasurer.measure(
                    AnnotatedString("${index + 1}"),
                    TextStyle(fontSize = 10.sp)
                )
                drawText(
                    textLayoutResult = raceNumber,
                    topLeft = Offset(x, size.height + 16f),
                )
            }

            // Connect the dots with lines
            if (index > 0) {
                val prevX = paddingStart + (((index - 1).toFloat() / (maxX - 1)) * plotWidth)
                val prevY = size.height - ((raceData[index - 1].wpm.toFloat() / maxY) * (size.height - 20))
                drawLine(start = Offset(prevX, prevY), end = Offset(x, y), color = Color.Blue)
            }
        }

        // Draw y-values on the y-axis
        val yIncrement = maxY / 5
        for (i in 0..5) {
            val yValue = (yIncrement * i).toInt()
            val yValueLabel = textMeasurer.measure(
                AnnotatedString("$yValue"),
                TextStyle(fontSize = 10.sp)
            )
            val mark = size.height - ((i * yIncrement / maxY) * (size.height - 20))
            drawText(
                textLayoutResult = yValueLabel,
                topLeft = Offset(paddingStart - 70f, mark + 5f),
            )
        }
    }
}







