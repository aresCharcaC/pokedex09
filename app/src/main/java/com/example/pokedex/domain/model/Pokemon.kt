package com.example.pokedex.domain.model

//aki va el modelo principal pa la lista
data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    //la url de la imagen la sacamos del sprite frontal
    val types: List<String> = emptyList()
)