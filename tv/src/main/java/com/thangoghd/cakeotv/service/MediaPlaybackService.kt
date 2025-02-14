package com.thangoghd.cakeotv.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.thangoghd.cakeotv.PlayerActivity
import com.thangoghd.cakeotv.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlaybackService : MediaSessionService() {

    companion object {
       private const val NOTIFICATION_ID = 1
       private const val CHANNEL_ID = "media_playback_channel"
        var player: Player? = null
    }

    private var mediaSession: MediaSession? = null

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeSession()
    }

    @UnstableApi
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    private fun initializeSession() {
        player?.let { exoPlayer ->
            mediaSession?.release()
            mediaSession = MediaSession.Builder(this, exoPlayer)
                .setSessionActivity(
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, PlayerActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
                .build()

            startForeground(NOTIFICATION_ID, buildNotification().build())
        }
    }

    @OptIn(UnstableApi::class)
    private fun buildNotification(): NotificationCompat.Builder {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, PlayerActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Playing media")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.media_playback_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.media_playback_channel_description)
        }

        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        mediaSession?.run {
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
