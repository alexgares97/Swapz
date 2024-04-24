package com.eug.swapz.ui.scenes.chat


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import kotlinx.coroutines.launch
import com.eug.swapz.models.Article
import com.eug.swapz.models.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class ChatViewModel(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource,
) : ViewModel() {
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("chats")
    private lateinit var chatQuery: Query
    private lateinit var chatListener: ValueEventListener
    private val _currentChatId = MutableLiveData<String>()
    val currentChatId: LiveData<String> = _currentChatId
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    private val sentMessage = MutableLiveData<String>()


    fun listenForChatMessages(chatId: String) {
        val chatQuery = databaseReference.child(chatId)
        val chatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messagesList = mutableListOf<ChatMessage>()
                for (messageSnapshot in snapshot.children) {
                    val senderId = messageSnapshot.child("senderId").getValue(String::class.java)
                    val text = messageSnapshot.child("text").getValue(String::class.java)

                    if (senderId != null && text != null) {
                        val chatMessage = ChatMessage(senderId, text)
                        messagesList.add(chatMessage)
                    }
                }
                _messages.postValue(messagesList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }
        chatQuery.addValueEventListener(chatListener)
        this.chatQuery = chatQuery
        this.chatListener = chatListener
    }

    fun sendMessage(chatId: String, message: String?) {
        val currentUserUid = sessionDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            try {
                // Push the new message to the chat node in the Firebase Realtime Database
                val messageId = databaseReference.child(chatId).push().key ?: ""
                val messageData = mapOf(
                    "senderId" to currentUserUid,
                    "text" to message,
                    "timestamp" to ServerValue.TIMESTAMP
                )
                databaseReference.child(chatId).child(messageId).setValue(messageData)
                    .addOnSuccessListener {
                        // Message sent successfully
                        Log.d("ChatViewModel", "Message sent successfully")
                    }
                    .addOnFailureListener { e ->
                        // Error sending message
                        Log.e("ChatViewModel", "Error sending message", e)
                    }
            } catch (e: Exception) {
                // Exception occurred
                Log.e("ChatViewModel", "Exception occurred while sending message", e)
            }
        }
    }


    fun setUserId(userId: String) {
        // Set the user ID when needed
    }

    fun setArticleId(articleId: String) {
        // Set the article ID when needed
    }
    fun setChatId(chatId: String) {
        _currentChatId.value = chatId
    }

    override fun onCleared() {
        // Remove the ValueEventListener when ViewModel is cleared
        chatQuery.removeEventListener(chatListener)
        super.onCleared()
    }

    fun home() {
        navController.popBackStack()
    }

    fun signOut() {
        viewModelScope.launch {
            sessionDataSource.signOutUser()
            navController.navigate(AppRoutes.LOGIN.value) {
                popUpTo(AppRoutes.MAIN.value) {
                    inclusive = true
                }
            }
        }
    }
}

