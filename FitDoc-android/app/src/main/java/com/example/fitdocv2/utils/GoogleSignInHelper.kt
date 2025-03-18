package com.example.fitdocv2.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.IntentSenderRequest
import com.example.fitdocv2.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class GoogleSignInHelper(private val context: Context) {
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private val auth = Firebase.auth

    open fun startSignIn(onSuccess: (IntentSenderRequest) -> Unit, onFailure: (Exception) -> Unit) {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id)) // Add your web client ID
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                onSuccess(intentSenderRequest)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    open fun handleSignInResult(data: Intent?, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            onFailure(task.exception ?: Exception("Unknown error"))
                        }
                    }
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}