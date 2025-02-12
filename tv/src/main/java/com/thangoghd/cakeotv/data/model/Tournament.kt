package com.thangoghd.cakeotv.data.model

import com.google.gson.annotations.SerializedName

data class Tournament(
    @SerializedName("model_id")
    val modelId: String,
    val name: String,
    val slug: String,
    val logo: String,
    @SerializedName("external_logo")
    val externalLogo: String,
    @SerializedName("is_featured")
    val isFeatured: Boolean,
    val priority: Int
)
