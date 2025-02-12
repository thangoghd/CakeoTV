package com.thangoghd.cakeotv.data.repository

import com.thangoghd.cakeotv.data.model.Match
import com.thangoghd.cakeotv.data.model.Result
import com.thangoghd.cakeotv.data.remote.CakeoApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val api: CakeoApi
) : MatchRepository {

    override suspend fun getLiveMatches(): Flow<Result<List<Match>>> = flow {
        try {
            emit(Result.loading<List<Match>>())
            val response = api.getLiveMatches()
            if (response.success) {
                emit(Result.success(response.data))
            } else {
                emit(Result.error(response.message))
            }
        } catch (e: Exception) {
            emit(Result.error(e.message ?: "An unexpected error occurred"))
        }
    }
}
