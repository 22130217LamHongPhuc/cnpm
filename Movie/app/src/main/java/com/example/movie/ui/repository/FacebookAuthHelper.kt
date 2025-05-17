package com.example.movie.ui.repository

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.movie.ui.detail.UiState
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class FacebookAuthHelper(val auth: FirebaseAuth) {
    val loginManager = LoginManager.getInstance()
    internal val callbackManager = CallbackManager.Factory.create()


    fun loginWithFacebook(activity: ComponentActivity, onSuccess: (LoginResult) -> Unit, onError: (String) -> Unit) {

        loginManager.logInWithReadPermissions(activity,listOf("public_profile", "email") )

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onCancel() {
                Log.d("sss","2")

                onError("Người dùng hủy đăng nhập.")
            }

            override fun onError(error: FacebookException) {
                Log.d("sss","2")

                onError("Lỗi Facebook: ${error.message}")
            }

            override fun onSuccess(result: LoginResult) {
                Log.d("sss","3")
                onSuccess(result)

            }
        })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode,resultCode, data)
    }


}
