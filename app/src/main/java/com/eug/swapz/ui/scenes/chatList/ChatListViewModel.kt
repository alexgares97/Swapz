package com.eug.swapz.ui.scenes.chatList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Chat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatListViewModel(
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,

    ) : ViewModel() {
    private val _chatListState = MutableStateFlow<List<Chat>>(emptyList())
    val chatListState: StateFlow<List<Chat>> = _chatListState

    private val _chatListLiveData = MutableLiveData<List<Chat>>()
    val chatListLiveData: LiveData<List<Chat>> = _chatListLiveData

    fun fetchChatList() {
        viewModelScope.launch {
            try {
                val chatList = fetchChatListFromFirebase()
                _chatListLiveData.value = chatList
            } catch (e: Exception) {
                // Handle error fetching chat list
            }
        }
    }

    private suspend fun fetchChatListFromFirebase(): List<Chat> = withContext(Dispatchers.IO) {
        val chatList = mutableListOf<Chat>()
        FirebaseDatabase.getInstance().getReference("chats").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (chatSnapshot in snapshot.children) {
                    val chat = chatSnapshot.getValue(Chat::class.java)
                    chat?.let {
                        chatList.add(it)
                    }
                }
                _chatListLiveData.postValue(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
        return@withContext chatList
    }
    fun navigateToChat(chatId: String){

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
