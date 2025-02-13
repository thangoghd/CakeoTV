package com.thangoghd.cakeotv.data.remote

import com.thangoghd.cakeotv.data.model.ApiResponse
import com.thangoghd.cakeotv.data.model.Match
import com.thangoghd.cakeotv.data.model.MetaResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CakeoApi {
    companion object {
        const val BASE_URL = "https://api.cakeo.xyz/"
    }

    @GET("match/live")
    suspend fun getLiveMatches(): ApiResponse<List<Match>>

    @GET("match/meta-v2/{matchId}")
    suspend fun getMatchMeta(@Path("matchId") matchId: String): MetaResponse
}
