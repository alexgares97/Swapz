package com.eug.swapz.ui.scenes.main

import android.content.ContentValues
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class MainViewModel(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource
) : ViewModel() {
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles
    private val currentUserId = sessionDataSource.getCurrentUserId()

    fun fetch(){
        viewModelScope.launch {
            val allArticles = articlesDataSource.fetch()

            val articleList = allArticles.filter { it.user != currentUserId }

            _articles.value = articleList
            if (currentUserId != null) {
                subscribe(currentUserId)
            }
        }
    }

    private fun subscribe(currentUserId: String) {
        viewModelScope.launch {
            articlesDataSource.subscribe { articles ->
                // Filter articles on update as well
                _articles.value = articles.filter { it.user != currentUserId }
            }
        }
    }
    fun navigateToDetail(article: Article){
        viewModelScope.launch {
            Log.d("Navigating to Article Detail", ""+article.id)
            navController.navigate(AppRoutes.ARTICLE_DETAIL.value+"/"+article.id)
        }
    }
    fun navigateToAddArticle(){
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

    fun navigateToFilter(category: String){
        viewModelScope.launch {
            navController.navigate(AppRoutes.FILTER.value+"/"+category)
        }

    }
    fun navigateToMain(){
        viewModelScope.launch {
            navController.navigate(AppRoutes.MAIN.value)
        }
    }
}
