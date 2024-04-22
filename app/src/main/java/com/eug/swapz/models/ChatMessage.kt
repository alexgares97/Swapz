package com.eug.swapz.models

data class ChatMessage(
    var senderId: String = "",
    var text: String = "",
    var timestamp: Long = 0,
    var isSentByUser: Boolean = false
) {
    // No-argument constructor
    constructor() : this("", "", 0, false)
}
