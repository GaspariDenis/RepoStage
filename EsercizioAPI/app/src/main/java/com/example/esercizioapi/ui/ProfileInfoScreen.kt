package com.example.esercizioapi.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.esercizioapi.Authentication
import com.example.esercizioapi.ChangePassword
import com.example.esercizioapi.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileInfoScreen(modifier: Modifier = Modifier, nav : NavHostController){
    val authenticator : AuthenticationModel by remember { mutableStateOf(AuthenticationModel()) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(60.dp)
                        .clickable(
                            onClick = {
                                nav.popBackStack()
                            }
                        ),
                    painter = painterResource(R.drawable.freccia_indietro),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    textAlign = TextAlign.Center,
                    text = "User: ${authenticator.currentUser?.email}"
                )
            }
        }
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ){
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                onClick = {
                    nav.navigate(ChangePassword)
                }
            ) {
                Text(text = "Change Password")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                onClick = {
                    authenticator.signOut()
                    nav.navigate(Authentication)
                }
            ) {
                Text(text = "Sign Out")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                onClick = {
                    authenticator.deleteUser {
                        nav.navigate(Authentication)
                    }
                }
            ) {
                Text(text = "Delete Account")
            }
        }
    }
}