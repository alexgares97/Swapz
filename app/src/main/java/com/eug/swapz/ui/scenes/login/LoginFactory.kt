package com.eug.swapz.ui.scenes.login

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.helpers.ComposableFactory


class LoginFactory(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource
) : ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val viewModel = LoginViewModel(navController, sessionDataSource)
        return LoginScene(viewModel = viewModel)
    }
}
