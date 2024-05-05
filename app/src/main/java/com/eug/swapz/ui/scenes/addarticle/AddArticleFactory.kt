package com.eug.swapz.ui.scenes.addarticle

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.helpers.ComposableFactory

class AddArticleFactory(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource
) :
    ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val viewModel = AddArticleViewModel(navController = navController, sessionDataSource = sessionDataSource, articlesDataSource = articlesDataSource)
        return AddArticle(viewModel) // Pasar el contexto como argumento
    }
}