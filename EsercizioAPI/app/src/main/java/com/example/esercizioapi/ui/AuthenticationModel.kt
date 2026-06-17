package com.example.esercizioapi.ui

import android.app.Activity
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class AuthenticationModel : Activity() {
    private val tag = "Authentication"
    private val auth = Firebase.auth

    val currentUser = auth.currentUser

    fun createNewAccount(email: String, password : String, onCreationSuccessful : () -> Unit, onCreationFailed : () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d(tag, "createUserWithEmail:success")
                    onCreationSuccessful()
                } else{
                    Log.w(tag, "createUserWithEmail:failed", task.exception)
                    onCreationFailed()
                }
            }
    }

    fun signInWith(email: String, password: String, onSingInSuccessful : () -> Unit, onSignInFailure : () -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d(tag, "signInWithEmail:success")
                    onSingInSuccessful()
                }else{
                    Log.w(tag, "signInWithEmail:failed", task.exception)
                    onSignInFailure()
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun modifyPassword(password: String){
        currentUser?.updatePassword(password)
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.d(tag, "User password updated.")
                }else{
                    Log.w(tag, "User password wasn't changed.")
                }
            }
    }

    fun deleteUser(onFinished : () -> Unit) {
        currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.d(tag, "User account deleted.")
                    onFinished()
                }else{
                    Log.w(tag, "User unsuccessfully deleted.")
                }
            }
    }
}

