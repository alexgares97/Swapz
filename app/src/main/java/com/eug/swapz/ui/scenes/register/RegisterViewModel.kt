package com.eug.swapz.ui.scenes.register


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.SessionDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class RegisterViewModel (
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
    fun signUp(email: String, password: String, username: String, name: String) {
        viewModelScope.launch {
            isLoading.value = true
            val success = sessionDataSource.signUpUser(email, password, username, name)
            _loggedIn.value = success
            if(!success){
                isLoading.value = false
                errorMessage.value = "Email already in use"
            } else{
                navigateToLogin()
            }
        }
    }

    fun navigateToLogin(){
        viewModelScope.launch {
            navController.navigate(AppRoutes.LOGIN.value) {
                popUpTo(AppRoutes.REGISTER.value) {
                    inclusive = true
                }
            }
        }
    }

}