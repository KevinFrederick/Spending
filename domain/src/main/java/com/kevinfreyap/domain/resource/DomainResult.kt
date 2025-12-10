package com.kevinfreyap.domain.resource

import com.kevinfreyap.domain.error.ValidationError

sealed class DomainResult<out T> {
    data class Success<T>(val data: T): DomainResult<T>()
    data class ValidationFailed(val errors: List<ValidationError>): DomainResult<Nothing>()
    data class Failure(val throwable: Throwable): DomainResult<Nothing>()
}
