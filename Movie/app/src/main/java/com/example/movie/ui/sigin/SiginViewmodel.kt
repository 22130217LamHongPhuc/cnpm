package com.example.movie.ui.sigin

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import com.example.movie.ui.detail.UiState
import com.example.movie.ui.repository.EmailAuthRepo
import com.example.movie.ui.repository.FacebookAuthHelper
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class SiginViewmodel @Inject constructor(val auth:EmailAuthRepo) : ViewModel(){
    private var _stateSignin = MutableStateFlow<UiState<String>>(UiState.Default)

    var stateSignin = _stateSignin.asStateFlow()
    val callbackManager = CallbackManager.Factory.create()
    val loginManager = com.facebook.login.LoginManager.getInstance()

    fun openDialogLogin(activity: ComponentActivity, launcher: ActivityResultLauncher<IntentSenderRequest>){
         auth.signIn(activity,launcher)
    }



    suspend fun handleSignInResult(data: Intent?) {
        try {
            _stateSignin.value = UiState.Loading

            val result = auth.handleSignInResult(data)

            result.onSuccess {
                _stateSignin.value = UiState.Success(it)
            }
            result.onFailure {
                _stateSignin.value = UiState.Error(it.message ?: "Đăng nhập thất bại")
            }

        } catch (e: Exception) {
            _stateSignin.value = UiState.Error(e.message ?: "Lỗi không xác định")
        }
    }

    suspend fun siginWithEmail(email: String, pass: String) {

        try {
            _stateSignin.value = UiState.Loading
            Log.d("SignIn", "⏳ Đang gọi handleSignInResult...")

            val result = auth.signInWithEmail(email,pass)

            Log.d("SignIn", "✅ Kết quả nhận được: $result")

            result.onSuccess {
                Log.d("SignIn", "🎉 Đăng nhập thành công: $it")
                _stateSignin.value = UiState.Success(it)
            }
            result.onFailure {
                Log.e("SignIn", "❌ Đăng nhập thất bại: ${it.message}")
                _stateSignin.value = UiState.Error(it.message ?: "Đăng nhập thất bại")
            }

        } catch (e: Exception) {
            Log.e("SignIn", "⚠️ Lỗi ngoại lệ: ${e.message}")
            _stateSignin.value = UiState.Error(e.message ?: "Lỗi không xác định")
        }
    }

    fun creaditFacebookForFirebase(result: LoginResult,activity: ComponentActivity) {
         try {
            val idToken = result.accessToken.token

            if (idToken != null) {
                val credential = FacebookAuthProvider.getCredential(idToken)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("SignIn", "Firebase sign-in successful")
                                _stateSignin.value = UiState.Success("Đăng nhập thành công")

                            } else {
                                Log.e("SignIn", "Firebase sign-in failed", task.exception)
                                _stateSignin.value = UiState.Error(task.exception?.message ?: "Đăng nhập thất bại")

                            }
                        }

            } else {
                Log.e("SignIn", "No ID Token!")
            }
        } catch (e: ApiException) {
             _stateSignin.value = UiState.Error(e.message ?: "Đăng nhập thất bại")
         }
    }

    fun setErrorLogin(error: String) {
        _stateSignin.value =UiState.Error(error)
    }


}

