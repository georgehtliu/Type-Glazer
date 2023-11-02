import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


data class RaceInfo(val raceNumber: Int, val date: String, val wpm: Int)

val RaceInfoList = listOf(
    RaceInfo(1, "01/04/2023", 30),
    RaceInfo(2, "01/05/2023", 70),
    RaceInfo(3, "01/09/2023", 10)
)


@Composable
fun DataTable() {
    LazyColumn(
        Modifier.padding(8.dp)
    ) {
        item {
            Row (
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

        items(RaceInfoList) { raceInfo ->
            Row (
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

@Composable
fun RowScope.RowItem(text: String, title: Boolean) {
    val weight : Float = 0.2f
    Text (
        text = text,
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier.weight(weight).padding(10.dp)
    )
}


