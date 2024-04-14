package com.eug.swapz.repository

import android.content.ContentValues.TAG
import android.util.Log

class DefaultChatRepository(private val chatService: ChatRepository) : ChatRepository {

    override suspend fun sendMessage(message: String) {
        try {
            // Call a method in the chat service to send the message
            chatService.sendMessage(message)
            // Handle success
        } catch (e: Exception) {
            // Handle error
            Log.e(TAG, "Error sending message: $e")
        }
    }
}
