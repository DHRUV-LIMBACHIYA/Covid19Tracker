package com.example.covid19tracker.Utils

/**
 * Created by Dhruv Limbachiya on 18-09-2021.
 */

sealed class Resource<T>(val data: T? = null,val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String,data: T? = null) : Resource<T>(message = message,data = data)
    class Loading<T>() : Resource<T>()
}