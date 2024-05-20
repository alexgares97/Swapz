package com.eug.swapz.ui.scenes.editarticle

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
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
fun EditArticle(viewModel: EditArticleViewModel, articleId: String) {
    val context = LocalContext.current
    val article by viewModel.article.observeAsState()
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var carrusel by remember { mutableStateOf<List<String>>(emptyList()) }

    val catOptions = listOf("Deportes", "Hogar", "Moda", "Otros")
    val statusOptions = listOf("Usado", "Bueno", "Muy bueno", "Excelente", "Sin abrir")
    var expandedCat by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }

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
            carrusel = carrusel + listOf(downloadUrl.toString())
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
    // Load the article when the screen initializes
    LaunchedEffect(articleId) {
        viewModel.fetchArticle(articleId)
    }


    // Associate the article values with the form fields
    LaunchedEffect(article) {
        article?.let {
            title = it.title
            desc = it.desc
            status = it.status
            cat = it.cat
            value = it.value.toString()
            carrusel = it.carrusel
        }
    }

    fun deleteImg(imageUrl: String) {
        carrusel = carrusel.filter { it != imageUrl }
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
            text = "Editar Artículo",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            items(carrusel) { imageUrl ->
                if (imageUrl.isNotBlank()) {
                    Box(modifier = Modifier.size(100.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
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

        IconButton(
            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título", color = Color.White) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF2F96D8),
                unfocusedBorderColor = Color.Gray,
                textColor = Color.White,
                cursorColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Descripción", color = Color.White) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF2F96D8),
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
                .clickable { expandedStatus = true }
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = status,
                onValueChange = { status = it },
                label = { Text("Estado", color = Color.White) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF2F96D8),
                    unfocusedBorderColor = Color.Gray,
                    textColor = Color.White,
                    cursorColor = Color.White,
                    disabledTextColor = Color.White // Ensure the text color is white even when disabled
                ),
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedStatus,
                onDismissRequest = { expandedStatus = false },
                modifier = Modifier.background(Color(0xFF222222))
            ) {
                statusOptions.forEach { option ->
                    androidx.compose.material.DropdownMenuItem(onClick = {
                        status = option
                        expandedStatus = false
                    }) {
                        androidx.compose.material.Text(option, color = Color.White)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .clickable { expandedCat = true }
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = cat,
                onValueChange = { cat = it },
                label = { Text("Categoría", color = Color.White) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF2F96D8),
                    unfocusedBorderColor = Color.Gray,
                    textColor = Color.White,
                    cursorColor = Color.White,
                    disabledTextColor = Color.White // Ensure the text color is white even when disabled
                ),
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedCat,
                onDismissRequest = { expandedCat = false },
                modifier = Modifier.background(Color(0xFF222222))
            ) {
                catOptions.forEach { option ->
                    androidx.compose.material.DropdownMenuItem(onClick = {
                        cat = option
                        expandedCat = false
                    }) {
                        androidx.compose.material.Text(option, color = Color.White)
                    }
                }
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Valor Nuevo", color = Color.White) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF2F96D8),
                unfocusedBorderColor = Color.Gray,
                textColor = Color.White,
                cursorColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (title.isEmpty() ||
                        desc.isEmpty() ||
                        status.isEmpty() ||
                        cat.isEmpty() ||
                        value.isEmpty() ||
                        carrusel.isEmpty()
                    ) {
                        Toast.makeText(
                            context,
                            "Rellena todos los campos.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.updateArticle(
                            articleId = articleId,
                            title = title,
                            desc = desc,
                            status = status,
                            cat = cat,
                            value = value.toIntOrNull() ?: 0,
                            carrusel = carrusel,
                            img = carrusel.firstOrNull() ?: "",
                            user = article?.user ?: ""
                        )
                        viewModel.navigateToMain()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F96D8)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 20.dp)
            ) {
                Text("Guardar", color = Color.White)
            }

            Button(
                onClick = { viewModel.navigateToMain() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F96D8)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 20.dp)
            ) {
                Text("Cancelar", color = Color.White)
            }
        }
    }
}
