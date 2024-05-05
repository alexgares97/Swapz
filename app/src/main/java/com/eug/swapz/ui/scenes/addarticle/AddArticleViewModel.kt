package com.eug.swapz.ui.scenes.addarticle

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
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

class AddArticleViewModel (
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource

    ) : ViewModel() {
    var imageUrl by mutableStateOf(emptyList<String>())


    // Function to update the image URI
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

    fun home() {
        navController.popBackStack()
    }
    fun navigateToMain() {
        viewModelScope.launch {
            navController.navigate(AppRoutes.MAIN.value)
        }
    }
    fun navigateToAddArticle(){
        viewModelScope.launch {
            Log.d("Navigating to Add Article", "")
            navController.navigate(AppRoutes.ADD_ARTICLE.value)
        }
    }
    private fun getCurrentUserId(): String? {
        return sessionDataSource.getCurrentUserId()
    }

    fun addArticle(title: String, desc: String, status: String, cat: String, value: Int, img: List<String>)
    {
        val user = getCurrentUserId()
        viewModelScope.launch {
            val article = user?.let {
                Article(
                    title = title,
                    desc = desc,
                    status = status,
                    cat = cat,
                    value = value,
                    carrusel = img,
                    user = it
                )
            }
            try {
                if (article != null) {
                    articlesDataSource.addArticle(article)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

}