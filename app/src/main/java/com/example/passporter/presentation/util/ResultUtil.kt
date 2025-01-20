package com.example.passporter.presentation.util

sealed class ResultUtil<out T> {
    data class Success<T>(val data: T) : ResultUtil<T>()
    data class Error(val exception: Throwable) : ResultUtil<Nothing>()
}