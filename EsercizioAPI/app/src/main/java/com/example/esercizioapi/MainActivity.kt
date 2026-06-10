package com.example.esercizioapi

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.esercizioapi.network.Pokemon
import com.example.esercizioapi.ui.theme.EsercizioAPITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EsercizioAPITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val viewModel : PokeViewModel = viewModel(factory = PokeViewModel.Factory)
    var field by remember { mutableStateOf(TextFieldState()) }

    val list by viewModel.stateflow.collectAsState()

    Column(
        modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ){
        Barra(
            field,
            viewModel.searchListName(field.text.toString().lowercase()),
            {
                viewModel.getInfoPokemons(field.text.toString().lowercase())
            },
            {
            }
        )
        if(list != null){
            Card(list!!)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Barra(field : TextFieldState,
          result : List<String>,
          onSearched : () -> Unit,
          onModifier: () -> Unit
          ) {
    var expanded by remember { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = field.text.toString(),
                onQueryChange = {str ->
                    onModifier()
                    field.edit { replace(0,length, str) }
                },
                onSearch = {
                    onSearched()
                    //field.edit { replace(0,length, "") }
                    expanded = false
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = {Text("Search")}
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())){
            result.forEach {
                    result->
                ListItem(
                    headlineContent = {Text(result)},
                    modifier = Modifier
                        .clickable {
                            field.edit { replace(0, length, result) }
                            expanded = false
                        }
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun Card( pokemons : List<Pokemon>, modifier: Modifier = Modifier) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(
            items = pokemons,
            itemContent = {
                poke->
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 15.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if(poke.sprites.front_default != null){
                            AsyncImage(
                                modifier = Modifier
                                    .padding(start = 20.dp)
                                    .height(250.dp)
                                    .width(250.dp),
                                model = poke.sprites.front_default,
                                contentDescription = null,
                            )
                        }

                        Text(
                            text = poke.name
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EsercizioAPITheme {
        Greeting()
    }
}