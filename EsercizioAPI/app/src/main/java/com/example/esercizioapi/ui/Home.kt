package com.example.esercizioapi.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.example.esercizioapi.PokeViewModel
import com.example.esercizioapi.R
import com.example.esercizioapi.network.Pokemon
import kotlinx.coroutines.flow.Flow

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Home(modifier: Modifier = Modifier, viewModel: PokeViewModel,
         onNavigation : (String) -> Unit
         ) {
    val field = viewModel.field

    val list by viewModel.stateflow.collectAsState()
    val paging = viewModel.userPagingFlow.collectAsLazyPagingItems()

    viewModel.checkAlert()

    Column(
        modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ){
        Barra(
            field,
            viewModel.searchName(field.text.toString().lowercase()),
            {
                viewModel.getInfoPokemons(field.text.toString().lowercase())
            },
            {
            }
        )
        if(list != null && field.text != ""){
            Cards(list!!, onNavigation = onNavigation)
        }else{
            Paging(viewModel.userPagingFlow, onNavigation, {
                paging.refresh()
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Paging(flow :Flow<PagingData<Pokemon>>, onNavigation : (String) -> Unit, onRefresh : () -> Unit){
    val userFlow = flow
    val lazyPagingItems = userFlow.collectAsLazyPagingItems()

    var isRefreshing by remember { mutableStateOf(false) }

    PullToRefreshBox(
        isRefreshing =  isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh()
            isRefreshing = false
        },
    ) {
        LazyColumn {
            items(
                lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id }
            ) {
                    index ->
                val poke = lazyPagingItems[index]
                if(poke != null)
                    Card(poke, onNavigation = onNavigation)
            }
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
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = result,
                itemContent = {result->
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
            )
        }
    }
}

@Composable
fun Cards(pokemons : List<Pokemon>, onNavigation: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(
            items = pokemons,
            itemContent = {
                    poke->
                Card(poke, onNavigation = onNavigation)
            }
        )
    }
}

@Composable
fun Card(poke : Pokemon, modifier: Modifier = Modifier, onNavigation: (String) -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 15.dp)
            .clickable(
                enabled = true,
                onClick = {
                    onNavigation(poke.name)
                },
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (poke.sprites.front_default != null && poke.sprites.front_default != "") {
                AsyncImage(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 15.dp, top = 20.dp, bottom = 20.dp)
                        .height(250.dp)
                        .width(250.dp),
                    contentScale = ContentScale.Crop,
                    model = poke.sprites.front_default,
                    contentDescription = null,
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
                style = MaterialTheme.typography.titleLarge,
                text = poke.name.uppercase()
            )
        }
    }
}
