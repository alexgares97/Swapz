package com.eug.swapz.ui.scenes.articleDetail


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.helpers.ComposableFactory

class ArticleDetailFactory(
    private val navController: NavController,
    private val articlesDataSource: ArticlesDataSource,
    private val sessionDataSource: SessionDataSource,

    ) : ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val article = id?.let { articlesDataSource.get(it) }
        val context = LocalContext.current // Obtener el contexto actual utilizando LocalContext.current
        val viewModel = ArticleDetailViewModel(navController = navController, context = context, article = article, sessionDataSource = sessionDataSource)
        return ArticleDetail(viewModel) // Pasar el contexto como argumento
    }
}