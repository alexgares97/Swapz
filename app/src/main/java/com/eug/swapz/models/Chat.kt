package com.eug.swapz.models

data class Chat(
    val id: String,
    val otherUserId: String,
    val name: String,
    val text: String,
    val photoUrl: String,
)
