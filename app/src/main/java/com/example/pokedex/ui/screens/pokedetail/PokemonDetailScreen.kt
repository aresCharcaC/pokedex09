package com.example.pokedex.ui.screens.pokedetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.domain.model.PokemonDetail
import com.example.pokedex.ui.screens.pokelist.components.PokemonTypeChip


@Composable
fun PokemonDetailScreen(
    navController: NavController,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Box(modifier = Modifier.fillMaxSize()) {
        state.pokemon?.let { pokemon ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                //cabecera con imagen y nombre
                PokemonHeader(pokemon)

                Spacer(modifier = Modifier.height(16.dp))

                //info básica (peso, altura)
                PokemonBasicInfo(pokemon)

                Spacer(modifier = Modifier.height(16.dp))

                //tipos del pokemon
                PokemonTypes(pokemon)

                Spacer(modifier = Modifier.height(16.dp))

                //stats con barras de progreso
                PokemonStats(pokemon)
            }
        }

        //loading mientras carga
        if(state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        //error si algo sale mal
        if(state.error != null) {
            Text(
                text = state.error,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

// Componentes pa la pantalla de detalle
@Composable
private fun PokemonHeader(pokemon: PokemonDetail) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //imagen grande del pokemon
        AsyncImage(
            model = pokemon.imageUrl,
            contentDescription = pokemon.name,
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp)
        )

        //nombre y número
        Text(
            text = "#${pokemon.id} ${pokemon.name}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PokemonBasicInfo(pokemon: PokemonDetail) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //altura
        InfoItem(
            title = "Height",
            value = "${pokemon.height / 10.0}m"
        )
        //peso
        InfoItem(
            title = "Weight",
            value = "${pokemon.weight / 10.0}kg"
        )
    }
}

@Composable
private fun InfoItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PokemonTypes(pokemon: PokemonDetail) {
    Column {
        Text(
            text = "Types",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pokemon.types.forEach { type ->
                PokemonTypeChip(type = type)
            }
        }
    }
}

@Composable
private fun PokemonStats(pokemon: PokemonDetail) {
    Column {
        Text(
            text = "Base Stats",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        //barra pa cada stat
        pokemon.stats.forEach { stat ->
            StatBar(
                statName = stat.name,
                statValue = stat.value
            )
        }
    }
}

@Composable
private fun StatBar(statName: String, statValue: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = statName.capitalize(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = statValue.toString(),
                fontWeight = FontWeight.Bold
            )
        }
        //la barra de progreso
        LinearProgressIndicator(
            progress = statValue / 255f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                statValue < 50 -> Color.Red
                statValue < 100 -> Color.Yellow
                else -> Color.Green
            }
        )
    }
}