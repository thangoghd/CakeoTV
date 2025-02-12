package com.thangoghd.cakeotv.data.repository

import com.thangoghd.cakeotv.data.model.Match
import com.thangoghd.cakeotv.data.model.Result
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    suspend fun getLiveMatches(): Flow<Result<List<Match>>>
}
