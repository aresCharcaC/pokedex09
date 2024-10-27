package com.example.pokedex.ui.screens.pokedetail

import com.example.pokedex.domain.model.PokemonDetail
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//estado pa la pantalla de detalle
data class PokemonDetailState(
    val pokemon: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(PokemonDetailState())
    val state: State<PokemonDetailState> = _state

    init {
        //agarramos el id q viene de la navegación
        savedStateHandle.get<Int>("pokemonId")?.let { pokemonId ->
            loadPokemonDetail(pokemonId)
        }
    }

    private fun loadPokemonDetail(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            when(val result = repository.getPokemonDetail(id)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        pokemon = result.data,
                        isLoading = false,
                        error = null
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