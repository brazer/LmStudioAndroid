package com.salanevich.lmstudioandroid.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salanevich.lmstudioandroid.ui.theme.LmStudioAndroidTheme

@Composable
fun InputFieldWithButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    buttonEnabled: Boolean = true,
    value: String = "",
    placeholder: String = "",
    errorMessage: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    reset: Boolean = false,
    action: (String) -> Unit
) {
    var text by remember(value) { mutableStateOf(value) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val supportingText: @Composable (() -> Unit)? = if (errorMessage != null) {
            @Composable { Text(text = errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null
        OutlinedTextField(
            modifier = Modifier
                .padding(end = 4.dp)
                .weight(1f)
                .focusRequester(focusRequester),
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            value = text,
            trailingIcon = trailingIcon,
            isError = errorMessage != null,
            supportingText = supportingText,
            onValueChange = { text = it }
        )
        Button(
            modifier = Modifier.padding(top = 4.dp),
            onClick = {
                action(text)
                if (reset) {
                    text = ""
                    focusManager.clearFocus()
                }
            },
            enabled = text.isNotEmpty() && buttonEnabled
        ) {
            Text(text = buttonText)
        }
    }
}

@Preview
@Composable
private fun InputFieldWithButtonPreview() {
    LmStudioAndroidTheme {
        InputFieldWithButton(buttonText = "Submit", errorMessage = "Error", placeholder = "Label") {}
    }
}