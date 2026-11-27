package com.kevinfreyap.domain.error

interface ErrorReporter {
    fun log(message: String)
    fun recordException(throwable: Throwable)
    fun setCustomKey(key: String, value: String)
    fun setUserId(userId: String)
}