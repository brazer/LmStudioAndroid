package com.salanevich.lmstudioandroid.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salanevich.lmstudioandroid.R
import com.salanevich.lmstudioandroid.ui.theme.LmStudioAndroidTheme

@Composable
fun ModelSelection(
    modifier: Modifier = Modifier,
    models: List<String>,
    applyButton: @Composable (String) -> Unit = {},
    reloadButton: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.select_a_model))
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(models[0]) }
        Column(
            modifier = modifier
                .selectableGroup()
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            models.forEach { text ->
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .padding(start = 16.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = androidx.compose.ui.semantics.Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = modifier.padding(start = 16.dp)
                    )
                }
            }
        }
        Row {
            applyButton(selectedOption)
            Spacer(modifier = Modifier.width(4.dp))
            reloadButton()
        }
    }
}

@Preview
@Composable
private fun ModelSelectionPreview() {
    LmStudioAndroidTheme {
        ModelSelection(
            models = listOf("model1", "model2", "model3"),
            applyButton = {
                Button(onClick = {}) {
                    Text(text = "Apply")
                }
            },
            reloadButton = {
                Button(onClick = {}) {
                    Text(text = "Reload")
                }
            }
        )
    }
}