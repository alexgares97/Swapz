package com.eug.swapz.ui.scenes.inventory

import com.eug.swapz.ui.scenes.main.MainScene
import com.eug.swapz.ui.scenes.main.MainViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.helpers.ComposableFactory

class InventoryFactory (
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource
) :
    ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val viewModel = InventoryViewModel(navController, sessionDataSource, articlesDataSource)
        return InventoryScene(viewModel = viewModel)
    }
}