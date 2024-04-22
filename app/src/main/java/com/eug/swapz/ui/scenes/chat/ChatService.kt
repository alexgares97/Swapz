package com.eug.swapz.ui.scenes.chat

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatService {

    private val database: DatabaseReference by lazy {
        Firebase.database.reference.child("chats")
    }

    fun sendMessage(message: String, message1: String) {
        // Implement logic to send message to Firebase Realtime Database
        val messageId = database.push().key
        if (messageId != null) {
            database.child(messageId).setValue(message)
        }
    }
}
