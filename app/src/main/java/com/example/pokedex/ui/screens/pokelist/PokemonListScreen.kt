package com.example.pokedex.ui.screens.pokelist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pokedex.ui.screens.pokelist.components.PokemonItem

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    //observamos el estado
    val state = viewModel.state.value

    Box(modifier = Modifier.fillMaxSize()) {
        //la lista de pokemon
        LazyColumn(
            contentPadding = PaddingValues(16.dp)
        ) {
            items(state.pokemons) { pokemon ->
                PokemonItem(
                    pokemon = pokemon,
                    onItemClick = {
                        //navegamos al detalle
                        navController.navigate("pokemon_detail/${pokemon.id}")
                    }
                )
                //cuando llegamos cerca del final cargamos más
                if(pokemon == state.pokemons.last() && !state.endReached) {
                    viewModel.loadPokemonPaginated()
                }
            }
        }

        //si está cargando mostramos un loading
        if(state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        //si hay error lo mostramos
        if(state.error != null) {
            Text(
                text = state.error,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

