package com.eug.swapz.ui.scenes.articleDetail
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

import com.eug.swapz.models.Article

class ArticleDetailViewModel (
    private val navController: NavController,
    private val context: Context,
    internal val article: Article?,


    ) : ViewModel(){
    fun signOut() {
        TODO("Not yet implemented")
    }




}