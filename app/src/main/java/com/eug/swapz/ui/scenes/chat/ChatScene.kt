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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eug.swapz.ui.scenes.chat.ChatViewModel

// Data class representing a chat message
data class ChatMessage(val sender: String, val text: String)

@Composable
fun ChatScene(viewModel: ChatViewModel) {
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) } // Initialize with an empty list
    var messageInput by remember { mutableStateOf("") }


    Column(modifier = Modifier.fillMaxSize()) {
        // Chat messages
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(messages) { message ->
                ChatMessageItem(message)
            }
        }

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
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { viewModel.sendMessage(messageInput) },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .background(Color.LightGray, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(text = message.text)
        }
    }
}
