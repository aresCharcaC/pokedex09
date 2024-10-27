package com.example.pokedex.data.repository

import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.model.PokemonDetail
import com.example.pokedex.domain.model.Stat
import com.example.pokedex.util.Resource
import java.util.Locale
import javax.inject.Inject

//aki va toda la logica de datos
class PokemonRepository @Inject constructor(
    private val api: PokeApiService
) {
    //trae la lista de pokemon con paginación
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<List<Pokemon>> {
        return try {
            val response = api.getPokemonList(limit, offset)
            val pokemons = response.results.mapIndexed { index, result ->
                //sacamos el id de la url xq la api no lo manda directo
                val id = index + offset + 1
                Pokemon(
                    id = id,
                    name = result.name.capitalize(),
                    //armamos la url de la imagen con el id
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
                )
            }
            Resource.Success(pokemons)
        } catch (e: Exception) {
            Resource.Error("Ups! Algo salió mal: ${e.message}")
        }
    }

    //trae el detalle de un pokemon x su id
    suspend fun getPokemonDetail(id: Int): Resource<PokemonDetail> {
        return try {
            val response = api.getPokemonDetail(id)
            Resource.Success(
                PokemonDetail(
                    id = response.id,
                    name = response.name.capitalize(),
                    height = response.height,
                    weight = response.weight,
                    types = response.types.map { it.type.name },
                    stats = response.stats.map {
                        Stat(
                            name = it.stat.name,
                            value = it.base_stat
                        )
                    },
                    imageUrl = response.sprites.front_default
                )
            )
        } catch (e: Exception) {
            Resource.Error("No pudimos cargar el pokemon: ${e.message}")
        }
    }

    suspend fun searchPokemon(query: String): Resource<List<Pokemon>> {
        return try {
            //traemos todos (la lista básica es liviana)
            val response = api.getPokemonList(limit = 1500, offset = 0)
            //filtramos los q coinciden
            val filtered = response.results
                .filter { it.name.contains(query) }
                .mapIndexed { _, result ->
                    //sacamos el id de la url
                    val id = result.url.split("/")
                        .dropLast(1)
                        .last()
                        .toInt()
                    Pokemon(
                        id = id,
                        name = result.name.capitalize(Locale.ROOT),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
                    )
                }
            Resource.Success(filtered)
        } catch(e: Exception) {
            Resource.Error("No pudimos buscar: ${e.message}")
        }
    }
}

data class PokemonListState(
    val pokemons: List<Pokemon> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)