package com.example.pokedex.data.remote.models

//estos son los modelos q vienen directo de la API
data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val url: String
)
