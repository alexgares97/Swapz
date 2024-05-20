package com.eug.swapz.ui.scenes.addarticle

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AddArticle(viewModel: AddArticleViewModel) {
    var addArticleDialog by remember { mutableStateOf(false) }
    var titleFieldValue = remember { mutableStateOf(TextFieldValue()) }
    val descFieldValue = remember { mutableStateOf(TextFieldValue()) }
    val selectedStatus = remember { mutableStateOf(TextFieldValue()) }
    val selectedCat = remember { mutableStateOf(TextFieldValue()) }
    val value = remember { mutableStateOf(TextFieldValue()) }

    val catOptions = listOf("Deportes", "Hogar", "Moda", "Otros")
    val statusOptions = listOf("Usado", "Bueno", "Muy bueno", "Excelente", "Sin abrir")
    var expandedCat = remember { mutableStateOf(false) }
    var expandedStatus = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var imageUrl = remember { mutableStateOf(emptyList<TextFieldValue>()) }
    val context = LocalContext.current

    suspend fun uploadImageToFirebaseStorage(bitmap: Bitmap) {
        val storage = Firebase.storage
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "imagen_$timestamp.jpg"
        val storageRef = storage.reference.child(filename)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        val taskSnapshot = uploadTask.await()
        val downloadUrl = taskSnapshot.storage.downloadUrl.await()
        if (downloadUrl != null) {
            imageUrl.value += TextFieldValue(downloadUrl.toString())
        }
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    uploadImageToFirebaseStorage(bitmap)
                }
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(null)
            }
        }

    fun deleteImg(imageUrlToDelete: TextFieldValue) {
        imageUrl.value = imageUrl.value.filter { it != imageUrlToDelete }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3A3A3A),
                        Color(0xFF121212)
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Añadir Artículo",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            items(imageUrl.value) { imageUrl ->
                if (imageUrl.text.isNotBlank()) {
                    Box(modifier = Modifier.size(100.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl.text),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .padding(4.dp)
                        )
                        IconButton(
                            onClick = { deleteImg(imageUrl) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = titleFieldValue.value,
                    onValueChange = { titleFieldValue.value = it },
                    label = { Text("Título", color = Color.White) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = descFieldValue.value,
                    onValueChange = { descFieldValue.value = it },
                    label = { Text("Descripción", color = Color.White) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .clickable { expandedStatus.value = true }
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = selectedStatus.value,
                        onValueChange = { newValue -> selectedStatus.value = newValue },
                        label = { Text("Estado", color = Color.White) },
                        enabled = false,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            textColor = Color.White,
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, Color.Gray, RoundedCornerShape(8.dp))
                    )
                    DropdownMenu(
                        expanded = expandedStatus.value,
                        onDismissRequest = { expandedStatus.value = false },
                        modifier = Modifier.background(Color(0xFF222222))
                    ) {
                        statusOptions.forEach { option ->
                            androidx.compose.material.DropdownMenuItem(onClick = {
                                selectedStatus.value = TextFieldValue(option)
                                expandedStatus.value = false
                            }) {
                                androidx.compose.material.Text(option, color = Color.White)
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clickable { expandedCat.value = true }
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = selectedCat.value,
                        onValueChange = { newValue -> selectedCat.value = newValue },
                        label = { Text("Categoría", color = Color.White) },
                        enabled = false,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            textColor = Color.White,
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, Color.Gray, RoundedCornerShape(8.dp))
                    )
                    DropdownMenu(
                        expanded = expandedCat.value,
                        onDismissRequest = { expandedCat.value = false },
                        modifier = Modifier.background(Color(0xFF222222))
                    ) {
                        catOptions.forEach { option ->
                            androidx.compose.material.DropdownMenuItem(onClick = {
                                selectedCat.value = TextFieldValue(option)
                                expandedCat.value = false
                            }) {
                                androidx.compose.material.Text(option, color = Color.White)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = value.value,
                    onValueChange = { value.value = it },
                    label = { Text("Valor Nuevo", color = Color.White) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            IconButton(
                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(8.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF3A8EF8), Color(0xFF0059D6))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            if (titleFieldValue.value.text.isEmpty() ||
                                descFieldValue.value.text.isEmpty() ||
                                selectedStatus.value.text.isEmpty() ||
                                selectedCat.value.text.isEmpty() ||
                                value.value.text.isEmpty() ||
                                imageUrl.value.any { it.text.isBlank() }
                            ) {
                                Toast.makeText(
                                    context,
                                    "Rellena todos los campos.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                addArticleDialog = false
                                viewModel.addArticle(
                                    titleFieldValue.value.text,
                                    descFieldValue.value.text,
                                    selectedStatus.value.text,
                                    selectedCat.value.text,
                                    value.value.text.toIntOrNull() ?: 0,
                                    imageUrl.value.map { it.text }
                                )
                                viewModel.navigateToMain()
                            }
                        },
                        contentAlignment = Alignment.Center
                ) {
                    Text("Subir", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(8.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF3A8EF8), Color(0xFF0059D6))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            viewModel.navigateToMain()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cancelar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
