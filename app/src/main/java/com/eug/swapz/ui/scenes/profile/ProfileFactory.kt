package com.eug.swapz.ui.scenes.profile

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.helpers.ComposableFactory

class ProfileFactory (
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource
):
    ComposableFactory<Any> {
        @Composable
        override fun create(id: String?) : Any{
            val viewModel = ProfileViewModel(navController, sessionDataSource, articlesDataSource)
           return ProfileScene(viewModel = viewModel)
        }
        @Composable
        fun CreateWithUser(userId: String?) {
            val viewModel = ProfileViewModel(navController, sessionDataSource, articlesDataSource)
            userId?.let { viewModel.setUserId(it) }
            ProfileScene(viewModel = viewModel)
        }

    }