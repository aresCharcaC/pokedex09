package com.example.pokedex.ui.screens.pokelist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//el estado q vamos a mostrar en la UI
data class PokemonListState(
    val pokemons: List<Pokemon> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    //pa mantener el estado de la pantalla
    private val _state = mutableStateOf(PokemonListState())
    val state: State<PokemonListState> = _state

    //cuando se crea el viewmodel cargamos la primera página
    init {
        loadPokemonPaginated()
    }

    //esto carga más pokemon cuando llegas al final de la lista
    fun loadPokemonPaginated() {
        viewModelScope.launch {
            //pa q no cargue más si ya llegamos al final o está cargando
            if(_state.value.endReached || _state.value.isLoading) {
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true)

            //cada página trae 20 pokemon
            val page = _state.value.page
            val result = repository.getPokemonList(20, page * 20)

            when(result) {
                is Resource.Success -> {
                    //agregamos los nuevos pokemon a la lista q ya teníamos
                    val pokemons = _state.value.pokemons + result.data
                    //si trajo menos de 20 es q ya no hay más
                    val endReached = result.data.size < 20
                    _state.value = _state.value.copy(
                        pokemons = pokemons,
                        page = page + 1,
                        endReached = endReached,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
}