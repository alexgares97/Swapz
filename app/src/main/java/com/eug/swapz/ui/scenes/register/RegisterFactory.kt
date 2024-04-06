package com.eug.swapz.ui.scenes.register


import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.helpers.ComposableFactory
import com.eug.swapz.ui.scenes.register.RegisterViewModel

class RegisterFactory(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource
) : ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val viewModel = RegisterViewModel(navController, sessionDataSource)
        return RegisterScene (viewModel = viewModel)

    }
}