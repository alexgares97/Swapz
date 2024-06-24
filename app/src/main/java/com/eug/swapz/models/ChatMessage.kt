package com.eug.swapz.models

data class ChatMessage(
    var senderId: String = "",
    var text: String = "",
    var imageUrl: String? = "",
    var title: String? ="",
    var timestamp: Long = 0,
    var isInventory: Boolean = false,
    var isFinalize: Boolean = false
)
