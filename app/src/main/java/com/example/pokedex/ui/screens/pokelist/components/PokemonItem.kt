package com.example.pokedex.ui.screens.pokelist.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokedex.domain.model.Pokemon
import java.util.Locale

@Composable
fun PokemonItem(
    pokemon: Pokemon,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //imagen del pokemon
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                //nombre del pokemon
                Text(
                    text = "#${pokemon.id} ${pokemon.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                //tipos del pokemon
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    pokemon.types.forEach { type ->
                        PokemonTypeChip(type = type)
                    }
                }
            }
        }
    }
}

//para mostrar los tipos del pokemon con colores
@Composable
fun PokemonTypeChip(type: String) {
    //colores según el tipo
    val typeColor = when(type.lowercase()) {
        "fire" -> Color(0xFFFF7F50)
        "water" -> Color(0xFF6495ED)
        "electric" -> Color(0xFFFFD700)
        "grass" -> Color(0xFF98FB98)
        "ice" -> Color(0xFF87CEEB)
        "fighting" -> Color(0xFFCD5C5C)
        "poison" -> Color(0xFF9370DB)
        "ground" -> Color(0xFFDEB887)
        "flying" -> Color(0xFF87CEEB)
        "psychic" -> Color(0xFFFF69B4)
        "bug" -> Color(0xFF9ACD32)
        "rock" -> Color(0xFFBDB76B)
        "ghost" -> Color(0xFF8A2BE2)
        "dark" -> Color(0xFF4A4A4A)
        "dragon" -> Color(0xFF7B68EE)
        "steel" -> Color(0xFFB8B8D0)
        "fairy" -> Color(0xFFFFB6C1)
        else -> Color(0xFFAAAAAA)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = typeColor.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, typeColor)
    ) {
        Text(
            text = type.capitalize(Locale.ROOT),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = typeColor
        )
    }
}