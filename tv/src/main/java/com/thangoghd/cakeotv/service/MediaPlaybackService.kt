package com.thangoghd.cakeotv.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
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
        private const val TAG = "MediaPlaybackService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "media_playback_channel"
        private var mediaSession: MediaSession? = null
            private set
        private var service: MediaPlaybackService? = null
            private set
        
        var player: Player? = null
            set(value) {
                Log.d(TAG, "Setting player: ${value != null}")
                field = value
                value?.let { 
                    service?.let { service ->
                        Log.d(TAG, "Initializing session from setter")
                        service.initializeSession(it)
                    } ?: Log.w(TAG, "Service not ready when setting player")
                }
            }
    }

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    private var isBackgroundPlayback = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        service = this
        
        // Create notification channel first
        createNotificationChannel()
        
        // Start foreground with an empty notification only if not in background playback
        if (!isBackgroundPlayback) {
            val emptyNotification = buildEmptyNotification().build()
            startForeground(NOTIFICATION_ID, emptyNotification)
        }
        
        // Initialize player if it exists
        player?.let { 
            Log.d(TAG, "Player already exists, initializing session from onCreate")
            initializeSession(it)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand")
        
        // Check if this is a background playback request
        intent?.getBooleanExtra("background_playback", false)?.let {
            isBackgroundPlayback = it
            Log.d(TAG, "Background playback mode: $it")
        }
        
        return START_REDELIVER_INTENT
    }

    @UnstableApi
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.d(TAG, "onGetSession: ${mediaSession != null}")
        return mediaSession
    }

    private fun initializeSession(player: Player) {
        Log.d(TAG, "initializeSession start")
        try {
            // Don't recreate session if it already exists with same player
            if (mediaSession?.player == player) {
                Log.d(TAG, "MediaSession already exists for this player")
                return
            }

            // Release existing session if it exists
            mediaSession?.release()
            
            // Create a new session
            mediaSession = MediaSession.Builder(this, player)
                .setSessionActivity(
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, PlayerActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
                .build()
            
            // Only update notification if not in background playback mode
            if (!isBackgroundPlayback) {
                Log.d(TAG, "Updating notification with media session")
                val notification = buildNotification().build()
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
            
            Log.d(TAG, "MediaSession initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MediaSession", e)
        }
    }

    @OptIn(UnstableApi::class)
    private fun buildNotification(): NotificationCompat.Builder {
        Log.d(TAG, "Building notification")
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

    @OptIn(UnstableApi::class)
    private fun buildEmptyNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Media Playback")
            .setContentText("Preparing...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        Log.d(TAG, "Creating notification channel")
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
        Log.d(TAG, "onDestroy")
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        service = null
        super.onDestroy()
    }
}
