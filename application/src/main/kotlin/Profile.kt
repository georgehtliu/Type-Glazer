import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun SignUpPrompt(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isSignInMode: Boolean,
    onSubmit: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

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
                .focusRequester(focusRequester)
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
            modifier = Modifier.fillMaxWidth().onKeyEvent { event ->
                if (event.key == Key.Enter) {
                    onSubmit()
                    true
                } else {
                    false
                }
            }
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
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
