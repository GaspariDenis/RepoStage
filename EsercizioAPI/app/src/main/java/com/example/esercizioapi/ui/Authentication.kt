package com.example.esercizioapi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.esercizioapi.Authentication
import com.example.esercizioapi.CreateAccount
import com.example.esercizioapi.Main
import com.example.esercizioapi.network.UiState
import kotlin.io.path.fileVisitor

@Composable
fun AuthenticationScreen(modifier: Modifier = Modifier, nav : NavHostController) {
    var fieldEmail by remember { mutableStateOf(TextFieldState()) }
    var fieldPassword by remember { mutableStateOf(TextFieldState()) }
    val Authenticator by remember { mutableStateOf(AuthenticationModel()) }

    if(Authenticator.currentUser != null){
        nav.navigate(Main)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp),
                value = fieldEmail.text.toString(),
                onValueChange = {
                    fieldEmail.edit { replace(0,length,it) }
                },
                label = {Text(text="Email")}
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                value = fieldPassword.text.toString(),
                onValueChange = {
                    fieldPassword.edit { replace(0,length,it) }
                },
                label = {Text(text="Password")},
                visualTransformation = PasswordVisualTransformation()
            )
        }

        TextButton(
            onClick = {
                if(fieldEmail.text.toString() != "" && fieldPassword.text.toString() != ""){
                    Authenticator.singInWith(
                        fieldEmail.text.toString(),
                        fieldPassword.text.toString(),
                        {
                            nav.navigate(Main)
                        })
                }
            },
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
        ) {
            Text(text = "SingIn")
        }

        TextButton(
            onClick = {
                nav.navigate(CreateAccount)
            },
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
        ) {
            Text(text = "Crea nuovo account")
        }
    }
}