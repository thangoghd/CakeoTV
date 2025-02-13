package com.thangoghd.cakeotv.ui.model

enum class UIMode {
    TV, MOBILE
}

data class UIState(
    val isFirstLaunch: Boolean = true,
    val uiMode: UIMode = UIMode.TV,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBackgroundPlaybackEnabled: Boolean = true,
    val isPictureInPictureEnabled: Boolean = true
)
