package com.example.esercizioapi.ui

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope

class AuthenticationModel : Activity() {
    private val TAG = "Authentication"
    private val auth = Firebase.auth

    val currentUser = auth.currentUser

    fun createNewAccount(email: String, password : String, onCreationSuccessful : () -> Unit, onCreationFailed : () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d(TAG, "createUserWithEmail:success")
                    onCreationSuccessful()
                } else{
                    Log.w(TAG, "createUserWithEmail:failed", task.exception)
                    onCreationFailed()
                }
            }
    }

    fun signInWith(email: String, password: String, onSingInSuccessful : () -> Unit, onSignInFailure : () -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d(TAG, "signInWithEmail:success")
                    onSingInSuccessful()
                }else{
                    Log.w(TAG, "signInWithEmail:failed", task.exception)
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
                    Log.d(TAG, "User password updated.")
                }else{
                    Log.w(TAG, "User password wasn't changed.")
                }
            }
    }

    fun deleteUser(OnFinished : () -> Unit) {
        currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")
                    OnFinished()
                }else{
                    Log.w(TAG, "User unsuccessfully deleted.")
                }
            }
    }
}

