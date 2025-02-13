package com.thangoghd.cakeotv.data.model

import com.google.gson.annotations.SerializedName

data class MetaResponse(
    val status: Int,
    val data: MetaData
)

data class MetaData(
    val scores: Scores,
    @SerializedName("has_lineup")
    val hasLineup: Boolean,
    @SerializedName("has_tracker")
    val hasTracker: Boolean,
    val fansites: List<Fansite>
)

data class Fansite(
    @SerializedName("model_id")
    val modelId: String,
    val name: String,
    val blv: List<Commentator>,
    @SerializedName("play_urls")
    val playUrls: List<PlayUrl>
)

data class Commentator(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val img: String
)

data class PlayUrl(
    val id: Int,
    val name: String,
    val url: String,
    val idz: Int
)
