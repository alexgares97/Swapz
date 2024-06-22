import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.style.TextOverflow
import com.eug.swapz.models.Article


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

    DisposableEffect(currentChatId) {
        Log.d("ChatScene", "Listening for chat messages")
        viewModel.updateOtherUserDetails(currentChatId, currentUserUid)
        viewModel.listenForChatMessages(currentChatId)
        viewModel.listenForChatStatus(currentChatId) // Add this line

        onDispose {
            viewModel.cleanupChatMessagesListener()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Encabezado con la foto y nombre del usuario
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
                    .clip(RoundedCornerShape(25.dp)),
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

        // Botón para cancelar el intercambio
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
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Cancelar Intercambio", color = Color.White)
            }
        }

        // Aplicar el filtrado de mensajes antes de renderizar
        val filteredMessages = remember(chatMessages, currentUserUid) {
            chatMessages.filterNot { message ->
                message.isInventory && message.senderId == currentUserUid
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            items(filteredMessages) { message ->
                ChatMessage(message, currentUserUid, viewModel)
            }
        }

        LaunchedEffect(chatMessages.size) {
            // Scroll to the bottom when the messages list changes
            listState.animateScrollToItem(chatMessages.size)
        }

        // Campo de entrada y botón para enviar mensajes
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextField(
                value = messageInput,
                onValueChange = { messageInput = it },
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
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Enviar")
            }
        }

        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )
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
            }
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
            .background(if (isCurrentUser) Color.LightGray else Color(0xFF2F96D8), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(
            text = message.text,
            color = if (isCurrentUser) Color.Black else Color.White,
            textAlign = if (isCurrentUser) TextAlign.End else TextAlign.Start
        )

        // Mostrar título e imagen si existen
        if (!message.imageUrl.isNullOrEmpty() && !message.title.isNullOrEmpty()) {
            Text(
                text = message.title ?: "",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )
            Image(
                painter = rememberAsyncImagePainter(message.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
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
                    .height(200.dp) // Adjust the height to include the "View" button
                    .background(if (isSelected) Color.Green else Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .clickable(
                        enabled = !isSelected // Disable clicking if the article is selected
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
                    text = article.title ?: "Title",
                    style = MaterialTheme.typography.subtitle1.copy(color = Color.White),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                // "View" button outside the gray area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .background(Color.Transparent), // Transparent background for "View"
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
                                // Send a message to the other user
                                val messageText = "He seleccionado este artículo"
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
                    }
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