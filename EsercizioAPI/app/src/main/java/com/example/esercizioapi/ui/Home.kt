package com.example.esercizioapi.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.example.esercizioapi.PokemonRoute
import com.example.esercizioapi.ProfileInfo
import com.example.esercizioapi.R
import com.example.esercizioapi.network.UiPokemon
import com.example.esercizioapi.network.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.Flow

private val TAG = "UI"

@SuppressLint("StateFlowValueCalledInComposition", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Home(modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel(),
         nav : NavHostController
         ) {
    val field = viewModel.field

    val list by viewModel.stateflow.collectAsState()
    val paging = viewModel.userPagingFlow.collectAsLazyPagingItems()

    viewModel.checkAlert()

    val flowlistaFavourite = viewModel.retriveFavouritePokemon
    val StatelistaFavourite by viewModel.retriveFavouritePokemon.collectAsStateWithLifecycle(initialValue = UiState.Loading)

    val listaFavourite = when(StatelistaFavourite) {
        is UiState.Success -> (StatelistaFavourite as UiState.Success<List<UiPokemon>>).json
        is UiState.Loading -> listOf<UiPokemon>()
        is UiState.Error -> listOf<UiPokemon>()
    }

    Content(
        modifier = modifier,
        field = field,
        searchName ={str -> viewModel.searchName(str)},
        searchInfoPokemons ={str -> viewModel.searchInfoPokemons(str)},
        nav = nav,
        list = list,
        paging = paging,
        userPagingFlow = viewModel.userPagingFlow,
        listaFavourite = flowlistaFavourite,
        onFavourite ={str -> viewModel.removeFavourite(str)},
        onNonFavourite = {poke -> viewModel.insertFavourite(poke)},
        initRicerca = {viewModel.initRicerca()},
        setPokemonName = {str -> viewModel.setPokemonName(str)},
        Favourite = listaFavourite
    )

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Content(
    modifier: Modifier = Modifier,
    field : TextFieldState,
    searchName : (String) -> List<String>,
    searchInfoPokemons: (String) -> Unit,
    nav : NavHostController,
    list : List<UiPokemon>?,
    paging : LazyPagingItems<UiPokemon>,
    userPagingFlow : Flow<PagingData<UiPokemon>>,
    listaFavourite : Flow<UiState<List<UiPokemon>>>,
    onFavourite: (UiPokemon) -> Unit,
    onNonFavourite: (UiPokemon) -> Unit,
    Favourite : List<UiPokemon>,
    setPokemonName : (String) -> Unit,
    initRicerca: () -> Unit
){
    var isFavourite by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Barra(
                field,
                searchName(field.text.toString().lowercase()),
                {
                    searchInfoPokemons(field.text.toString().lowercase())
                },
                {
                },
                initRicerca = initRicerca,
                modifier = Modifier,
                nav
            )
        },
        bottomBar = {
            BottomBar(
                onClickHome = {
                    isFavourite = false
                },
                onClickFavourite = {
                    isFavourite = true
                }
            )
        }
    ) {
        Column(
            modifier
                .fillMaxSize()
                .semantics { isTraversalGroup = true }
        ){
            if(!isFavourite) {
                if(list != null && field.text != ""){
                    Cards(list, onNavigation = { str ->
                        nav.navigate(PokemonRoute(str))
                    },
                        onFavourite ={poke -> onFavourite(poke)},
                        isFavourite = Favourite,
                        )
                }else{
                    Paging(userPagingFlow, onNavigation = { str ->
                        nav.navigate(PokemonRoute(str))
                    }, {
                        paging.refresh()
                    },
                        onFavourite = { poke -> onFavourite(poke) },
                        onNonFavourite = { poke -> onNonFavourite(poke) },
                        isFavourite = Favourite,
                        setPokemonName = setPokemonName)
                }
            }else{
                val listaF by listaFavourite.collectAsState(UiState.Loading)

                var list : List<UiPokemon> = listOf()

                when(listaF) {
                    is UiState.Success -> list = (listaF as UiState.Success<*>).json as List<UiPokemon>
                    is UiState.Error -> {}
                    is UiState.Loading -> {}
                }
                Cards(list,
                    onNavigation = {str ->
                    nav.navigate(PokemonRoute(str))
                    },
                    onFavourite = {poke -> onFavourite(poke)},
                    isFavourite = Favourite
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Paging(flow :Flow<PagingData<UiPokemon>>,
           onNavigation : (String) -> Unit,
           onRefresh : () -> Unit,
           onFavourite: (UiPokemon) -> Unit,
           onNonFavourite: (UiPokemon) -> Unit,
           isFavourite : List<UiPokemon>,
           setPokemonName : (String) -> Unit
           ){
    val userFlow = flow
    val lazyPagingItems = userFlow.collectAsLazyPagingItems()

    PullToRefreshBox(
        isRefreshing = !lazyPagingItems.loadState.isIdle,
        onRefresh = {
            onRefresh()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(
                lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id }
            ) {
                    index ->
                val poke = lazyPagingItems[index]
                if(poke != null){
                    Card(poke,
                        onNavigation = onNavigation,
                        onFavourite = { poke -> onFavourite(poke)},
                        isFavourite = isFavourite.contains(poke)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Barra(field : TextFieldState,
          result : List<String>,
          onSearched : () -> Unit,
          onModifier: () -> Unit,
          initRicerca : () -> Unit,
          modifier: Modifier,
          nav : NavHostController,
) {
    var expanded by remember { mutableStateOf(false) }

    if(expanded){
        LaunchedEffect(true) {
            initRicerca()
        }
    }

    Row(modifier = modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically){
        SearchBar(
            modifier = modifier
                .padding(start = 10.dp, end = 10.dp)
                .weight(1f),
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
                    placeholder = {Text("Ciao ${Firebase.auth.currentUser?.email}")}
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

        if(!expanded){
            Image(
                modifier = Modifier
                    .padding(end = 10.dp, top = 25.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            nav.navigate(ProfileInfo)
                        }
                    ),
                painter = painterResource(R.drawable.icon),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun Cards(pokemons : List<UiPokemon>,
          onNavigation: (String) -> Unit,
          onFavourite: (UiPokemon) -> Unit,
          isFavourite : List<UiPokemon>,
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(
            items = pokemons,
            itemContent = {
                    poke->
                Card(poke,
                    onNavigation = onNavigation,
                    onFavourite = onFavourite,
                    isFavourite = isFavourite.contains(poke),)
            }
        )
    }
}

@Composable
fun Card(poke : UiPokemon,
         modifier: Modifier = Modifier,
         onNavigation: (String) -> Unit,
         onFavourite: (UiPokemon) -> Unit,
         isFavourite : Boolean,) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 15.dp)
            .clickable(
                enabled = true,
                onClick = {
                    Log.d(TAG, "info pokemon -> ${poke.name}")
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

            Column() {
                if(isFavourite){
                    Image(
                        modifier = modifier
                            .size(60.dp)
                            .clickable(onClick = {
                                onFavourite(poke)
                            }),
                        painter = painterResource(R.drawable.stella_gialla),
                        contentDescription = null
                    )
                }

                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = poke.name.uppercase()
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    onClickHome : () -> Unit,
    onClickFavourite : () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 55.dp)
                .height(50.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(45.dp)
                        .padding(start = 10.dp)
                        .clickable(onClick = onClickHome),
                    painter = painterResource(R.drawable.occhio),
                    contentDescription = null
                )
                Spacer(
                    modifier = Modifier.size(60.dp)
                )
                Image(
                    modifier = Modifier
                        .size(45.dp)
                        .padding(end = 10.dp)
                        .clickable(onClick = onClickFavourite),
                    painter = painterResource(R.drawable.stella_gialla),
                    contentDescription = null
                )
            }
        }
    }
}