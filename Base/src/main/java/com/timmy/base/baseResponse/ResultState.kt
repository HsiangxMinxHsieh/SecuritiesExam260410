package com.timmy.base.baseResponse

sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
//    data object NotModify : ResultState<Nothing>() // 沒有304的情況
    data class Error(val message: String) : ResultState<Nothing>()
}