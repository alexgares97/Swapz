package com.eug.swapz.ui.scenes.chat


import ChatScene
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.helpers.ComposableFactory

class ChatFactory (
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource

) : ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val viewModel = ChatViewModel(navController, sessionDataSource, articlesDataSource)
        return ChatScene(viewModel = viewModel)
    }
    @Composable
    fun CreateWithArticleId(userId: String?, articleId: String?, chatId: String?) {
        // Create a ChatViewModel instance with the provided user ID and article ID
        val viewModel = ChatViewModel(navController, sessionDataSource, articlesDataSource)

        // Pass the user ID and article ID to the view model
        userId?.let { viewModel.setUserId(it) }
        articleId?.let { viewModel.setArticleId(it) }
        chatId?.let { viewModel.setChatId(it) }

        // Create a ChatScene composable with the ChatViewModel
        ChatScene(viewModel = viewModel)
    }
    @Composable
    fun CreateFromList(userId: String?, chatId: String?) {
        val viewModel = ChatViewModel(navController, sessionDataSource, articlesDataSource)
        userId?.let { viewModel.setUserId(it) }
        chatId?.let { viewModel.setChatId(it) }
        ChatScene(viewModel = viewModel)
    }
}
