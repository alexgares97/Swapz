package com.eug.swapz.models

data class Chat(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp: Long,
    val userName: String,
    val lastMessage: String
)
