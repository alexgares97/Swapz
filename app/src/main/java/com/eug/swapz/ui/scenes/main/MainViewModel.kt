package com.eug.swapz.ui.scenes.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.MainDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import kotlinx.coroutines.launch

class MainViewModel(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val mainDataSource: MainDataSource
) : ViewModel() {
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    fun fetch(){
        viewModelScope.launch {
            val articleList = mainDataSource.fetch()
            _articles.value = articleList
            subscribe()
        }
    }

    fun subscribe(){
        viewModelScope.launch {
            mainDataSource.subscribe {
                _articles.value = it
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            sessionDataSource.signOutUser()
            navController.navigate(AppRoutes.LOGIN.value){
                popUpTo(AppRoutes.MAIN.value){
                    inclusive = true
                }
            }
        }
    }

    fun navigateToDetail(category: Article){
        viewModelScope.launch {
            Log.d("Navigating to category", ""+category.id)
            navController.navigate(AppRoutes.ARTICLE_DETAIL.value+"/"+category.id)
        }
    }
}
