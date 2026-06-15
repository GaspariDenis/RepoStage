package com.example.esercizioapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.esercizioapi.ui.Home
import com.example.esercizioapi.ui.PokemonScreen
import com.example.esercizioapi.ui.theme.EsercizioAPITheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@Serializable
object Main
@Serializable
data class PokemonRoute(
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

                    NavHost(navController = nav, startDestination = Main) {
                        composable<Main> { Home(
                            modifier = Modifier.padding(innerPadding),
                            nav = nav
                            ) }
                        composable<PokemonRoute>{ entry ->
                            val poke = entry.toRoute<PokemonRoute>()
                            PokemonScreen(
                                poke.pokemon,
                                modifier =  Modifier.padding(innerPadding),
                                nav = nav
                            )
                        }
                    }
                }
            }
        }
    }
}