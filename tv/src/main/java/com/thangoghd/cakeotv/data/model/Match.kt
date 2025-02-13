package com.thangoghd.cakeotv.data.model

import com.google.gson.annotations.SerializedName

data class Match(
    val id: String,
    val providers: Providers,
    val name: String,
    val slug: String,
    val timestamp: Long,
    val home: Team,
    val away: Team,
    val thumbnails: String?,
    val fansites: List<FansiteMatch>,
    val tournament: Tournament,
    val scores: Scores,
    @SerializedName("match_status")
    val matchStatus: Int,
    @SerializedName("sport_type")
    val sportType: Int,
    @SerializedName("has_lineup")
    val hasLineup: Boolean,
    @SerializedName("has_tracker")
    val hasTracker: Boolean,
    @SerializedName("live_tracker")
    val liveTracker: String,
    val priority: Int,
    @SerializedName("is_featured")
    val isFeatured: Boolean,
    val status: Int,
    @SerializedName("time_str")
    val timeStr: String,
    @SerializedName("red_cards")
    val redCards: RedCards,
//    val stats: List<Stat>,
//    val incidents: List<Incident>
)

data class Providers(
    val thesports: Provider
)

data class Provider(
    @SerializedName("provider_id")
    val providerId: String,
    @SerializedName("provider_name")
    val providerName: String,
    @SerializedName("model_id")
    val modelId: String,
    @SerializedName("other_id")
    val otherId: String?
)


data class Scores(
    val home: Int,
    val away: Int,
    @SerializedName("sport_type")
    val sportType: Int,
    val detail: Any?
)

data class RedCards(
    val home: Int,
    val away: Int
)

data class Stat(
    val type: Int,
    val home: Int,
    val away: Int
)

data class Incident(
    val type: Int,
    val position: Int,
    val time: Int,
    @SerializedName("player_id")
    val playerId: String,
    @SerializedName("player_name")
    val playerName: String,
    @SerializedName("reason_type")
    val reasonType: Int
)
