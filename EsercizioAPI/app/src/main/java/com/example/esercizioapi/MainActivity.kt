package com.example.esercizioapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.esercizioapi.ui.Home
import com.example.esercizioapi.ui.PokemonScreen
import com.example.esercizioapi.ui.theme.EsercizioAPITheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import com.example.esercizioapi.data.AppContainer

@Serializable
object Main
@Serializable
data class PokemonScreen(
    val pokemon : String
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EsercizioAPITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val nav = rememberNavController()
                    val viewModel = hiltViewModel<PokeViewModel, PokeViewModel.Factory>(creationCallback = {
                        factory ->
                        val repo = AppContainer()
                        factory.create(repo.repository)
                    })

                    NavHost(navController = nav, startDestination = Main) {
                        composable<Main> { Home(
                            modifier = Modifier.padding(innerPadding),
                            viewModel,
                            onNavigation = { pokemon ->
                                if(!viewModel.searchName(pokemon).isEmpty())
                                    nav.navigate(PokemonScreen(pokemon = pokemon))
                            }
                        ) }
                        composable<PokemonScreen>{ entry ->
                            val poke = entry.toRoute<PokemonScreen>()
                            PokemonScreen(
                                poke.pokemon,
                                modifier =  Modifier.padding(innerPadding),
                                viewModel,
                                onNavigation = {
                                    nav.navigate(Main)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}