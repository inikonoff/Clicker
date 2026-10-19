package com.inikonoff.clicker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import androidx.media.VolumeProviderCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ClickerMediaService : Service() {
    private lateinit var mediaSession: MediaSessionCompat
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        createChannel()
        setupMediaSession()
        startForeground(1, buildNotification(0))
        // Update notification with count
        scope.launch {
            CounterRepository.state.collectLatest { state ->
                val nm = getSystemService(NotificationManager::class.java)
                nm.notify(1, buildNotification(state.count))
            }
        }
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "ClickerMedia").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() { isActive = true }
                override fun onPause() { isActive = false }
                override fun onStop() { stopSelf() }
            })
            isActive = true

            // Critical: VolumeProvider so system routes volume keys to us even on lock screen
            val volumeProvider = object : VolumeProviderCompat(
                VolumeProviderCompat.VOLUME_CONTROL_RELATIVE,
                100,
                50
            ) {
                override fun onAdjustVolume(direction: Int) {
                    when (direction) {
                        AudioManager.ADJUST_RAISE -> {
                            CounterRepository.plusOne()
                            vibrateIfEnabled(this@ClickerMediaService, isMinus = false)
                        }
                        AudioManager.ADJUST_LOWER -> {
                            CounterRepository.minusOne()
                            vibrateIfEnabled(this@ClickerMediaService, isMinus = true)
                        }
                    }
                }
            }
            setPlaybackToRemote(volumeProvider)

            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_STOP)
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1f)
                    .build()
            )
            setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Кликер")
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Клавиши громкости")
                    .build()
            )
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("clicker", "Clicker", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(count: Int): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, ClickerMediaService::class.java).apply { action = "STOP" },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, "clicker")
            .setContentTitle("Кликер: $count")
            .setContentText("Клавиши громкости активны")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(openIntent)
            .addAction(0, "Стоп", stopIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0)
            )
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP") {
            stopSelf()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        mediaSession.release()
        super.onDestroy()
    }
}

internal fun vibrateIfEnabled(context: android.content.Context, isMinus: Boolean) {
    // Simple check — in real app read from SettingsRepository
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (isMinus) {
            // Two short pulses for minus
            vibrator.vibrate(android.os.VibrationEffect.createWaveform(longArrayOf(0, 30, 60, 30), -1))
        } else {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(30, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
