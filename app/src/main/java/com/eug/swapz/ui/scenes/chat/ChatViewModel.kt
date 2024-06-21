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
    val messagesList = mutableListOf  <ChatMessage>()
    private val _otherUserPhotoUrl = MutableLiveData<String?>()
    val otherUserPhotoUrl: LiveData<String?> = _otherUserPhotoUrl
    private val _otherUserName = MutableLiveData<String?>()
    val otherUserName: LiveData<String?> = _otherUserName

    fun listenForChatMessages(currentChatId: String) {
        Log.d("ChatViewModel", "CHATID: $currentChatId")
        val chatQuery = databaseReference.child(currentChatId)
        val chatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                // Iterate through each child node (message ID) under the currentChatId
                snapshot.children.forEach { messageSnapshot ->
                    // Retrieve senderId, text, imageUrl, and title from the messageSnapshot
                    val senderId = messageSnapshot.child("senderId").getValue(String::class.java)
                    val text = messageSnapshot.child("text").getValue(String::class.java)
                    val imageUrl = messageSnapshot.child("imageUrl").getValue(String::class.java)
                    val title = messageSnapshot.child("title").getValue(String::class.java)

                    // Check if all required fields are not null
                    if (senderId != null && text != null) {
                        val chatMessage = ChatMessage(senderId, text, imageUrl, title)
                        messages.add(chatMessage)
                    }
                }
                // Update _messages LiveData with the new message list
                _messages.value = messages
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("ChatViewModel", "Database error: ${error.message}")
            }
        }
        // Add a single ValueEventListener to the chatQuery
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
    fun cleanupChatMessagesListener() {
        // Remove the listener for chat messages here
        // For example, if you're using Firebase Realtime Database:
        chatQuery.removeEventListener(chatListener)
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
    fun updateOtherUserDetails(chatId: String, currentUserId: String) {
        // Split the chat ID to retrieve the user IDs
        val userIds = chatId.split("-")

        // Find the user ID that is not the current user's ID
        val otherUserId = userIds.find { it != currentUserId }

        // Ensure otherUserId is not null
        otherUserId?.let { userId ->
            // Retrieve user details from the Firebase Realtime Database
            val usersReference = FirebaseDatabase.getInstance().getReference("users")
            val userQuery = usersReference.child(userId)
            userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val photoUrl = snapshot.child("photo").getValue(String::class.java)
                    val name = snapshot.child("name").getValue(String::class.java)

                    // Update the LiveData with the other user's photo URL
                    _otherUserPhotoUrl.value = photoUrl
                    _otherUserName.value = name
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Log.e("ChatViewModel", "Database error: ${error.message}")
                }
            })
        }
    }


    fun setChatId(chatId: String) {
        node = chatId
    }

    fun getCurrentUserId(): String? {
        return sessionDataSource.getCurrentUserId()
    }
    fun cancelExchange(chatId: String) {
        viewModelScope.launch {
            // Logic to cancel the exchange and clean up the chat
            // For example, you might delete the chat or remove the related messages
            val chatsRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId)
            chatsRef.removeValue().addOnSuccessListener {
                Log.d("ChatViewModel", "Exchange cancelled successfully")
            }.addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error cancelling exchange", e)
            }
        }
    }
    override fun onCleared() {
        // Remove the ValueEventListener when ViewModel is cleared
        chatQuery.removeEventListener(chatListener)
        super.onCleared()
    }

    fun goBack() {
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

