package com.thangoghd.cakeotv.data.model

import com.google.gson.annotations.SerializedName

data class FansiteMatch(
    @SerializedName("model_id")
    val modelId: String,
    val name: String,
    val blv: List<BLV>,
)

data class BLV(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val img: String
)
