package com.eug.swapz.ui.scenes.chat


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
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles
    private val _status = MutableLiveData<String>()
    var status: LiveData<String> = _status
    private val _requestorId = MutableLiveData<String>()
    var requestorId: LiveData<String> = _requestorId
    private val _otherUserId = MutableLiveData<String?>()
    val otherUserId: LiveData<String?> = _otherUserId

    fun listenForChatMessages(currentChatId: String) {
        Log.d("ChatViewModel", "Listening to chat ID: $currentChatId")

        val chatQuery = databaseReference.child(currentChatId).child("messages")
        val chatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (messageSnapshot in snapshot.children) {
                    val senderId = messageSnapshot.child("senderId").getValue(String::class.java) ?: ""
                    val text = messageSnapshot.child("text").getValue(String::class.java) ?: ""
                    val imageUrl = messageSnapshot.child("imageUrl").getValue(String::class.java)
                    val title = messageSnapshot.child("title").getValue(String::class.java)
                    val timestamp = messageSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                    val isInventory = messageSnapshot.child("isInventory").getValue(Boolean::class.java) ?: false


                    val chatMessage = ChatMessage(senderId, text, imageUrl, title, timestamp, isInventory)
                    messages.add(chatMessage)
                }
                _messages.postValue(messages) // Use postValue for thread safety
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatViewModel", "Database error: ${error.message}")
            }
        }

        chatQuery.addValueEventListener(chatListener)

        // Save references to remove the listener later if needed
        this.chatQuery = chatQuery
        this.chatListener = chatListener
    }

    // Función para obtener los artículos de un usuario
    fun getUserArticles(userId: String) {
        viewModelScope.launch {
            if (userId.isNotEmpty()) {
                val articleList = articlesDataSource.getUserArticles(userId)
                _articles.value = articleList
                // No need to call subscribe here, as fetching user articles already does the job
            } else {
                Log.e("InventoryViewModel", "User ID is null or empty")
            }
        }
    }



    fun sendMessage(message: String?) {
        val currentUserUid = sessionDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            try {
                // Reference to the messages node
                val messagesReference = databaseReference.child(node).child("messages")
                // Generate a new message ID
                val messageId = messagesReference.push().key ?: return@launch

                // Prepare the message data
                val messageData = mapOf(
                    "senderId" to currentUserUid,
                    "text" to message,
                    "timestamp" to ServerValue.TIMESTAMP,
                    "isInventory" to false
                )

                // Set the value for the new message
                messagesReference.child(messageId).setValue(messageData)
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
    fun sendFinalMessage(message: String?) {
        val currentUserUid = sessionDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            try {
                // Reference to the messages node
                val messagesReference = databaseReference.child(node).child("messages")
                // Generate a new message ID
                val messageId = messagesReference.push().key ?: return@launch

                // Prepare the message data
                val messageData = mapOf(
                    "senderId" to currentUserUid,
                    "text" to message,
                    "timestamp" to ServerValue.TIMESTAMP,
                    "isInventory" to true
                )
                // Set the value for the new message
                messagesReference.child(messageId).setValue(messageData)
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
    fun sendSelectedArticleMessage(
        senderId: String,
        text: String,
        imageUrl: String?,
        title: String?,
    ) {
        viewModelScope.launch {
            try {
                val messagesReference = databaseReference.child(node).child("messages")
                val messageData = mapOf(
                    "senderId" to senderId,
                    "text" to text,
                    "imageUrl" to imageUrl,
                    "title" to title
                )
                val messageId = messagesReference.push().key ?: return@launch
                messagesReference.child(messageId).setValue(messageData)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                        Log.e("ChatViewModel", "Error sending message", e)

                    }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Exception occurred while sending message", e)
            }
        }
    }
    fun sendMessageRejected(senderId: String) {
        // Mensajes de rechazo
        val message1 = "¡El intercambio ha sido rechazado! Escoge mejor los artículos"
        val message2 = "¡Has rechazado el intercambio!"

        // Referencia a la base de datos para el inventario del usuario
        val inventoryRef = FirebaseDatabase.getInstance().getReference("users")
            .child(senderId).child("inventory")

        inventoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(inventorySnapshot: DataSnapshot) {
                val inventoryItems = mutableListOf<Map<String, String>>()

                // Iterar sobre los hijos del snapshot para obtener los elementos de inventario
                for (itemSnapshot in inventorySnapshot.children) {
                    val item = itemSnapshot.getValue(Map::class.java) as? Map<String, String>
                    item?.let { inventoryItems.add(it) }
                }

                // Referencia a la base de datos para mensajes
                val messagesReference = databaseReference.child(node).child("messages")

                // Enviar el primer mensaje
                val messageId1 = messagesReference.push().key ?: return
                val messageData1 = mapOf(
                    "senderId" to senderId,
                    "text" to message1,
                    "isInventory" to true
                )

                messagesReference.child(messageId1).setValue(messageData1)
                    .addOnSuccessListener {
                        Log.d("ChatViewModel", "Primer mensaje de rechazo enviado correctamente")

                        // Enviar el segundo mensaje después de que el primero se haya enviado
                        val messageId2 = messagesReference.push().key ?: return@addOnSuccessListener
                        val messageData2 = mapOf(
                            "senderId" to senderId,
                            "text" to message2,
                            "isInventory" to false
                        )

                        messagesReference.child(messageId2).setValue(messageData2)
                            .addOnSuccessListener {
                                Log.d("ChatViewModel", "Segundo mensaje de rechazo enviado correctamente")
                            }
                            .addOnFailureListener { e ->
                                Log.e("ChatViewModel", "Error al enviar el segundo mensaje de rechazo", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ChatViewModel", "Error al enviar el primer mensaje de rechazo", e)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
                Log.e("SendMessageRejected", "Database error", error.toException())
            }
        })
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
                    val userId = snapshot.key

                    // Update the LiveData with the other user's photo URL
                    _otherUserPhotoUrl.value = photoUrl
                    _otherUserName.value = name
                    _otherUserId.value = userId
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

    fun listenForChatStatus(chatId: String) {
        val chatRef = databaseReference.child(chatId)
        chatRef.child("status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _status.value = snapshot.getValue(String::class.java) ?: ""
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatViewModel", "Error listening for chat status", error.toException())
            }
        })
    }
    fun getRequestorId(chatId: String) {
        val chatRef = databaseReference.child(chatId)
        chatRef.child("participants").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val participants = snapshot.children.toList()
                if (participants.isNotEmpty()) {
                    val secondParticipant = participants[0]
                    val requestorIdValue = secondParticipant.getValue(String::class.java) // Obtiene el valor del segundo participante
                    _requestorId.value = requestorIdValue?:""
                } else {
                    Log.e("ChatViewModel", "No hay suficientes participantes")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatViewModel", "Error listening for chat status", error.toException())
            }
        })
    }


    fun updateChatStatus(chatId: String, status: String, selectedArticleId: String? = null) {
        val chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId)
        val updates = mapOf(
            "status" to status,
            "selectedArticleId" to selectedArticleId
        )

        chatRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Chat status updated to $status")
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error updating chat status", e)
            }
    }

    fun confirmExchange(chatId: String, status: String) {
        val chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId)
        val updates = mapOf(
            "status" to status
        )

        chatRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Chat status updated to $status")
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error updating chat status", e)
            }
    }

    fun navigateToChatList() {
        viewModelScope.launch {
            navController.navigate(AppRoutes.CHAT_LIST.value)
        }
    }
    fun navigateToProfile(userId: String){
        viewModelScope.launch {
            Log.d("Navigating to Profile", "")
            navController.navigate("${AppRoutes.PROFILE.value}/$userId")
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
    fun navigateToDetail(article: Article) {
        viewModelScope.launch {
            Log.d("Navigating to article detail", "" + article.id)
            navController.navigate(AppRoutes.ARTICLE_DETAIL.value + "/" + article.id)
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
}