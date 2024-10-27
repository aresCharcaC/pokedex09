package com.example.pokedex.data.remote.models

//la respuesta de la api cuando pides un pokemon específico
data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeResponse>,
    val stats: List<StatResponse>,
    val sprites: Sprites
)

//las responses secundarias q vienen dentro de la principal
data class TypeResponse(
    val slot: Int,
    val type: Type
)

data class Type(
    val name: String,
    val url: String
)

data class StatResponse(
    val base_stat: Int,
    val stat: StatInfo
)

data class StatInfo(
    val name: String
)

data class Sprites(
    val front_default: String
)