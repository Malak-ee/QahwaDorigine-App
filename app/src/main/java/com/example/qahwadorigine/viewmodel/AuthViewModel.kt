package com.example.qahwadorigine.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qahwadorigine.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // Simulation d'authentification
            kotlinx.coroutines.delay(1000) // Simule un appel réseau

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val user = User(
                    email = email,
                    name = email.substringBefore("@"),
                    password = null
                )
                _currentUser.value = user
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Email ou mot de passe invalide")
            }
        }
    }

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            kotlinx.coroutines.delay(1000)

            if (name.isNotEmpty() && email.isNotEmpty() && password.length >= 6) {
                val user = User(email = email, name = name, password = null)
                _currentUser.value = user
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Informations invalides")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}