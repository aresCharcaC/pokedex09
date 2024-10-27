package com.example.pokedex.domain.model

//este es el modelo con todos los datos del pokemon
data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<String>,
    val stats: List<Stat>,
    val imageUrl: String
)

//para guardar las stats del pokemon (vida ataque y otros)
data class Stat(
    val name: String,
    val value: Int
)