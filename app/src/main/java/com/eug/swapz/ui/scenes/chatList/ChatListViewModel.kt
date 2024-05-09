package com.eug.swapz.ui.scenes.chatList

import android.content.ContentValues.TAG
import android.util.Log
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
    private val _chatList = MutableLiveData<List<Chat>>()
    val chatList: LiveData<List<Chat>> = _chatList


    private val userId = sessionDataSource.getCurrentUserId()
    fun fetchChatList() {
        userId?.let { userId ->
            fetchChatListFromFirebase(userId) { chatList ->
                _chatList.postValue(chatList)
                Log.d(TAG, "fetchChatList: $chatList")
            }
        }
    }

    private fun fetchChatListFromFirebase(userId: String, callback: (List<Chat>) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("chats")
        val chatQuery = databaseReference.orderByKey().startAt("$userId-").endAt("$userId-\uf8ff")
        Log.d(TAG, "fetchChatListFromFirebase: $chatQuery")

        val chatList = mutableListOf<Chat>()

        chatQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatId = snapshot.key ?: "" // Assuming the chat ID is the key of the snapshot
                for (chatSnapshot in snapshot.children) {
                    // Get the last child node of each chatSnapshot
                    val lastMessageSnapshot = chatSnapshot.children.lastOrNull()

                    lastMessageSnapshot?.let { messageSnapshot ->
                        val text = messageSnapshot.child("text").getValue(String::class.java)
                        Log.d(TAG, "onDataChange: $text")

                        if (text != null) {
                            val otherUserId = chatSnapshot.key?.split("-")?.find { it != userId }
                            if (otherUserId != null) {
                                val usersReference = FirebaseDatabase.getInstance().getReference("users").child(otherUserId)
                                usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(userSnapshot: DataSnapshot) {
                                        val name = userSnapshot.child("name").getValue(String::class.java)
                                        val photoUrl = userSnapshot.child("photo").getValue(String::class.java)
                                        if (name != null && photoUrl != null) {
                                            val chat = Chat(name, text, photoUrl)
                                            chatList.add(chat)
                                        }
                                        // Check if all chat messages are processed
                                        if (chatList.size.toLong() == snapshot.childrenCount) {
                                            callback(chatList)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle onCancelled event
                                    }
                                })
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
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
