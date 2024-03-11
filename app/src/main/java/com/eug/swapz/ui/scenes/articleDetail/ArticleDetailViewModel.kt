package com.eug.swapz.ui.scenes.articleDetail
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.ui.scenes.main.MainScene


import com.eug.swapz.models.Article
import kotlinx.coroutines.launch

class ArticleDetailViewModel (
    private val navController: NavController,
    private val context: Context,
    internal val article: Article?,
    private val sessionDataSource: SessionDataSource,




    ) : ViewModel(){
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
    fun home() {
        navController.popBackStack()
    }

   /* fun exchange(requester: requester, requested: requested, requestedArticle: requestedArticle, exchangeArticle: exchangeArticle  ){
        TODO("Not yet implemented")

    }*/




}