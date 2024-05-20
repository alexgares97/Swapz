package com.eug.swapz.ui.scenes.articleDetail
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch


class ArticleDetailViewModel(
    private val navController: NavController,
    private val context: Context,
    internal val article: Article?,
    private val sessionDataSource: SessionDataSource,
) : ViewModel() {

    private val sentMessage = MutableLiveData<String>()
    private val _otherUserName = MutableLiveData<String?>()
    val otherUserName: LiveData<String?> = _otherUserName
    fun getCurrentUserId(): String? {
        return sessionDataSource.getCurrentUserId()
    }

    fun signOut() {
        viewModelScope.launch {
            sessionDataSource.signOutUser()
            navigateToLogin()
        }
    }

    fun home() {
        navController.popBackStack()
    }

    fun navigateToAddArticle() {
        viewModelScope.launch {
            Log.d("ArticleDetailViewModel", "Navigating to Add Article")
            navController.navigate(AppRoutes.ADD_ARTICLE.value)
        }
    }

    fun navigateToChatList() {
        viewModelScope.launch {
            navController.navigate(AppRoutes.CHAT_LIST.value)
        }
    }

    private fun navigateToExchange(userId: String,article: Article, chatId: String ) {
        viewModelScope.launch {
            Log.d(TAG, "Navigating to exchange with user id: $userId")
            navController.navigate("${AppRoutes.CHAT.value}/$userId/${article.id}/$chatId")
        }
    }
    private fun navigateToLogin() {
        navController.navigate(AppRoutes.LOGIN.value) {
            popUpTo(AppRoutes.MAIN.value) {
                inclusive = true
            }
        }
    }
    fun navigateToInventory() {
        viewModelScope.launch {
            Log.d("Navigating to Add Article", "")
            navController.navigate(AppRoutes.INVENTORY.value)
        }
    }
    fun startExchange(userId: String,article: Article) {
        val currentUserUid = getCurrentUserId() ?: run {
            Log.e("ArticleDetailViewModel", "Current user ID is null")
            return
        }
        val imageUrl = article?.carrusel?.get(0)
        val title = article?.title
        val message = "¡Hola! Me interesaría intercambiar este artículo:"

        val chatsRef = FirebaseDatabase.getInstance().getReference("chats")
        val chatId = if (currentUserUid < userId) {
            "$currentUserUid-$userId"
        } else {
            "$userId-$currentUserUid"
        }

        val chatRef = chatsRef.child(chatId)

        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Chat node exists, add new message as subnode
                    val messageId = chatRef.push().key ?: run {
                        Log.e("ArticleDetailViewModel", "Message ID is null")
                        return
                    }
                    val timestamp = ServerValue.TIMESTAMP
                    val messageData = mapOf(
                        "senderId" to currentUserUid,
                        "text" to message,
                        "imageUrl" to imageUrl,
                        "title" to title,
                        "timestamp" to timestamp,
                    )
                    chatRef.child(messageId).setValue(messageData)
                        .addOnSuccessListener {
                            // Notify UI of sent message
                            sentMessage.postValue(message)
                            // Navigate to exchange fragment with chat node
                            navigateToExchange(userId, article, chatId)
                            Log.d("ArticleDetailViewModel", "Message sent successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("ArticleDetailViewModel", "Error sending message", e)
                        }
                } else {
                    // Chat node doesn't exist, create new chat node
                    val messageId = chatRef.push().key ?: run {
                        Log.e("ArticleDetailViewModel", "Message ID is null")
                        return
                    }
                    val timestamp = ServerValue.TIMESTAMP
                    val messageData = mapOf(
                        "senderId" to currentUserUid,
                        "text" to message,
                        "imageUrl" to imageUrl,
                        "title" to title,
                        "timestamp" to timestamp
                    )
                    chatRef.child(messageId).setValue(messageData)
                        .addOnSuccessListener {
                            // Notify UI of sent message
                            sentMessage.postValue(message)
                            // Navigate to exchange fragment with chat node
                            navigateToExchange(userId, article, chatId)
                            Log.d("ArticleDetailViewModel", "Message sent successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("ArticleDetailViewModel", "Error sending message", e)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ArticleDetailViewModel", "Database error", error.toException())
            }
        })
    }
    fun fetchUserName(userId: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                if (name != null) {
                    _otherUserName.postValue(name)
                } else {
                    Log.e("ArticleDetailViewModel", "User name is null")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ArticleDetailViewModel", "Database error", error.toException())
            }
        })
    }
}


