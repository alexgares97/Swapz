package com.eug.swapz.ui.scenes.editarticle

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.helpers.ComposableFactory
import com.eug.swapz.ui.scenes.addarticle.AddArticle
import com.eug.swapz.ui.scenes.addarticle.AddArticleViewModel

class EditArticleFactory (
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource
) :
    ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val viewModel = EditArticleViewModel(navController = navController, sessionDataSource = sessionDataSource, articlesDataSource = articlesDataSource)
        return EditArticle(viewModel = viewModel, articleId = id ?: "")
    }
}