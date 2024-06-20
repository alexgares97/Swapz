package com.eug.swapz.models

data class ChatMessage(
    var senderId: String = "",
    var text: String = "",
    var imageUrl: String? = "",
    var title: String? ="",
    var timestamp: Long = 0,
    var isSentByUser: Boolean = false,
    var articleId: String? = ""
) {
    // No-argument constructor
    constructor() : this("", "", "","",0, false, "")
}
