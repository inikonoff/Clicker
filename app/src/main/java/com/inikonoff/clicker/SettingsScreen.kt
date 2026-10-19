package com.inikonoff.clicker

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val style by SettingsRepository.digitStyle(context).collectAsStateWithLifecycle(initialValue = "mechanical")
    val theme by SettingsRepository.theme(context).collectAsStateWithLifecycle(initialValue = "system")
    val vibration by SettingsRepository.vibration(context).collectAsStateWithLifecycle(initialValue = true)
    val sound by SettingsRepository.sound(context).collectAsStateWithLifecycle(initialValue = false)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Настройки") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Стиль цифр: $style")
            // Simple toggles for demo
            Text("Тема: $theme")
            Switch(checked = vibration, onCheckedChange = {
                CoroutineScope(Dispatchers.IO).launch {
                    SettingsRepository.setVibration(context, it)
                }
            })
            Text("Вибрация")
            Switch(checked = sound, onCheckedChange = {
                CoroutineScope(Dispatchers.IO).launch {
                    SettingsRepository.setSound(context, it)
                }
            })
            Text("Звук")
        }
    }
}

fun vibrate(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(30)
    }
}
