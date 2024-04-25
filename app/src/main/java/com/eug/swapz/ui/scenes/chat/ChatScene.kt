import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.eug.swapz.models.ChatMessage
import com.eug.swapz.ui.scenes.chat.ChatViewModel

// Data class representing a chat message
data class ChatMessage(val sender: String, val text: String, val isSentByUser: Boolean)

@Composable
fun ChatScene(viewModel: ChatViewModel) {
    var messageInput by remember { mutableStateOf("") }
    val chatMessages by viewModel.messages.observeAsState(emptyList())
    val message by viewModel.message.observeAsState("")
    val currentChatId by viewModel.currentChatId.observeAsState("")
    val article by viewModel.article.observeAsState()
    val imageUrl = article?.carrusel?.get(0)
    val title = article?.title

    // Extract the image URL from the article if available

    DisposableEffect(Unit) {
        viewModel.listenForChatMessages(viewModel.currentChatId.value ?: "")
        onDispose {
            // Cleanup code if needed
        }
    }


        Column(modifier = Modifier.fillMaxSize()) {
            // Chat messages
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(chatMessages) { message ->
                    ChatMessageItem(message, imageUrl, title)
                }
            }
            Spacer(modifier = Modifier.weight(1f))


            // Message input field and send button
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
                        messageInput = "" // Clear input field after sending message
                    },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text("Send")
                }
            }

            // Display the message sent
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1
            )
        }
    }

@Composable
fun ChatMessageItem(message: ChatMessage, imageUrl: String?, title:String?) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.isSentByUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByUser) Color.LightGray else MaterialTheme.colors.primary,
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
            ) {
                // Display the image if imageUrl is not null
                if (!imageUrl.isNullOrEmpty() && !title.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp) // Adjust size as needed
                            .align(Alignment.CenterHorizontally) // Center the image horizontally
                    )
                    Text(
                        text = title ?: "", // Use title if not null, otherwise empty string
                        style = TextStyle( // Define text style for the title
                            fontWeight = FontWeight.Bold, // Example: bold
                            fontSize = 18.sp, // Example: 18sp
                            color = Color.White // Example: white color
                        )
                    )
                } else {
                    // Handle the case where imageUrl is null or empty
                    // For example, you can display a placeholder image or hide the Image composable
                    Log.d("ChatScene", "imageUrl: $imageUrl")
                }


            // Display the text message
                Text(
                    text = message.text,
                    color = if (message.isSentByUser) Color.Black else Color.White,
                    textAlign = if (message.isSentByUser) TextAlign.End else TextAlign.Start
                )
            }
        }
    }
}

