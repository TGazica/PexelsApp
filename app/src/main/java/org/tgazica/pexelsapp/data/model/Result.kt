package org.tgazica.pexelsapp.data.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

sealed class Result<T> {
    class Loading<T>: Result<T>()
    data class Success<T>(val data: T): Result<T>()
    data class Error<T>(val error: Throwable): Result<T>()
}

fun <T> Result<T>.isLoading() = this is Result.Loading

fun <T> Result<T>.dataOrNull(): T? = (this as? Result.Success)?.data

fun <T> Result<T>.onSuccess(block: (T) -> Unit) = this.apply {
    if (this is Result.Success) {
        block(this.data)
    }
}

fun <T> Result<T>.onLoading(block: () -> Unit) = this.apply {
    if (this is Result.Loading) {
        block()
    }
}

fun <T> Result<T>.onError(block: (Throwable) -> Unit) = this.apply {
    if (this is Result.Error) {
        block(this.error)
    }
}

fun <T> Flow<Result<T>>.onSuccess(block: suspend (T) -> Unit) = onEach {
    if (it is Result.Success) {
        block(it.data)
    }
}

fun <T> Flow<Result<T>>.onLoading(block: suspend () -> Unit) = onEach {
    if (it is Result.Loading) {
        block()
    }
}

fun <T> Flow<Result<T>>.onError(block: suspend (Throwable) -> Unit) = onEach {
    if (it is Result.Error) {
        block(it.error)
    }
}
