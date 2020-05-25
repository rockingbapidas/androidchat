package com.bapidas.chattingapp.data.core

import com.bapidas.chattingapp.data.core.callbacks.ResultInterface
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by bapidas on 26/07/17.
 */
class AuthHandler {
    lateinit var firebaseAuth: FirebaseAuth

    fun performLogin(email: String, password: String, resultInterface: ResultInterface) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        resultInterface.onSuccess(task.toString())
                    }
                }
                .addOnFailureListener { e -> resultInterface.onFail(e.message.orEmpty()) }
    }

    fun performSignup(email: String, password: String, resultInterface: ResultInterface) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        resultInterface.onSuccess(task.toString())
                    }
                }
                .addOnFailureListener { e -> resultInterface.onFail(e.message.orEmpty()) }
    }
}