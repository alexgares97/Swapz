package com.eug.swapz.ui.scenes.chatList

import ChatList
import com.eug.swapz.ui.scenes.chatList.ChatListViewModel



import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.helpers.ComposableFactory

class ChatListFactory (
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
) :
    ComposableFactory<Any> {
    @Composable
    override fun create(id: String?): Any {
        val viewModel = ChatListViewModel(navController, sessionDataSource)
        return ChatList(viewModel)
    }

}