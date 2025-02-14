package com.thangoghd.cakeotv

import android.app.Activity
import android.app.Dialog
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.thangoghd.cakeotv.data.model.PlayUrl
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
    private var currentItem = 0
    private var playbackPosition = 0L
    private var qualityDialog: Dialog? = null
    private var qualityAdapter: QualityAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.player_view)
        setupQualityAdapter()
        
        // Get match data from intent
        intent.getStringExtra(EXTRA_MATCH_ID)?.let { matchId ->
            viewModel.loadMatch(matchId)
        }

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

    private fun initializePlayer(playUrl: PlayUrl) {
        player?.release()
        
        player = PlayerUtils.createTrustAllExoPlayer(this).also { exoPlayer ->
            playerView?.player = exoPlayer
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.seekTo(currentItem, playbackPosition)
            
            val mediaItem = MediaItem.fromUri(playUrl.url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
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
        super.onPause()
        if (!viewModel.uiState.value.isBackgroundPlaybackEnabled) {
            releasePlayer()
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStop() {
        super.onStop()
        if (!viewModel.uiState.value.isBackgroundPlaybackEnabled) {
            releasePlayer()
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

    companion object {
        const val EXTRA_MATCH_ID = "extra_match_id"

        fun createIntent(activity: Activity, matchId: String): Intent {
            return Intent(activity, PlayerActivity::class.java).apply {
                putExtra(EXTRA_MATCH_ID, matchId)
            }
        }
    }
}
