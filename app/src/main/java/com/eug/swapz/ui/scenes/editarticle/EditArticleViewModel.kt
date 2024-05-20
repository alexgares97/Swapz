package com.eug.swapz.ui.scenes.editarticle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import kotlinx.coroutines.launch

class EditArticleViewModel(
    private val articlesDataSource: ArticlesDataSource,
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource
) : ViewModel() {

    private val _article = MutableLiveData<Article?>()
    val article: LiveData<Article?> = _article

    fun fetchArticle(articleId: String) {
        viewModelScope.launch {
            val fetchedArticle = articlesDataSource.getArticleById(articleId)
            _article.value = fetchedArticle
        }
    }

    fun updateArticle(
        articleId: String,
        title: String,
        desc: String,
        status: String,
        cat: String,
        value: Int,
        carrusel: List<String>,
        img: String,
        user: String
    ) {
        viewModelScope.launch {
            val updatedArticle = Article(
                id = articleId,
                title = title,
                desc = desc,
                status = status,
                cat = cat,
                value = value,
                carrusel = carrusel,
                img = img,
                user = user
            )
            articlesDataSource.updateArticle(updatedArticle)
            navigateToMain()
        }
    }

    fun navigateToMain() {
        navController.navigate(AppRoutes.MAIN.value)
    }
}
