package com.thangoghd.cakeotv.data.model

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()

    companion object {
        fun <T> loading(): Result<T> = Loading
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> error(message: String): Result<T> = Error(message)
    }
}
