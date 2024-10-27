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

    //estado pa la búsqueda
    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    //para no hacer muchas llamadas seguidas
    private var searchJob: Job? = null

    //cuando se crea el viewmodel cargamos la primera página
    init {
        loadPokemonPaginated()
    }

    //función pa buscar
    fun onSearchTextChange(text: String) {
        _searchText.value = text
        //cancelamos búsqueda anterior si existe
        searchJob?.cancel()
        //si está vacío mostramos la lista normal
        if(text.isEmpty()) {
            loadPokemonPaginated()
            return
        }
        //iniciamos nueva búsqueda con delay
        searchJob = viewModelScope.launch {
            delay(500L) //esperamos q termine de escribir
            searchPokemon(text)
        }
    }

    private suspend fun searchPokemon(query: String) {
        _state.value = _state.value.copy(isLoading = true)
        when(val result = repository.searchPokemon(query.lowercase())) {
            is Resource.Success -> {
                _state.value = _state.value.copy(
                    pokemons = result.data,
                    isLoading = false,
                    error = null
                )
            }
            is Resource.Error -> {
                _state.value = _state.value.copy(
                    pokemons = emptyList(),
                    isLoading = false,
                    error = result.message
                )
            }
            is Resource.Loading -> {
                _state.value = _state.value.copy(isLoading = true)
            }
        }
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