package com.eug.swapz.ui.scenes.articleDetail
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import com.eug.swapz.ui.scenes.chat.ChatViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.net.URLEncoder


class ArticleDetailViewModel(
    private val navController: NavController,
    private val context: Context,
    internal val article: Article?,
    private val sessionDataSource: SessionDataSource,
) : ViewModel() {

    private val sentMessage = MutableLiveData<String>()

    private fun getCurrentUserId(): String? {
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

   private fun navigateToExchange(article: Article, chatId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Navigating to exchange with chat node: $chatId")
            navController.navigate("${AppRoutes.CHAT.value}/$chatId/${article.id}")
        }
    }
    private fun navigateToLogin() {
        navController.navigate(AppRoutes.LOGIN.value) {
            popUpTo(AppRoutes.MAIN.value) {
                inclusive = true
            }
        }
    }
    fun startExchange(article: Article) {
        val currentUserUid = getCurrentUserId() ?: run {
            Log.e("ArticleDetailViewModel", "Current user ID is null")
            return
        }

        val otherUserId = article.userId ?: run {
            Log.e("ArticleDetailViewModel", "Other user ID is null")
            return
        }

        val message = "¡Hola! Me interesaría intercambiar este artículo:\n" +
                "${article.title}, ${article.carrusel?.get(0)}"

        val chatsRef = FirebaseDatabase.getInstance().getReference("chats")
        val chatId = if (currentUserUid < otherUserId) {
            "$currentUserUid-$otherUserId"  
        } else {
            "$otherUserId-$currentUserUid"
        }

        val chatRef = chatsRef.child(chatId)

        // Check if the chat node already exists
        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Chat node already exists, add the text directly
                    val messageId = chatRef.push().key ?: run {
                        Log.e("ArticleDetailViewModel", "Message ID is null")
                        return
                    }
                    val messageData = mapOf(
                        "text" to message
                    )
                    chatRef.child(messageId).setValue(messageData)
                        .addOnSuccessListener {
                            // Notify UI of sent message
                            sentMessage.postValue(message)
                            // Get the chat node
                            // Navigate to exchange fragment with chat node
                            navigateToExchange(article, chatId)
                            Log.d("ArticleDetailViewModel", "Message sent successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("ArticleDetailViewModel", "Error sending message", e)
                        }
                } else {
                    // Chat node doesn't exist, create the chat node
                    val messageData = mapOf(
                        "senderId" to currentUserUid,
                        "text" to message
                    )
                    chatRef.setValue(messageData)
                        .addOnSuccessListener {
                            // Notify UI of sent message
                            sentMessage.postValue(message)
                            // Get the chat node
                            // Navigate to exchange fragment with chat node
                            navigateToExchange(article, chatId)
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



}
