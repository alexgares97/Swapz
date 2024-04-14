package com.eug.swapz.ui.scenes.articleDetail
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch


class ArticleDetailViewModel (
    private val navController: NavController,
    private val context: Context,
    internal val article: Article?,
    private val sessionDataSource: SessionDataSource,

    ) : ViewModel() {


    // Function to update the image URI
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

    fun home() {
        navController.popBackStack()
    }
    fun navigateToAddArticle(){
        viewModelScope.launch {
            Log.d("Navigating to Add Article", "")
            navController.navigate(AppRoutes.ADD_ARTICLE.value)
        }
    }
    fun navigateToChatList(){
        viewModelScope.launch {
            navController.navigate(AppRoutes.CHAT_LIST.value)
        }
    }
    fun navigateToChat(){
        viewModelScope.launch{
            navController.navigate(AppRoutes.CHAT.value)
        }
    }
    private fun getCurrentUserId(): String? {
        return sessionDataSource.getCurrentUserId()
    }
    fun startChat(userId: String) {
        // Get the current user's ID
        val currentUserUid = getCurrentUserId() ?: return // Return early if current user ID is null
        // Construct the predefined message
        val message = "¡Hola! Me interesaría intercambiar este artículo:\n" +
                "${article?.title}, ${article?.carrusel?.get(0)}"
        // Construct the chat reference
        val chatRef = FirebaseDatabase.getInstance().getReference("chats")
        val currentUserChatRef = chatRef.child(currentUserUid).child(userId)
        // Push the message to the chat reference
        val messageId = currentUserChatRef.push().key
        if (messageId != null) {
            currentUserChatRef.child(messageId).setValue(message)
                .addOnSuccessListener {
                    navigateToChat()
                    Log.d(TAG, "Message sent successfully")
                    // Optionally, navigate to the chat screen or perform any other action
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error sending message", e)
                }
        }
    }


}