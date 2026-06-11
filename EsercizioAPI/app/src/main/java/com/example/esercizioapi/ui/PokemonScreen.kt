package com.example.esercizioapi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.esercizioapi.PokeViewModel
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.esercizioapi.R
import com.example.esercizioapi.network.Pokemon
import okhttp3.internal.wait

@Composable
fun PokemonScreen(
    name: String?,
    modifier: Modifier = Modifier,
    viewModel: PokeViewModel,
    onNavigation : () -> Unit
    ) {
    when(name){
        null -> Pokemon(name = "Error", id = -1)
        else -> viewModel.getInfoPokemons(name)
    }

    val list by viewModel.stateflow.collectAsState()
    val poke =
        if(list == null)
            Pokemon("Error", id = -1)
        else
            list!!.first()

    Card(
        modifier = modifier
            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 15.dp)
            .fillMaxSize()
    ) {
        Column(
        ) {
            Icon(
                modifier = Modifier
                    .height(70.dp)
                    .width(70.dp)
                    .padding(top = 20.dp, start = 15.dp)
                    .clickable(
                        onClick = onNavigation
                    ),
                painter = painterResource(R.drawable.freccia_indietro),
                contentDescription = "",
            )

            Column() {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if (poke.sprites.front_default != null && poke.sprites.front_default != "") {
                        var clicked by remember {mutableStateOf(false)}

                        AsyncImage(
                            modifier = Modifier.clickable(
                                onClick = {
                                    clicked = !clicked
                                }
                            )
                                .padding(start = 20.dp, end = 15.dp, top = 20.dp, bottom = 20.dp)
                                .height(250.dp)
                                .width(250.dp),
                            contentScale = ContentScale.Crop,
                            model =
                                if(clicked)
                                    poke.sprites.front_shiny
                                else
                                    poke.sprites.front_default
                            ,
                            contentDescription = null
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .width(250.dp)
                                .height(250.dp)
                                .padding(start = 20.dp, end = 15.dp, top = 20.dp, bottom = 20.dp),
                            painter = painterResource(R.drawable.missingno),
                            contentDescription = ""
                        )
                    }

                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = poke.name.uppercase(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Text(
                fontSize = 20.sp,
                lineHeight = 30.sp,
                text = "Peso: ${poke.weight}" +
                        "\nSpecie: ${poke.species.name}" +
                        "\nID: ${poke.id}" +
                        "\nEsperienza iniziale: ${poke.base_experience}" +
                        "\nAbilità :"
            )
            poke.abilities.forEach { ability ->
                Text(
                    lineHeight = 30.sp,
                    fontSize = 20.sp,
                    text = "->  Nome abilità : ${ability.ability.name}\n" +
                            "     Slot: ${ability.slot}"
                )
            }
        }
    }
}