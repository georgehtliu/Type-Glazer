import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun SignUpPrompt(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isSignInMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { onUsernameChange(it) },
            label = { Text("Username") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { onPasswordChange(it) },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(password.length),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@Composable
fun BugReportForm(onSubmit: (String) -> Unit) {
    var bugDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Found a bug on the website? Let us know!")

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = bugDescription,
            onValueChange = { bugDescription = it },
            label = { Text("Bug Description") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { onSubmit(bugDescription) }) {
            Text("Submit Bug Report")
        }
    }
}

class PasswordVisualTransformation(private val passwordLength: Int) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val passwordText = "*".repeat(passwordLength)
        return TransformedText(
            buildAnnotatedString { append(passwordText) },
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return minOf(offset, passwordText.length)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return minOf(offset, text.length)
                }
            }
        )
    }
}

fun runQueryOnClick () {
    println("signup")
}

suspend fun queryWebsite(): String {
    val site = "https://ktor.io/"
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(site)
    client.close()

    if (response.status.value in 200..299) {
        return "SITE: ${site} \nSTATUS: ${response.status.value}\nHEADER: ${response.headers}\nCONTENT: ${response.body<String>().length} bytes\n"
    } else
        return "STATUS: ${response.status.value}"
}
