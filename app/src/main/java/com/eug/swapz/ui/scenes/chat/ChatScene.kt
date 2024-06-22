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
    var showCancelDialog by remember { mutableStateOf(false) }
    val status by viewModel.status.observeAsState("")
    var showConfirmDialog by remember { mutableStateOf(false) }
    val requestor by viewModel.requestorId.observeAsState("")
    var showConfetti by remember { mutableStateOf(false) }



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
                    .border(2.dp, Color.Gray, RoundedCornerShape(25.dp)),
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

        // Buttons for cancel and confirm
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    showCancelDialog = true
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .padding(8.dp)
                    .height(50.dp)
            ) {
                Text("Cancelar Intercambio", color = Color.White)
            }

            // Show Confirm button if the status is selected
            if (status == "selected" &&  currentUserUid == requestor ) {
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
    ConfirmActionDialog(
        title = "Confirmar Acción",
        message = "¿Estás seguro de que deseas confirmar el intercambio?",
        onConfirm = {
            viewModel.confirmExchange(currentChatId, "confirmed")
            showConfetti = true // Mostrar la animación de confeti
        },
        onDismiss = {
            showConfirmDialog = false
        },
        isVisible = showConfirmDialog
    )

    if (showConfetti) {
        ConfettiAnimation(
            animationResource = R.raw.confetti, // Asegúrate de tener este archivo en res/raw
            repeatCount = 1
        ) {
            showConfetti = false // Detener la animación después de que termine
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(text = "Confirmar Cancelación") },
            text = { Text(text = "¿Estás seguro de que deseas cancelar el intercambio? Se eliminará la conversación.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelExchange(currentChatId)
                        showCancelDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showCancelDialog = false }) {
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
    var selectedArticleId by remember { mutableStateOf<String?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val status by viewModel.status.observeAsState("")
    val currentChatId = viewModel.node
    val currentUserUid = viewModel.getCurrentUserId() ?: return

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(inventory) { article ->
            val isSelected = selectedArticleId == article.id || status == "selected"
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .height(220.dp)
                    .background(if (isSelected) Color.Green else Color.Gray, RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .clickable(
                        enabled = !isSelected
                    ) {
                        showConfirmationDialog = status != "selected"
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
                    text = article.title ?: "Título",
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
            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    title = { Text(text = "Confirmar Selección") },
                    text = { Text(text = "¿Deseas seleccionar el artículo ${article.title}? No podrás volver a seleccionar otro") },
                    confirmButton = {
                        Button(
                            onClick = {
                                selectedArticleId = article.id
                                viewModel.updateChatStatus(
                                    chatId = currentChatId,
                                    status = "selected",
                                    selectedArticleId = article.id
                                )
                                val messageText = "El usuario ha seleccionado este artículo"
                                val imageUrl = article.carrusel[0]
                                val title = article.title
                                viewModel.sendSelectedArticleMessage(
                                    senderId = currentUserUid,
                                    text = messageText,
                                    imageUrl = imageUrl,
                                    title = title
                                )
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
fun ConfirmActionDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean,
    confirmButtonText: String = "Confirmar",
    dismissButtonText: String = "Cancelar"
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = title) },
            text = { Text(text = message) },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(50),
                ) {
                    Text(confirmButtonText, color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF5252)),
                    shape = RoundedCornerShape(50),
                ) {
                    Text(dismissButtonText, color = Color.White)
                }
            },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White
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

