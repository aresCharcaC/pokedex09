package com.example.pokedex.ui.screens.pokelist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _state = mutableStateOf(PokemonListState())
    val state: State<PokemonListState> = _state

    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    private var searchJob: Job? = null

    init {
        loadPokemonPaginated()
    }

    //actualizamos la función de búsqueda
    fun onSearchTextChange(text: String) {
        _searchText.value = text
        searchJob?.cancel()

        if(text.isEmpty()) {
            //si limpiamos la búsqueda, reseteamos y volvemos a la lista paginada
            _state.value = _state.value.copy(
                pokemons = emptyList(),
                page = 0,
                endReached = false
            )
            loadPokemonPaginated()
            return
        }

        searchJob = viewModelScope.launch {
            delay(500L)
            searchPokemon(text)
        }
    }

    //la paginación normal
    fun loadPokemonPaginated() {
        //solo paginamos si no estamos buscando
        if(_searchText.value.isNotEmpty()) return

        viewModelScope.launch {
            if(_state.value.endReached || _state.value.isLoading) {
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true)

            val page = _state.value.page
            when(val result = repository.getPokemonList(20, page * 20)) {
                is Resource.Success -> {
                    //agregamos los nuevos pokemon a la lista existente
                    val pokemons = _state.value.pokemons + result.data
                    _state.value = _state.value.copy(
                        pokemons = pokemons,
                        page = page + 1,
                        endReached = result.data.isEmpty(),
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

    private suspend fun searchPokemon(query: String) {
        _state.value = _state.value.copy(isLoading = true)
        when(val result = repository.searchPokemon(query.lowercase())) {
            is Resource.Success -> {
                _state.value = _state.value.copy(
                    pokemons = result.data,
                    isLoading = false,
                    error = null,
                    //pa la búsqueda no necesitamos paginación
                    endReached = true
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