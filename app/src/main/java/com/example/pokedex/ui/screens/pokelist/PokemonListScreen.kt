package com.example.pokedex.ui.screens.pokelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pokedex.ui.screens.pokelist.components.PokemonItem

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val searchText = viewModel.searchText.value

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            searchText = searchText,
            onSearchChange = viewModel::onSearchTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.pokemons) { pokemon ->
                    PokemonItem(
                        pokemon = pokemon,
                        onItemClick = {
                            navController.navigate("pokemon_detail/${pokemon.id}")
                        }
                    )

                    //solo cargamos más si no estamos buscando
                    if(pokemon == state.pokemons.last() && !state.endReached && searchText.isEmpty()) {
                        viewModel.loadPokemonPaginated()
                    }
                }

                //item pa mostrar loading al final mientras carga más
                if(state.isLoading && searchText.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            //loading principal solo pa búsquedas
            if(state.isLoading && searchText.isNotEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if(state.error != null) {
                Text(
                    text = state.error,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if(!state.isLoading && state.pokemons.isEmpty() && searchText.isNotEmpty()) {
                Text(
                    text = "No encontramos ningún Pokémon :(",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

//componente pa la barra de búsqueda
@Composable
fun SearchBar(
    searchText: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchChange,
        modifier = modifier,
        placeholder = { Text("Buscar Pokémon...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if(searchText.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpiar"
                    )
                }
            }
        },
        maxLines = 1,
        singleLine = true
    )
}