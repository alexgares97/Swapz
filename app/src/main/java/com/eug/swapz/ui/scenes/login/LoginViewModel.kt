package com.eug.swapz.ui.scenes.login


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.SessionDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class LoginViewModel(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource
) : ViewModel() {

    private val _loggedIn = MutableStateFlow(false)
    val loggedIn: StateFlow<Boolean> = _loggedIn

    var isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    fun isLoggedIn() {
        _loggedIn.value = sessionDataSource.isLoggedIn()
    }

    fun loginAnonimous() {
        viewModelScope.launch {
            isLoading.value = true
            val success = sessionDataSource.loginUserAnonymous()
            _loggedIn.value = success
            navigateToMain()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            val success = sessionDataSource.loginUser(email, password)
            _loggedIn.value = success
            if (!success) {
                isLoading.value = false
                errorMessage.value = "Incorrect Credentials"
            } else {
                navigateToMain()
            }

        }
    }

    /*fun signUp(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            val success = sessionDataSource.signUpUser(email, password)
            _loggedIn.value = success
            if (!success) {
                isLoading.value = false
                errorMessage.value = "Email already in use"
            } else {
                navigateToMain()
            }
        }
    }*/

    private fun navigateToMain() {
        viewModelScope.launch {
            isLoading.value = false
            if (_loggedIn.value) {
                navController.navigate(AppRoutes.MAIN.value) {
                    popUpTo(AppRoutes.LOGIN.value) {
                        inclusive = true
                    }
                }
            }
        }
    }

    fun navigateToRegister() {
        viewModelScope.launch {
            isLoading.value = false
            navController.navigate(AppRoutes.REGISTER.value) {
                popUpTo(AppRoutes.LOGIN.value) {
                    inclusive = true
                }
            }
        }
    }
}