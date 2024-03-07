package com.eug.swapz.ui.scenes.intro


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import kotlinx.coroutines.launch

class IntroViewModel (private val navController: NavController) : ViewModel() {
    fun navigateToLogin(){
        viewModelScope.launch {
            navController.navigate(AppRoutes.LOGIN.value) {
                popUpTo(AppRoutes.INTRO.value) {
                    inclusive = true
                }
            }
        }
    }
}