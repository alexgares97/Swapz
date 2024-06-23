import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.R
import com.eug.swapz.models.Chat
import com.eug.swapz.ui.scenes.chatList.ChatListViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
@Composable
fun ChatList(viewModel: ChatListViewModel) {
    val chatListState = viewModel.chatList.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        // Llama al método de actualización de la lista de chats en el ViewModel
        viewModel.updateChatList()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Mensajes",
            style = MaterialTheme.typography.h4.copy(
                color = Color(0xFF6200EA),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            ),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            if (chatListState.value.isEmpty()) {
                NoMessagesView(viewModel)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatListState.value) { chat ->
                        ChatListItem(chat = chat, viewModel = viewModel) {
                            viewModel.navigateToChat(chat.id, chat.otherUserId)
                        }
                    }
                }
            }
        }
        BottomNavigationBar(viewModel)
    }
}
@Composable
fun NoMessagesView(viewModel: ChatListViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.drawable.sobre_azul),
            contentDescription = "No Messages",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¡Todavía no tienes mensajes!",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿Ya tienes artículos subidos?",
            style = TextStyle(fontSize = 16.sp),
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.navigateToMain()},
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2F96D8))
        ) {
            Text("Buscar artículos", color = Color.White)
        }
    }
}

@Composable
fun BottomNavigationBar(viewModel: ChatListViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp) // Ajuste de altura para acomodar etiquetas y dar más espacio
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2F96D8), Color(0xFF1A73E8))
                )
            )
            .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavigationItem(
            icon = Icons.Filled.Home,
            label = "Inicio",
            onClick = { viewModel.navigateToMain() },
            iconSize = 24.dp
        )
        BottomNavigationItem(
            icon = Icons.AutoMirrored.Filled.Chat,
            label = "Chats",
            onClick = { viewModel.navigateToChatList() },
            iconSize = 24.dp
        )
        BottomNavigationItem(
            icon = Icons.Filled.AddBox,
            label = "Subir",
            onClick = { viewModel.navigateToAddArticle() },
            iconSize = 29.dp // Tamaño incrementado del ícono de "Subir"
        )
        BottomNavigationItem(
            icon = Icons.Filled.Person,
            label = "Inventario",
            onClick = { viewModel.navigateToInventory() },
            iconSize = 24.dp
        )
        BottomNavigationItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            label = "Salir",
            onClick = { viewModel.signOut() },
            iconSize = 24.dp
        )
    }
}

@Composable
fun BottomNavigationItem(icon: ImageVector, label: String, onClick: () -> Unit, iconSize: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.height(0.dp)) // Pequeño ajuste de espaciado
        Text(
            text = label,
            style = MaterialTheme.typography.caption.copy(
                fontSize = 10.sp,
                color = Color.White
            )
        )
    }
}



@Composable
fun ChatListItem(chat: Chat, viewModel: ChatListViewModel, onClick: () -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    offsetX = (offsetX + dragAmount).coerceIn(-300f, 0f)
                }
            }
    ) {
        // Background with delete icon on the right
        Box(
            modifier = Modifier
                .matchParentSize() // This ensures the Box takes the size of its parent, matching the height of the chat item
                .background(if (offsetX < -10f) Color.Red else Color.Transparent)
                .padding(start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (offsetX < -10f) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .alpha((-offsetX / 300f).coerceIn(0f, 1f))
                        .clickable {
                            showCancelDialog = true
                        }
                )
            }
        }

        // Chat item content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) } // Adjust to let the delete icon show up
                .clickable(onClick = onClick),
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(chat.photoUrl),
                    contentDescription = "User Icon",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(25.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = chat.name, style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = chat.text, style = MaterialTheme.typography.body2, color = Color.Gray)
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(text = "Confirmar Cancelación") },
            text = { Text(text = "¿Estás seguro de que deseas cancelar el chat?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelExchange(chat.id)
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