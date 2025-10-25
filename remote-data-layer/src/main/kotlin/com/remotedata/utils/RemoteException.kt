package com.remotedata.utils

sealed class RemoteException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    data class NetworkError(
        override val message: String = "Network error occurred",
        override val cause: Throwable? = null
    ) : RemoteException(message, cause)
    
    data class HttpError(
        val code: Int,
        override val message: String = "HTTP error: $code",
        override val cause: Throwable? = null
    ) : RemoteException(message, cause)
    
    data class ParseError(
        override val message: String = "Failed to parse response",
        override val cause: Throwable? = null
    ) : RemoteException(message, cause)
    
    data class RateLimitError(
        val retryAfter: Long? = null,
        override val message: String = "Rate limit exceeded",
        override val cause: Throwable? = null
    ) : RemoteException(message, cause)
    
    data class TimeoutError(
        override val message: String = "Request timed out",
        override val cause: Throwable? = null
    ) : RemoteException(message, cause)
    
    data class UnknownError(
        override val message: String = "Unknown error occurred",
        override val cause: Throwable? = null
    ) : RemoteException(message, cause)
}
