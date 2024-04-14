package com.eug.swapz.repository

interface ChatRepository {
    suspend fun sendMessage(message: String)

}