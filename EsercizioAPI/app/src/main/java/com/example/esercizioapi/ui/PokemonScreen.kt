package com.example.esercizioapi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.esercizioapi.R
import com.example.esercizioapi.network.Pokemon
import com.example.esercizioapi.network.UiState

@Composable
fun PokemonScreen(
    name: String,
    modifier: Modifier = Modifier,
    viewModel : HomeViewModel = hiltViewModel(),
    nav : NavHostController
    ) {



    val pokemon by viewModel.retriveInfoPokemon.collectAsStateWithLifecycle(initialValue = UiState.Loading)

    LaunchedEffect(true) {
        viewModel.setPokemonName(name)
    }

    val poke = when(pokemon){
            is UiState.Loading -> Pokemon(name = "Loading", id = -1)
            is UiState.Error -> Pokemon(name = "Error", id = -2)
            is UiState.Success -> (pokemon as UiState.Success<*>).json as Pokemon
        }
    //}

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
                        onClick = {
                            nav.popBackStack()
                        }
                    ),
                painter = painterResource(R.drawable.freccia_indietro),
                contentDescription = "",
            )

            Immagine(poke)

            Column (modifier = modifier.fillMaxWidth()) {

                Carta(
                    title = "Info",
                    text = "Peso: ${poke.weight}" +
                        "\nSpecie: ${poke.species.name}" +
                        "\nID: ${poke.id}" +
                        "\nEsperienza iniziale: ${poke.base_experience}",
                    modifier = Modifier.weight(1f))

                var str = ""

                poke.abilities.forEach { ability ->
                    str += "->  Nome abilità : ${ability.ability.name}\n" +
                            "     Slot: ${ability.slot}\n"
                }

                Carta(
                    modifier = Modifier.weight(1f),
                    title = "Abilita",
                    text = str
                )
            }
        }
    }
}

@Composable
fun Immagine(poke : Pokemon) {
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

@Composable
fun Carta(title: String, text: String, modifier: Modifier = Modifier){
    Card(
        modifier
            .padding(bottom = 5.dp, start = 10.dp, end = 10.dp)
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                style = MaterialTheme.typography.headlineLarge,
                text = title
            )
        }

        Text(
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp),
            fontSize = 20.sp,
            lineHeight = 30.sp,
            text = text
        )
    }
}