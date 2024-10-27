package com.example.pokedex.util

//esto es pa manejar los estados de la data (loading, error, success)
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}
