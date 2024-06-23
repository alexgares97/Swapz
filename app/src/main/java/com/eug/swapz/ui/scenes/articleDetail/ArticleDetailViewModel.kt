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
import com.google.firebase.database.DatabaseReference
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
    private val _otherUserPhoto = MutableLiveData<String?>()
    val otherUserPhoto: LiveData<String?> = _otherUserPhoto
    private val _hasStartedExchange = MutableLiveData(false)
    val hasStartedExchange: LiveData<Boolean> = _hasStartedExchange


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
    fun navigateToProfile(userId: String){
        viewModelScope.launch {
            Log.d("Navigating to Profile", "")
            navController.navigate("${AppRoutes.PROFILE.value}/$userId")
        }
    }
    fun navigateToMain(){
        viewModelScope.launch {
            navController.navigate(AppRoutes.MAIN.value)
        }
    }
    fun startExchange(userId: String, article: Article) {
        val currentUserUid = getCurrentUserId() ?: run {
            Log.e("ArticleDetailViewModel", "Current user ID is null")
            return
        }
        val imageUrl = article.carrusel[0]
        val articleId = article.id
        val title = article.title
        val initialMessage = "¡Hola! Me interesaría intercambiar este artículo"
        val additionalMessage = "Este es mi inventario,puedes seleccionar uno o más artículos"

        val chatsRef = FirebaseDatabase.getInstance().getReference("chats")
        val chatId = if (currentUserUid < userId) {
            "$currentUserUid-$userId"
        } else {
            "$userId-$currentUserUid"
        }

        val chatRef = chatsRef.child(chatId)

        // Obtener inventario del usuario que envía la solicitud
        val inventoryRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid).child("inventory")
        inventoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(inventorySnapshot: DataSnapshot) {
                val inventoryItems = mutableListOf<Map<String, String>>()
                for (itemSnapshot in inventorySnapshot.children) {
                    val item = itemSnapshot.getValue(Map::class.java) as Map<String, String>
                    inventoryItems.add(item)
                }

                chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // El nodo de chat existe, agrega nuevos mensajes como subnodos
                            sendMessage(chatRef, currentUserUid, initialMessage, imageUrl, title, false) {
                                // Después de enviar el mensaje inicial, envía el mensaje adicional
                                sendMessage(chatRef, currentUserUid, additionalMessage, null, null, true) {
                                    navigateToExchange(userId, article, chatId)
                                }
                            }
                        } else {
                            // El nodo de chat no existe, crea un nuevo nodo de chat
                            val timestamp = ServerValue.TIMESTAMP
                            val chatData = mapOf(
                                "status" to "requested", // Añadir el nuevo campo de estado en el nivel del chat
                                "articleId" to articleId,
                                "participants" to listOf(currentUserUid, userId),
                                "createdAt" to timestamp
                            )
                            chatRef.setValue(chatData).addOnSuccessListener {
                                sendMessage(chatRef, currentUserUid, initialMessage, imageUrl, title, false) {
                                    // Después de enviar el mensaje inicial, envía el mensaje adicional
                                    sendMessage(chatRef, currentUserUid, additionalMessage, null, null, true) {
                                        navigateToExchange(userId, article, chatId)
                                    }
                                }
                            }.addOnFailureListener { e ->
                                Log.e("ArticleDetailViewModel", "Error creating chat", e)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ArticleDetailViewModel", "Database error", error.toException())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ArticleDetailViewModel", "Database error", error.toException())
            }
        })
        checkIfExchangeStarted(userId, article.id ?: "")
    }

    fun sendMessage(
        chatRef: DatabaseReference,
        senderId: String,
        text: String,
        imageUrl: String?,
        title: String?,
        isInventory: Boolean,
        onSuccess: () -> Unit
    ) {
        val messageId = chatRef.child("messages").push().key ?: run {
            Log.e("ArticleDetailViewModel", "Message ID is null")
            return
        }
        val timestamp = ServerValue.TIMESTAMP
        val messageData = mutableMapOf(
            "senderId" to senderId,
            "text" to text,
            "timestamp" to timestamp,
            "isInventory" to isInventory
        )

        if (imageUrl != null) {
            messageData["imageUrl"] = imageUrl
        }

        if (title != null) {
            messageData["title"] = title
        }

        chatRef.child("messages").child(messageId).setValue(messageData)
            .addOnSuccessListener {
                onSuccess()
                Log.d("ArticleDetailViewModel", "Message sent successfully: $text")
            }
            .addOnFailureListener { e ->
                Log.e("ArticleDetailViewModel", "Error sending message: $text", e)
            }
    }


    fun fetchUserName(userId: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                val photoUrl = snapshot.child("photo").getValue(String::class.java)

                if (name != null && photoUrl != null) {
                    _otherUserName.postValue(name)
                    _otherUserPhoto.postValue(photoUrl)
                } else {
                    Log.e("ArticleDetailViewModel", "User name is null")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ArticleDetailViewModel", "Database error", error.toException())
            }
        })
    }
    fun checkIfExchangeStarted(userId: String, articleId: String) {
        val currentUserUid = getCurrentUserId() ?: return

        val chatsRef = FirebaseDatabase.getInstance().getReference("chats")
        val chatId = if (currentUserUid < userId) {
            "$currentUserUid-$userId"
        } else {
            "$userId-$currentUserUid"
        }

        val chatRef = chatsRef.child(chatId)

        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var exchangeExists = false
                for (childSnapshot in snapshot.children) {
                    val childArticleId = childSnapshot.child("articleId").getValue(String::class.java)
                    if (childArticleId == articleId) {
                        exchangeExists = true
                        break
                    }
                }
                _hasStartedExchange.postValue(exchangeExists)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ArticleDetailViewModel", "Database error", error.toException())
            }
        })
    }
    fun cancelExchange(userId: String, articleId: String) {
        val currentUserUid = getCurrentUserId() ?: return

        val chatsRef = FirebaseDatabase.getInstance().getReference("chats")
        val chatId = if (currentUserUid < userId) {
            "$currentUserUid-$userId"
        } else {
            "$userId-$currentUserUid"
        }

        val chatRef = chatsRef.child(chatId)

        chatRef.orderByChild("articleId").equalTo(articleId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    child.ref.removeValue().addOnSuccessListener {
                        Log.d("ArticleDetailViewModel", "Exchange cancelled successfully")
                        _hasStartedExchange.postValue(false)
                    }.addOnFailureListener { e ->
                        Log.e("ArticleDetailViewModel", "Error cancelling exchange", e)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ArticleDetailViewModel", "Database error", error.toException())
            }
        })
    }
}


