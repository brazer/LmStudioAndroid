package com.salanevich.lmstudioandroid.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salanevich.lmstudioandroid.Preferences
import com.salanevich.lmstudioandroid.R
import com.salanevich.lmstudioandroid.ui.theme.LmStudioAndroidTheme
import com.salanevich.lmstudioandroid.vm.PreferencesIntent
import com.salanevich.lmstudioandroid.vm.PreferencesSideEffect
import com.salanevich.lmstudioandroid.vm.PreferencesState
import com.salanevich.lmstudioandroid.vm.PreferencesViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
    navigateToChat: () -> Unit
) {
    val vm = hiltViewModel<PreferencesViewModel>()
    vm.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is PreferencesSideEffect.GoToChat -> navigateToChat()
        }
    }
    val state = vm.collectAsState()
    PreferencesScreen(
        modifier = modifier,
        state = state,
        applyAction = { url, system -> vm.reduce(PreferencesIntent.Apply(url, system)) },
        navigateToChat = navigateToChat
    )
    LaunchedEffect(Unit) {
        vm.reduce(PreferencesIntent.InitData)
    }
}

@Composable
private fun PreferencesScreen(
    modifier: Modifier = Modifier,
    state: State<PreferencesState>,
    applyAction: (url: String, system: String) -> Unit,
    navigateToChat: () -> Unit
) {
    Scaffold(
        topBar = { Toolbar(navigateToChat) }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (state.value.isLoading) {
                CircularProgressIndicator()
            } else {
                PreferencesScreenMain(state = state, applyAction = applyAction)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(navigateToChat: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(text = stringResource(R.string.preferences)) },
        navigationIcon = {
            IconButton(onClick = navigateToChat) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "back"
                )
            }
        }
    )
}

@Composable
private fun PreferencesScreenMain(
    modifier: Modifier = Modifier,
    state: State<PreferencesState>,
    applyAction: (url: String, system: String) -> Unit
) {
    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var url by remember { mutableStateOf(state.value.url ?: "") }
        OutlinedTextField(
            label = { Text(stringResource(R.string.server_url), color = Color.Gray) },
            value = url,
            onValueChange = { url = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        var system by remember { mutableStateOf(state.value.system ?: "") }
        OutlinedTextField(
            label = { Text(stringResource(R.string.system_prompt), color = Color.Gray) },
            value = system,
            onValueChange = { system = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { applyAction(url, system) }) {
            Text(text = stringResource(R.string.apply))
        }
    }
}

@Preview
@Composable
private fun PreferencesScreenPreview() {
    LmStudioAndroidTheme {
        val state = object : State<PreferencesState> {
            override val value: PreferencesState
                get() = PreferencesState(
                    url = "http://192.168.0.105:1234"
                )
        }
        PreferencesScreen(state = state, applyAction = { _, _ -> }, navigateToChat = {})
    }
}