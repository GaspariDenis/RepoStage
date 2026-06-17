package com.example.esercizioapi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ChangePasswordScreen(modifier: Modifier = Modifier, nav: NavHostController) {
    val authenticator by remember { mutableStateOf(AuthenticationModel()) }
    var fieldPassword by remember { mutableStateOf(TextFieldState()) }

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
                    .padding(top = 60.dp),
                value = fieldPassword.text.toString(),
                onValueChange = {
                    fieldPassword.edit { replace(0,length,it) }
                },
                visualTransformation = PasswordVisualTransformation(),
                label = {Text(text="Password")}
            )
        }

        TextButton(
            onClick = {
                if(fieldPassword.text.toString().length >= 6){
                    authenticator.modifyPassword(fieldPassword.text.toString())

                    if(authenticator.currentUser != null){
                        nav.popBackStack()
                    }
                }
            },
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
        ) {
            Text(text = "Cambia Password")
        }
    }
}