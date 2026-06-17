package com.example.esercizioapi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.esercizioapi.CreateAccount
import com.example.esercizioapi.Main
import com.example.esercizioapi.R

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
        var text by remember { mutableStateOf("") }

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            text = text
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            value = fieldEmail.text.toString(),
            onValueChange = {
                fieldEmail.edit { replace(0,length,it) }
            },
            label = {Text(text="Email")}
        )

        Row() {
            var PasswordView by remember { mutableStateOf(false) }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = fieldPassword.text.toString(),
                onValueChange = {
                    fieldPassword.edit { replace(0,length,it) }
                },
                label = {Text(text="Password")},
                visualTransformation = if(PasswordView) VisualTransformation.None else PasswordVisualTransformation()
            )

            Icon(
                modifier = Modifier.size(60.dp)
                    .clickable(
                        onClick = {
                            PasswordView = !PasswordView
                        }
                    ),
                painter = painterResource(R.drawable.occhio),
                contentDescription = null
            )
        }

        TextButton(
            onClick = {
                if(fieldEmail.text.toString() != "" && fieldPassword.text.toString() != ""){
                    Authenticator.signInWith(
                        fieldEmail.text.toString(),
                        fieldPassword.text.toString(),
                        {
                            nav.navigate(Main)
                        },
                        {
                            text = "Errore, riprova."
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