package com.thangoghd.cakeotv.data.remote

import com.thangoghd.cakeotv.data.model.ApiResponse
import com.thangoghd.cakeotv.data.model.Match
import retrofit2.http.GET

interface CakeoApi {
    companion object {
        const val BASE_URL = "https://api.cakeo.xyz/"
    }

    @GET("match/live")
    suspend fun getLiveMatches(): ApiResponse<List<Match>>
}
