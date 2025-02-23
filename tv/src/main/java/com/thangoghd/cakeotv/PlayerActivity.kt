package com.thangoghd.cakeotv

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Rational
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.thangoghd.cakeotv.data.model.PlayUrl
import com.thangoghd.cakeotv.service.MediaPlaybackService
import com.thangoghd.cakeotv.ui.components.QualityAdapter
import com.thangoghd.cakeotv.ui.viewmodel.PlayerViewModel
import com.thangoghd.cakeotv.utils.PlayerUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerActivity : ComponentActivity() {
    private val viewModel: PlayerViewModel by viewModels()
    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var playWhenReady = true
    private var currentMediaItem: MediaItem? = null
    private var playbackPosition = 0L
    private var qualityDialog: Dialog? = null
    private var qualityAdapter: QualityAdapter? = null
    private var isActivityResumed = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.player_view)
        setupQualityAdapter()
        
        // Get match data from intent
        intent.getStringExtra(EXTRA_MATCH_ID)?.let { matchId ->
            viewModel.loadMatch(matchId)
        }

        // Register activity lifecycle callbacks
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (activity is PlayerActivity) {
                    // Stop service and remove notification when activity is destroyed
                    stopService(Intent(activity, MediaPlaybackService::class.java))
                }
            }
        })

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            
            override fun onActivityResumed(activity: Activity) {
                if (activity == this@PlayerActivity) {
                    isActivityResumed = true
                }
            }
            
            override fun onActivityPaused(activity: Activity) {
                if (activity == this@PlayerActivity) {
                    isActivityResumed = false
                }
            }
            
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (activity == this@PlayerActivity) {
                    application.unregisterActivityLifecycleCallbacks(this)
                }
            }
        })

        // Observe UI state
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when {
                        state.isLoading -> {
                            // Show loading state if needed
                        }
                        state.error != null -> {
                            // Handle error and close activity
                            finish()
                        }
                        state.selectedUrl != null -> {
                            if (player == null) {
                                initializePlayer(state.selectedUrl)
                            } else if (player?.currentMediaItem?.localConfiguration?.uri.toString() != state.selectedUrl.url) {
                                // URL changed (quality switch), update player
                                initializePlayer(state.selectedUrl)
                            }
                            qualityAdapter?.updateData(state.availableQualities, state.selectedUrl)
                        }
                    }
                }
            }
        }
    }

    private fun setupQualityAdapter() {
        qualityAdapter = QualityAdapter(
            items = emptyList(),
            selectedUrl = null,
            onQualitySelected = { playUrl ->
                viewModel.selectQuality(playUrl)
                qualityDialog?.dismiss()
            }
        )
    }

    private fun showQualityDialog() {
        if (qualityDialog?.isShowing == true) return

        qualityDialog = Dialog(this, R.style.Theme_CakeoTV_Dialog).apply {
            setContentView(R.layout.dialog_quality_selection)
            findViewById<RecyclerView>(R.id.quality_list)?.adapter = qualityAdapter
            show()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_MENU -> {
                showQualityDialog()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializePlayer(playUrl: PlayUrl) {
        player?.release()
        
        player = PlayerUtils.createTrustAllExoPlayer(this).also { exoPlayer ->
            playerView?.player = exoPlayer
            exoPlayer.playWhenReady = playWhenReady
            
            // Restore playback position if same URL
            if (playUrl.url == viewModel.uiState.value.selectedUrl?.url) {
                exoPlayer.seekTo(viewModel.uiState.value.currentPosition)
            }
            
            val mediaItem = MediaItem.fromUri(playUrl.url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()

            // Set up player listeners
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    Log.d("MediaPlaybackPlayerActivity", "PlaybackState changed to: $playbackState")
                    if (playbackState == Player.STATE_READY) {
                        // Update ViewModel state
                        viewModel.updatePlaybackState(
                            exoPlayer.currentPosition,
                            exoPlayer.isPlaying
                        )
                        
                        // Start service và set player
                        if (viewModel.uiState.value.isBackgroundPlaybackEnabled) {
                            Log.d("MediaPlaybackPlayerActivity", "Starting service and setting player")
                            startServiceAndSetPlayer()
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    Log.d("MediaPlaybackPlayerActivity", "IsPlaying changed to: $isPlaying")
                    viewModel.updatePlaybackState(
                        exoPlayer.currentPosition,
                        isPlaying
                    )
                }
            })
        }
    }

    private fun startServiceAndSetPlayer() {
        Log.d("MediaPlaybackPlayerActivity", "Starting service and setting player")
        val serviceIntent = Intent(this, MediaPlaybackService::class.java).apply {
            putExtra("background_playback", viewModel.uiState.value.isBackgroundPlaybackEnabled)
        }
        startService(serviceIntent)
        MediaPlaybackService.player = player
        
        // Khởi tạo MediaSession sau khi service đã start
        if (viewModel.uiState.value.isBackgroundPlaybackEnabled) {
            viewModel.initializeMediaSessionWithDelay(this)
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentMediaItem = exoPlayer.currentMediaItem
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
        playerView?.player = null
    }

    override fun onStart() {
        super.onStart()
        // No need to initialize player here, it's handled in onCreate state collection
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (viewModel.uiState.value.isPictureInPictureEnabled) {
            player?.play()
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        if (isActivityResumed) {
            super.onPause()
        }
        if (!viewModel.uiState.value.isBackgroundPlaybackEnabled) {
            releasePlayer()
            viewModel.stopAndReleasePlayer()
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStop() {
        super.onStop()
        if (!viewModel.uiState.value.isBackgroundPlaybackEnabled) {
            releasePlayer()
            viewModel.stopAndReleasePlayer()
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, playerView!!).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (viewModel.uiState.value.isPictureInPictureEnabled && 
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            enterPictureInPictureMode(getPipParams())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPipParams(): PictureInPictureParams {
        return PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
            .build()
    }

    override fun onBackPressed() {
        releasePlayer()
        viewModel.stopAndReleasePlayer()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release player and cleanup
        releasePlayer()
        // Stop service if we're not in background mode
        if (!viewModel.uiState.value.isBackgroundPlaybackEnabled) {
            stopService(Intent(this, MediaPlaybackService::class.java))
        }
    }

    companion object {
        const val EXTRA_MATCH_ID = "extra_match_id"

        fun createIntent(activity: Activity, matchId: String): Intent {
            return Intent(activity, PlayerActivity::class.java).apply {
                putExtra(EXTRA_MATCH_ID, matchId)
            }
        }
    }
}
