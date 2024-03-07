package com.eug.swapz.ui.scenes.intro


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.eug.swapz.helpers.ComposableFactory


class IntroFactory (private val navController: NavController) :
    ComposableFactory<Any> {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun create(id: String?): Any {
        val viewModel = IntroViewModel(navController)
        return IntroScene(viewModel)
    }
}