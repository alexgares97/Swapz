package com.eug.swapz.ui.scenes.inventory

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource
) : ViewModel() {
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles
    private val auth = FirebaseAuth.getInstance()
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username
    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category


    init {
        // Call retrieveUsername() when the ViewModel is initialized
        retrieveUsername()
    }
    private fun getCurrentUserId(): String? {
        return sessionDataSource.getCurrentUserId()

    }
    fun fetch() {
        viewModelScope.launch {
            val userId = getCurrentUserId()
            if (!userId.isNullOrEmpty()) {
                val articleList = articlesDataSource.getUserArticles(userId)
                _articles.value = articleList
                // No need to call subscribe here, as fetching user articles already does the job
            } else {
                Log.e("InventoryViewModel", "User ID is null or empty")
            }
        }
    }
    private fun retrieveUsername() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            _username.value = currentUser?.displayName
        }
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
    fun navigateToMain() {
        viewModelScope.launch {
            navController.navigate(AppRoutes.MAIN.value) {
                popUpTo(AppRoutes.LOGIN.value) {
                    inclusive = true
                }
            }
        }
    }
    fun deleteArticle(article: String?) {
        viewModelScope.launch {
            try {
                // Perform deletion action using ArticlesDataSource
                if (article != null) {
                    articlesDataSource.deleteArticle(article)
                }
                fetch()
            } catch (e: Exception) {
                // Handle deletion failure
                // You can show a toast message or log the error
                // For simplicity, we'll just log the error here
                e.printStackTrace()
            }
        }
    }
}
