package com.eug.swapz.ui.scenes.profile

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val articlesDataSource: ArticlesDataSource
) : ViewModel() {
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles
    private val sentMessage = MutableLiveData<String>()
    var node: String = ""
    private val _otherUserPhoto = MutableLiveData<String?>()
    val otherUserPhoto: LiveData<String?> = _otherUserPhoto
    private val _otherUserName = MutableLiveData<String?>()
    val otherUserName: LiveData<String?> = _otherUserName
    private val _hasStartedExchangeMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val hasStartedExchangeMap: StateFlow<Map<String, Boolean>> = _hasStartedExchangeMap.asStateFlow()
    val currentUserUid = getCurrentUserId()

    fun setUserId(userId: String) {
        node = userId
    }
    fun fetch() {
        viewModelScope.launch {
            if (node.isNotEmpty()) {
                val articleList = articlesDataSource.getUserArticles(node)
                _articles.value = articleList
                getUserDetails()
                // No need to call subscribe here, as fetching user articles already does the job
            } else {
                Log.e("InventoryViewModel", "User ID is null or empty")
            }
        }
    }

    fun navigateToDetail(article: Article){
        viewModelScope.launch {
            Log.d("Navigating to Article Detail", ""+article.id)
            navController.navigate(AppRoutes.ARTICLE_DETAIL.value+"/"+article.id)
        }
    }
    fun navigateToAddArticle(){
        viewModelScope.launch {
            Log.d("Navigating to Add Article", "")
            navController.navigate(AppRoutes.ADD_ARTICLE.value)
        }
    }
    fun navigateToInventory() {
        viewModelScope.launch {
            Log.d("Navigating to Add Article", "")
            navController.navigate(AppRoutes.INVENTORY.value)
        }
    }
    fun navigateToChatList(){
        viewModelScope.launch {
            navController.navigate(AppRoutes.CHAT_LIST.value)
        }
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
    fun getCurrentUserId(): String? {
        return sessionDataSource.getCurrentUserId()
    }
    private fun navigateToExchange(userId: String,article: Article, chatId: String ) {
        viewModelScope.launch {
            Log.d(ContentValues.TAG, "Navigating to exchange with user id: $userId")
            navController.navigate("${AppRoutes.CHAT.value}/$userId/${article.id}/$chatId")
        }
    }
    fun startExchange(userId: String,article: Article) {
        val currentUserUid = getCurrentUserId() ?: run {
            Log.e("ArticleDetailViewModel", "Current user ID is null")
            return
        }
        val imageUrl = article.carrusel[0]
        val articleId = article.id
        val title = article.title
        val message = "¡Hola! Me interesaría intercambiar este artículo"
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
                        "timestamp" to timestamp,
                        "articleId" to articleId
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
    fun checkIfExchangeStarted(articleId: String) {
        val currentUserUid = getCurrentUserId() ?: return
        val chatsRef = FirebaseDatabase.getInstance().getReference("chats")
        chatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var exchangeExists = false
                val currentStatusMap = _hasStartedExchangeMap.value.toMutableMap()
                for (childSnapshot in snapshot.children) {
                    val chatId = childSnapshot.key ?: ""
                    if (chatId.contains(currentUserUid)) {
                        val participants = childSnapshot.child("participants").children.mapNotNull {
                            it.getValue(String::class.java)
                        }
                        if (participants.contains(currentUserUid)) {
                            val childArticleId =
                                childSnapshot.child("articleId").getValue(String::class.java)
                            if (childArticleId == articleId) {
                                exchangeExists = true
                                break
                            }
                        }
                    }
                }
                viewModelScope.launch {
                    _hasStartedExchangeMap.emit(currentStatusMap + (articleId to exchangeExists))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileViewModel", "Database error", error.toException())
            }
        })
    }

    private fun getUserDetails() {
        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(node)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                val photoUrl = snapshot.child("photo").getValue(String::class.java)

                if (name != null && photoUrl != null) {
                    viewModelScope.launch {
                        _otherUserPhoto.value = photoUrl
                        _otherUserName.value = name
                    }
                } else {
                    Log.e("ArticleDetailViewModel", "User name is null")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ArticleDetailViewModel", "Database error", error.toException())
            }
        })
    }
    fun navigateToChat(chatId: String, otherUserId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Navigating to exchange with user id: $otherUserId")
            navController.navigate("${AppRoutes.CHAT.value}/$otherUserId/$chatId")
        }
    }
    fun getChatId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) {
            "$userId1-$userId2"
        } else {
            "$userId2-$userId1"
        }
    }
    fun navigateToMain(){
        viewModelScope.launch {
            navController.navigate(AppRoutes.MAIN.value)
        }
    }
}