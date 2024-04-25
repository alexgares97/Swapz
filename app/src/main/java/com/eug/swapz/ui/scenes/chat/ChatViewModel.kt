package com.eug.swapz.ui.scenes.chat


import android.util.Base64
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
    private val _articleId = MutableLiveData<Article?>()
    val articleId: MutableLiveData<Article?> = _articleId
    private val _article = MutableLiveData<Article?>()
    val article: MutableLiveData<Article?> = _article
    var node: String = ""

    fun listenForChatMessages(chatId: String) {
        Log.d("ChatViewmodel", "CHATID: $articleId")
        val chatQuery = databaseReference.child(chatId)
        val chatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messagesList = mutableListOf<ChatMessage>()
                snapshot.children.forEach { nodeSnapshot ->
                    nodeSnapshot.children.forEach { messageSnapshot ->
                        val senderId = messageSnapshot.child("senderId").getValue(String::class.java)
                        val text = messageSnapshot.child("text").getValue(String::class.java)
                        // You may also want to retrieve the timestamp here

                        senderId?.let { senderId ->
                            text?.let { text ->
                                val chatMessage = ChatMessage(senderId, text)
                                messagesList.add(chatMessage)
                            }
                        }
                    }
                }
                _messages.postValue(messagesList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("ChatViewModel", "Database error: ${error.message}")
            }
        }
        chatQuery.addValueEventListener(chatListener)
        this.chatQuery = chatQuery
        this.chatListener = chatListener
    }



    fun sendMessage(message: String?) {
        val currentUserUid = sessionDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            try {
                // Create a new message node under the chatId node in the Firebase Realtime Database
                val messageId = databaseReference.child(node).push().key ?: ""
                val messageData = mapOf(
                    "senderId" to currentUserUid,
                    "text" to message,
                    "timestamp" to ServerValue.TIMESTAMP
                )
                databaseReference.child(node).child(messageId).setValue(messageData)
                    .addOnSuccessListener {
                        // Message sent successfully
                        Log.d("ChatViewModel", "Message sent successfully")
                        // Log the original chatId for reference
                        Log.d("ChatViewModel", "Original chatId: $node")
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
    }

    fun setArticleId(articleId: String) {
        // Fetch the article from the data source
        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "Fetching article for articleId: $articleId")
                val article = articlesDataSource.getArticleById(articleId)
                Log.d("ChatViewModel", "Fetched article: $article")
                _article.value = article
            } catch (e: Exception) {
                // Handle error
                _message.value = "Error fetching article: ${e.message}"
            }
        }
    }
    fun setChatId(chatId: String) {
        node = chatId
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

