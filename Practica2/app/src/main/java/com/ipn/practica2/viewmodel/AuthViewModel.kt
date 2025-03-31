package com.ipn.practica2.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipn.practica2.data.model.RegisterRequest
import com.ipn.practica2.data.model.User
import com.ipn.practica2.data.network.RetrofitInstance
import com.ipn.practica2.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel : ViewModel() {

    lateinit var userPrefs: UserPreferences

    fun init(context: Context) {
        userPrefs = UserPreferences(context)
    }

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    fun registerUser(user: RegisterRequest) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            try {
                val response = RetrofitInstance.api.registerUser(user)
                if (response.isSuccessful) {
                    _registerState.value = AuthState.Success(response.body())
                } else {
                    _registerState.value = AuthState.Error("Error: ${response.code()}")
                }
            } catch (e: IOException) {
                _registerState.value = AuthState.Error("Sin conexión al servidor.")
            } catch (e: HttpException) {
                _registerState.value = AuthState.Error("Error HTTP inesperado.")
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val user = User(name = "", email = email, password = password)
                val response = RetrofitInstance.api.loginUser(user)
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        val previousImageUri = userPrefs.getUserData().map { it.imageUri }.first()
                        val finalImageUri = if (!it.profileImageUrl.isNullOrBlank()) {
                            it.profileImageUrl!!
                        } else {
                            previousImageUri
                        }

                        userPrefs.saveUser(
                            id = it.id ?: "",
                            name = it.name,
                            email = it.email,
                            role = it.role,
                            token = it.token ?: "",
                            imageUri = finalImageUri
                        )
                    }
                    _loginState.value = AuthState.Success(user)
                } else {
                    _loginState.value = AuthState.Error("Credenciales incorrectas")
                }
            } catch (e: IOException) {
                _loginState.value = AuthState.Error("Sin conexión al servidor.")
            } catch (e: HttpException) {
                _loginState.value = AuthState.Error("Error inesperado del servidor.")
            }
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val user: User?) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}