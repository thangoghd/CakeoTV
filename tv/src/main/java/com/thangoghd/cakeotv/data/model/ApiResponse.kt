package com.thangoghd.cakeotv.data.model

data class ApiResponse<T>(
    val success: Boolean,
    val status: Int,
    val data: T,
    val message: String
)
