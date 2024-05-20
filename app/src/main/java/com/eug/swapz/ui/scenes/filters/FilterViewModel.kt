package com.eug.swapz.ui.scenes.filters


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FilterViewModel(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource
) : ViewModel() {
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles
    private val auth = FirebaseAuth.getInstance()

    fun fetch() {
        val category = getCategoryFromRoute()
        viewModelScope.launch {
            try {
                val articleList = articlesDataSource.getCategoryArticles(category)
                _articles.value = articleList
            } catch (e: Exception) {
                Log.e("FilterViewModel", "Error fetching articles for category: $category", e)
            }
        }
    }
    private fun getCategoryFromRoute(): String {
        val navBackStackEntry = navController.currentBackStackEntry ?: return ""
        val arguments = navBackStackEntry.arguments
        return arguments?.getString("category") ?: ""
    }

    fun subscribe(){
        viewModelScope.launch {
            articlesDataSource.subscribe {
                _articles.value = it
            }
        }
    }
    fun home() {
        navController.popBackStack()
    }

    fun signOut() {
        viewModelScope.launch {
            sessionDataSource.signOutUser()
            navController.navigate(AppRoutes.LOGIN.value) {
                popUpTo(AppRoutes.MAIN.value) {
                    inclusive = true
                }
            }
        }
    }

    fun navigateToDetail(article: Article) {
        viewModelScope.launch {
            Log.d("Navigating to article detail", "" + article.id)
            navController.navigate(AppRoutes.ARTICLE_DETAIL.value + "/" + article.id)
        }
    }

    fun navigateToAddArticle() {
        viewModelScope.launch {
            Log.d("Navigating to Add Article", "")
            navController.navigate(AppRoutes.ADD_ARTICLE.value)
        }
    }

    fun navigateToInventory() {
        viewModelScope.launch {
            Log.d("Navigating to Add Article", "")
            navController.navigate(AppRoutes.INVENTORY.value)
        }
    }
    fun navigateToChatList(){
        viewModelScope.launch {
            navController.navigate(AppRoutes.CHAT_LIST.value)
        }
    }
}
