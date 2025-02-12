package com.thangoghd.cakeotv.data.model

import com.google.gson.annotations.SerializedName

data class Team(
    @SerializedName("model_id")
    val modelId: String,
    val name: String,
    val slug: String,
    val logo: String
)
