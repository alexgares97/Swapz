package com.eug.swapz.ui.scenes.articleDetail


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.eug.swapz.datasources.MainDataSource
import com.eug.swapz.helpers.ComposableFactory

class ArticleDetailFactory(
    private val navController: NavController,
    private val mainDataSource: MainDataSource
) : ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val article = id?.let { mainDataSource.get(it) }
        val context = LocalContext.current // Obtener el contexto actual utilizando LocalContext.current
        val viewModel = ArticleDetailViewModel(navController = navController, context = context, article = article)
        return ArticleDetail(viewModel) // Pasar el contexto como argumento
    }
}