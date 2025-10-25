package com.app.persistence.domain.model

sealed class DataError : Exception() {
    data class NetworkError(override val message: String, val cause: Throwable? = null) : DataError()
    data class ParseError(override val message: String, val cause: Throwable? = null) : DataError()
    data class NotFoundError(override val message: String) : DataError()
    data class AuthenticationError(override val message: String) : DataError()
    data class RateLimitError(override val message: String, val retryAfter: Long? = null) : DataError()
    data class ValidationError(override val message: String) : DataError()
    data class CacheError(override val message: String, val cause: Throwable? = null) : DataError()
    data class UnknownError(override val message: String, val cause: Throwable? = null) : DataError()
}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: DataError) : Result<Nothing>()
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw error
    }
    
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (DataError) -> Unit): Result<T> {
        if (this is Error) action(error)
        return this
    }
}
