package com.eug.swapz.ui.scenes.register


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.helpers.ComposableFactory
import com.eug.swapz.ui.scenes.register.RegisterViewModel
import com.google.api.Context

class RegisterFactory(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource
) : ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val context = LocalContext.current // Obtener el contexto actual utilizando LocalContext.current
        val viewModel = RegisterViewModel(navController, sessionDataSource, context)
        return RegisterScene (viewModel = viewModel)

    }
}