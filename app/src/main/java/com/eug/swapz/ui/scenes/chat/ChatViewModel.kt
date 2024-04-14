package com.eug.swapz.ui.scenes.chat


import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class ChatViewModel(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,


    ) : ViewModel() {
    private val _sendMessageResult = MutableLiveData<Result<Unit>>()
    val sendMessageResult: LiveData<Result<Unit>> = _sendMessageResult

    private val chatService = ChatService()
    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                chatService.sendMessage(message)
                _sendMessageResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _sendMessageResult.value = Result.failure(e)
                Log.e("ChatViewModel", "Error sending message", e)
            }
        }
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