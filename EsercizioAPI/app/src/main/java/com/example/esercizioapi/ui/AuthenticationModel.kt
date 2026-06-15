package com.example.esercizioapi.ui

import android.app.Activity
import android.util.Log
import com.example.esercizioapi.network.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class AuthenticationModel : Activity() {
    private val TAG = "Authentication"
    private val auth = Firebase.auth

    val currentUser = auth.currentUser

    fun createNewAccount(email: String, password : String, onCreationSuccessful : () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d(TAG, "createUserWithEmail:success")
                    onCreationSuccessful()
                } else{
                    Log.w(TAG, "createUserWithEmail:failed", task.exception)
                }
            }
    }

    fun singInWith(email: String, password: String, onSingInSuccessful : () -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d(TAG, "signInWithEmail:success")
                    onSingInSuccessful()
                }else{
                    Log.w(TAG, "signInWithEmail:failed", task.exception)
                }
            }
    }

}