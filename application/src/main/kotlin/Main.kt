import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.Modifier
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Composable
fun App() {
    MaterialTheme {
        var progress by remember { mutableStateOf(0.0f) }
        var passages by remember {
            mutableStateOf(
                listOf(
                    "The quick brown fox jumped over the lazy dog and cat and mouse and fish",
                    "The sun is shining, the birds are singing, and the flowers are blooming",
                    "Education is the most powerful weapon which you can use to change the world",
                    "Happiness is not something ready-made. It comes from your own actions"
                )
            )
        }
        var currentPassageIndex by remember { mutableStateOf(0) }
        var passageWords by remember { mutableStateOf(passages[currentPassageIndex].split(" ")) }
        var userPosition by remember { mutableStateOf(0) }
        var startTime by remember { mutableStateOf(0L) }
        var wpm by remember { mutableStateOf(0) }
        var youWin by remember { mutableStateOf(false) }

        fun startNewRace() {
            currentPassageIndex = (0 until passages.size).random()
            passageWords = passages[currentPassageIndex].split(" ")
            userPosition = 0
            startTime = 0L
            wpm = 0
            youWin = false
        }

        if (userPosition >= passageWords.size && !youWin) {
            youWin = true
        }

        progress = userPosition.toFloat() / passageWords.size

        if (userPosition > 0 && startTime > 0) {
            val timeElapsedMinutes = (System.currentTimeMillis() - startTime) / 60000.0
            if (timeElapsedMinutes > 0) {
                wpm = (userPosition / timeElapsedMinutes).toInt()
            } else {
                wpm = 0
            }
        }

        Column {
            Spacer(modifier = Modifier.size(100.dp))

            FullWidthDottedLine(
                color = Color.Black,
                dotRadius = 2f,
                spacing = 8f,
                lineWidth = 2f,
                progress = progress
            )

            Spacer(modifier = Modifier.size(30.dp))

            if (!youWin) {
                Typer(passageWords) { typedWords ->
                    userPosition = typedWords
                    if (startTime == 0L) {
                        startTime = System.currentTimeMillis()
                        // Start a coroutine to update WPM
                        GlobalScope.launch {
                            while (userPosition < passageWords.size && !youWin) {
                                val elapsedTime = (System.currentTimeMillis() - startTime) / 60000.0
                                if (elapsedTime > 0) {
                                    wpm = (userPosition / elapsedTime).toInt()
                                }
                                delay(1000)
                            }
                        }
                    }
                }
            } else {
                Text("You Win!")
                Button(
                    onClick = { startNewRace() }
                ) {
                    Text("Start New Race")
                }
            }

            Text("Words typed: $userPosition / ${passageWords.size}")
            Text("WPM: $wpm")
        }
    }
}


@Composable
fun FullWidthDottedLine(
    color: Color = Color.Black,
    dotRadius: Float = 2f,
    spacing: Float = 8f,
    lineWidth: Float = 2f,
    progress: Float
) {
    Canvas(
        modifier = Modifier.fillMaxWidth()
    ) {
        val startY = size.height / 2
        val endX = size.width * progress
        var currentX = 0f

        while (currentX < endX) {
            drawCircle(
                color = color,
                radius = dotRadius,
                center = Offset(currentX, startY),
                style = Stroke(width = lineWidth)
            )
            currentX += spacing
        }
    }
}


@Composable
fun Typer(passageWords: List<String>, onWordsTyped: (Int) -> Unit) {
    var inputText by remember { mutableStateOf("") }
    var lettersTyped by remember { mutableStateOf(List<Boolean>(0) { false }) }
    var wordsTyped by remember { mutableStateOf(0) }
    var raceOver by remember { mutableStateOf(false) }
    var wrongLetters by remember { mutableStateOf(List<Boolean>(0) { false }) }

    fun update(currentInput: String) {
        // Check if the current input matches the next word in the passage
        if (!raceOver) {
            if (currentInput.endsWith(" ") && wordsTyped < passageWords.size) {
                if (passageWords[wordsTyped] == currentInput.trim()) {
                    wordsTyped++
                    // Mark the word as correctly typed
                    if (wordsTyped < passageWords.size) {
                        lettersTyped = List(passageWords[wordsTyped].length) { false }
                        wrongLetters = List(passageWords[wordsTyped].length) { false }
                    }
                } else {
                    // Mark letters in the word as incorrectly typed
                    lettersTyped = List(passageWords[wordsTyped].length) { false }
                    wrongLetters = List(passageWords[wordsTyped].length) { true }
                }
                // Clear the input text
                inputText = ""
            } else {
                // Check and mark individual letters in the current word
                val currentWord = passageWords.getOrNull(wordsTyped) ?: ""
                val inputWord = currentInput.trim()
                val newLettersTyped = mutableListOf<Boolean>()
                val newWrongLetters = mutableListOf<Boolean>()
                for (i in 0 until currentWord.length) {
                    if (i < inputWord.length) {
                        newLettersTyped.add(inputWord[i] == currentWord[i])
                        newWrongLetters.add(inputWord[i] != currentWord[i])
                    } else {
                        newLettersTyped.add(false)
                        newWrongLetters.add(false)
                    }
                }
                lettersTyped = newLettersTyped
                wrongLetters = newWrongLetters
                inputText = currentInput
            }

            if (wordsTyped == passageWords.size) {
                raceOver = true
            }
        }

        onWordsTyped(wordsTyped)
    }

    // Passage text display
    Column {
        Row {
            for (wordIdx in 0 until passageWords.size) {
                val word = passageWords.getOrNull(wordIdx) ?: ""
                for (letterIdx in 0 until word.length) {
                    val isTypedCorrectly =
                        wordIdx < wordsTyped || (wordIdx == wordsTyped && lettersTyped.getOrNull(letterIdx) == true)
                    val isWrongLetter = wordIdx == wordsTyped && wrongLetters.getOrNull(letterIdx) == true
                    val letterColor =
                        if (isTypedCorrectly) Color.Green else if (isWrongLetter) Color.Red else Color.Black
                    Text(
                        text = word[letterIdx].toString(),
                        color = letterColor,
                    )
                }
                if (wordIdx < passageWords.size - 1) {
                    // Preserve spaces between words
                    Text(" ", color = Color.Transparent)
                }
            }
        }

        // Type Bar
        TextField(
            value = inputText,
            onValueChange = { update(it) },
            textStyle = TextStyle(color = if (wrongLetters.any { it }) Color.Red else Color.Blue),
            modifier = Modifier.padding(0.dp, 50.dp, 20.dp, 20.dp)
        )
    }
}


@Composable
fun Circle(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    diameter: Int = 100
) {
    Canvas(
        modifier = modifier.size(diameter.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = diameter / 2

        drawCircle(
            color = color,
            center = Offset(centerX, centerY),
            radius = radius.toFloat(),
            style = Stroke(2f) // You can adjust the stroke width as needed
        )
    }
}


