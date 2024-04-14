package com.eug.swapz.ui.scenes.chat


import ChatScene
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.helpers.ComposableFactory

class ChatFactory (
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,

) : ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val viewModel = ChatViewModel(navController, sessionDataSource)
        return ChatScene(viewModel)
    }
}
