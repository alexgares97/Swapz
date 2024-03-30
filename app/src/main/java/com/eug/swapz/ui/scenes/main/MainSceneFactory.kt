package com.eug.swapz.ui.scenes.main


import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.datasources.MainDataSource
import com.eug.swapz.helpers.ComposableFactory

class MainSceneFactory (
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val mainDataSource: MainDataSource
) :
    ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val viewModel = MainViewModel(navController, sessionDataSource, mainDataSource)
        return MainScene(viewModel)
    }
}