package com.inikonoff.clicker

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MainScreen() {
    val state by CounterRepository.state.collectAsStateWithLifecycle()
    var showFinish by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Тап по числу = +шаг
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { CounterRepository.plus() },
                contentAlignment = Alignment.Center,
            ) {
                DigitDisplay(count = state.count, style = "mechanical")
            }

            StepField(step = state.step, onStepChange = CounterRepository::setStep)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = CounterRepository::reset,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(stringResource(R.string.reset)) }
                Button(
                    onClick = { showFinish = true },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(stringResource(R.string.finish)) }
                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(context, WindowActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Оконный режим") }
            }
        }
    }

    if (showFinish) {
        FinishDialog(
            total = state.count,
            onDismiss = { showFinish = false },
            onSave = {
                // TODO: запись в «Мои подсчёты» (Room) — следующий этап
                CounterRepository.reset()
                showFinish = false
            },
        )
    }
}

@Composable
private fun StepField(step: Int, onStepChange: (Int) -> Unit) {
    var text by rememberSaveable { mutableStateOf(step.toString()) }
    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            val digits = input.filter { it.isDigit() }.take(6)
            text = digits
            digits.toIntOrNull()?.takeIf { it >= 1 }?.let(onStepChange)
        },
        label = { Text(stringResource(R.string.step_label)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (!it.isFocused) text = step.toString() },
    )
}

@Composable
private fun FinishDialog(total: Int, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var comment by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.finish_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.finish_total, total),
                    style = MaterialTheme.typography.headlineSmall,
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(stringResource(R.string.comment_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(comment) }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}
