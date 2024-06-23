import android.animation.Animator
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.models.ChatMessage
import com.eug.swapz.ui.scenes.chat.ChatViewModel
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.style.TextOverflow
import com.eug.swapz.models.Article
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.eug.swapz.R

// Data class representing a chat message

@Composable
fun ChatScene(viewModel: ChatViewModel) {
    var messageInput by remember { mutableStateOf("") }
    val chatMessages by viewModel.messages.observeAsState(emptyList())
    val message by viewModel.message.observeAsState("")
    val currentChatId = viewModel.node
    val otherUserPhotoUrl by viewModel.otherUserPhotoUrl.observeAsState("")
    val otherUserName by viewModel.otherUserName.observeAsState("")
    val currentUserUid = viewModel.getCurrentUserId() ?: return
    val listState = rememberLazyListState()
    val status by viewModel.status.observeAsState("")
    var showConfirmDialog by remember { mutableStateOf(false) }
    val requestor by viewModel.requestorId.observeAsState("")
    var showConfetti by remember { mutableStateOf(false) }
    var showFinalizeDialog by remember { mutableStateOf(false) }
    var showFinalizedMessage by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    val otherUserUid by viewModel.otherUserId.observeAsState("")

    DisposableEffect(currentChatId) {
        Log.d("ChatScene", "Listening for chat messages and status")
        viewModel.updateOtherUserDetails(currentChatId, currentUserUid)
        viewModel.listenForChatMessages(currentChatId)
        viewModel.listenForChatStatus(currentChatId)
        viewModel.getRequestorId(currentChatId)
        onDispose {
            viewModel.cleanupChatMessagesListener()
            //viewModel.cleanupChatStatusListener()
        }
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF5F5F5))) {
        // Header with user photo and name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackIcon(onClick = { viewModel.goBack() })

            Image(
                painter = rememberAsyncImagePainter(otherUserPhotoUrl),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White, RoundedCornerShape(25.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(25.dp))
                    .clickable{viewModel.navigateToProfile(otherUserUid?:"")},
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = otherUserName ?: "",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
        }

        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Show Confirm button if the status is selected
            if (status == "selected" &&  currentUserUid == requestor ) {
                viewModel.listenForChatStatus(currentChatId)
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        showConfirmDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(50.dp)
                ) {
                    Text("Confirmar", color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        showRejectDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF5252)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(50.dp)
                ) {
                    Text("Rechazar", color = Color.White)
                }
            }
            else if (status == "confirmed" && currentUserUid != requestor){
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        showFinalizeDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(50.dp)
                ) {
                    Text("Confirmar", color = Color.White)
                }
            }
        }

        // Filter messages before rendering
        val filteredMessages = remember(chatMessages, currentUserUid) {
            chatMessages.filterNot { message ->
                message.isInventory && message.senderId == currentUserUid
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)

        ) {
            items(filteredMessages) { message ->
                ChatMessage(message, currentUserUid, viewModel)
            }
        }

        LaunchedEffect(chatMessages.size) {
            // Scroll to the bottom when the messages list changes
            listState.animateScrollToItem(chatMessages.size)
        }

        // Input field and send button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    cursorColor = Color.Gray
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Button(
                onClick = {
                    viewModel.sendMessage(messageInput)
                    viewModel.listenForChatMessages(currentChatId)
                    messageInput = "" // Clear input field after sending message
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .height(50.dp)
                    .padding(end = 4.dp)
            ) {
                Text("Enviar", color = Color.White)
            }
        }

        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )
    }
    if (showConfirmDialog){
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {Text(text = "Confirmar Intercambio")},
            text = { Text(text = "¿Estás seguro de que deseas confirmar el intercambio?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateChatStatus(currentChatId, "confirmed")
                        showConfirmDialog = false
                        }
                ){
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White
        )
    }
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text(text = "Rechazar Intercambio") },
            text = { Text(text = "¿Estás seguro de que deseas rechazar el intercambio? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateChatStatus(currentChatId, "rejected")
                        viewModel.sendMessageRejected(currentUserUid)
                        showRejectDialog = false
                    }
                ) {
                    Text("Rechazar")
                }
            },
            dismissButton = {
                Button(onClick = { showRejectDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White
        )
    }
    if (showFinalizeDialog){
        AlertDialog(
            onDismissRequest = { showFinalizeDialog = false },
            title = {Text(text = "Confirmar Intercambio")},
            text = { Text(text = "¿Estás seguro de que deseas confirmar el intercambio? Si aceptas se eliminaran tus artículos") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateChatStatus(currentChatId, "finalized")
                        showFinalizeDialog = false
                        showFinalizedMessage = true
                    }
                ){
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showFinalizeDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White
        )
    }
    if (showConfetti) {
        ConfettiAnimation(
            animationResource = R.raw.confetti, // Asegúrate de tener este archivo en res/raw
            repeatCount = 1
        ) {
            showConfetti = false // Detener la animación después de que termine
        }
    }

    // Dialog to show that the transaction has been finalized
    if (showFinalizedMessage) {
        showConfetti = true
        AlertDialog(
            onDismissRequest = { showFinalizedMessage = false },
            title = { Text(text = "Transacción Finalizada") },
            text = { Text(text = "La transacción ha sido confirmada por ambas partes y está finalizada.") },
            confirmButton = {
                Button(onClick = {
                    showFinalizedMessage = false
                    showConfetti = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = { showFinalizedMessage = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White
        )
    }
}
@Composable
fun ChatMessage(message: ChatMessage, currentUserUid: String, viewModel: ChatViewModel) {
    val isCurrentUser = message.senderId == currentUserUid

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                if (isCurrentUser) Color(0xFFE0E0E0) else Color(0xFFBBDEFB),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = message.text,
            color = if (isCurrentUser) Color.Black else Color.White,
            textAlign = if (isCurrentUser) TextAlign.End else TextAlign.Start
        )

        if (!message.imageUrl.isNullOrEmpty() && !message.title.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message.title ?: "",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isCurrentUser) Color.Black else Color.White,
                    textAlign = TextAlign.Center
                )
            )
            Image(
                painter = rememberAsyncImagePainter(message.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Mostrar inventario si `isInventory` es true y no es el mensaje del usuario actual
        if (message.isInventory && !isCurrentUser) {
            LaunchedEffect(message.senderId) {
                viewModel.getUserArticles(message.senderId)
            }

            val articles by viewModel.articles.observeAsState(emptyList())
            if (articles.isNotEmpty()) {
                InventoryCarousel(inventory = articles, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun InventoryCarousel(inventory: List<Article>, viewModel: ChatViewModel) {
    var selectedArticleIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedArticleToConfirm by remember { mutableStateOf<Article?>(null) }
    val status by viewModel.status.observeAsState("")
    val currentChatId = viewModel.node
    val currentUserUid = viewModel.getCurrentUserId() ?: return

    viewModel.listenForChatMessages(currentChatId)
    viewModel.listenForChatStatus(currentChatId)
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp) // Provide space between items
    ) {
        items(inventory) { article ->
            val isSelected = selectedArticleIds.contains(article.id) || status == "selected" || status == "confirmed" || status == "finalized"
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .height(220.dp)
                    .background(if (isSelected) Color.Green else Color.Gray, RoundedCornerShape(12.dp))
                    .padding(20.dp)
                    .clickable(
                        enabled = !isSelected
                    ) {
                        if (status == "requested" || status == "rejected") {
                            selectedArticleToConfirm = article
                            showConfirmationDialog = true
                        }
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(article.carrusel[0]),
                    contentDescription = "Article Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.subtitle1.copy(color = Color.White),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .background(Color.Transparent),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Ver",
                        modifier = Modifier
                            .clickable {
                                viewModel.navigateToDetail(article)
                            }
                            .padding(4.dp),
                        style = MaterialTheme.typography.button.copy(
                            color = Color.Blue,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
    if (showConfirmationDialog && selectedArticleToConfirm != null) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(text = "Confirmar Selección") },
            text = { Text(text = "¿Deseas seleccionar el artículo ${selectedArticleToConfirm?.title}? Puedes seleccionar más de un artículo.") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedArticleToConfirm?.let { article ->
                            selectedArticleIds = selectedArticleIds.plus(article.id?:"")
                            viewModel.updateChatStatus(
                                chatId = currentChatId,
                                status = "selected",
                                selectedArticleId = article.id
                            )
                            val messageText = "Ha sido seleccionado este artículo"
                            val imageUrl = article.carrusel[0]
                            val title = article.title
                            viewModel.sendSelectedArticleMessage(
                                senderId = currentUserUid,
                                text = messageText,
                                imageUrl = imageUrl,
                                title = title
                            )
                        }
                        showConfirmationDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmationDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White
        )
    }
}
@Composable
fun BackIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
}
@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    animationResource: Int,
    repeatCount: Int = LottieDrawable.INFINITE,
    onAnimationEnd: () -> Unit = {}
) {
    var isAnimationFinished by remember { mutableStateOf(false) }

    AndroidView(
        factory = { context ->
            LottieAnimationView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setAnimation(animationResource)
                this.repeatCount = repeatCount
                playAnimation()
                addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        // Implementa lo que deseas hacer al inicio de la animación
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isAnimationFinished = true
                        onAnimationEnd()
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        // Implementa lo que deseas hacer si la animación se cancela
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                        // Implementa lo que deseas hacer si la animación se repite
                    }
                })
            }
        },
        modifier = modifier,
        update = { view ->
            if (isAnimationFinished) {
                view.pauseAnimation()
            }
        }
    )
}