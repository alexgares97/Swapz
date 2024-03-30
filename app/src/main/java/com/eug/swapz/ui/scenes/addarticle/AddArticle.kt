package com.eug.swapz.ui.scenes.addarticle

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddArticle(viewModel: AddArticleViewModel){
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
    var openCameraEvent by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var imageUrl = remember { mutableStateOf(emptyList<TextFieldValue>()) }
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
            imageUrl.value = imageUrl.value + TextFieldValue(downloadUrl.toString())

        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            // Sube la imagen a Firebase Storage
            CoroutineScope(Dispatchers.Main).launch {
                uploadImageToFirebaseStorage(bitmap)
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Permiso de la cámara concedido, iniciar la actividad de la cámara
            cameraLauncher.launch(null)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { viewModel.signOut() }) {
                        Box(
                            Modifier
                                .size(37.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Green) // Fondo verde
                        ) {
                            Icon(
                                Icons.Filled.ExitToApp,
                                contentDescription = null,
                                tint = Color.White, // Ícono en color blanco
                                modifier = Modifier
                                    .align(Alignment.Center) // Centrar el ícono dentro del botón
                                    .padding(10.dp)
                            )
                        }
                    }
                }
            )
        })
    {
        Column(
            modifier = Modifier
                .padding(horizontal = 50.dp, vertical = 100.dp) // Add vertical padding/margin
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between items
        ) {
            TextField(
                value = titleFieldValue.value,
                onValueChange = {titleFieldValue.value = it},
                label = { androidx.compose.material.Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Description text field
            OutlinedTextField(
                value = descFieldValue.value,
                onValueChange = {descFieldValue.value = it},
                label = { androidx.compose.material.Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        expandedStatus.value = true
                    }
            ){
                OutlinedTextField(
                    value = selectedStatus.value.text,
                    onValueChange = { newValue ->
                        selectedStatus.value = TextFieldValue(newValue)
                    }, // Disable editing
                    label = { androidx.compose.material.Text("Estado") },
                    enabled = false,
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .clickable { expandedStatus.value = true}
                )
                DropdownMenu(
                    expanded = expandedStatus.value,
                    onDismissRequest = { expandedStatus.value = false },
                    modifier = Modifier,
                ) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(onClick = {
                            selectedStatus.value = TextFieldValue(option)
                            expandedStatus.value = false
                        }) {
                            androidx.compose.material.Text(option)
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        expandedCat.value = true
                    }
            ) {
                //Text("Añade categoría")
                OutlinedTextField(
                    value = selectedCat.value.text,
                    onValueChange = { newValue ->
                        selectedCat.value = TextFieldValue(newValue)
                    },
                    label = { androidx.compose.material.Text("Categoría") },
                    enabled = false,
                    interactionSource = interactionSource,
                    modifier = Modifier.clickable { expandedCat.value = true }
                )
                DropdownMenu(
                    expanded = expandedCat.value,
                    onDismissRequest = { expandedCat.value = false },
                    modifier = Modifier,
                ) {
                    catOptions.forEach { option ->
                        DropdownMenuItem(onClick = {
                            selectedCat.value = TextFieldValue(option)
                            expandedCat.value = false
                        }) {
                            androidx.compose.material.Text(option)
                        }
                    }
                }
            }
            OutlinedTextField(
                value = value.value,
                onValueChange = { value.value = it},
                label = { androidx.compose.material.Text("Valor Nuevo") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyRow {
                items(imageUrl.value) { imageUrl ->
                    if (imageUrl.text.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl.text),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp) // Adjust size as needed
                                .clip(RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA)},
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                androidx.compose.material.Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = null,
                    Modifier.size(50.dp)
                )
            }

            Button(
                onClick = {
                    // Perform the actions when the button is clicked
                    addArticleDialog = false
                    viewModel.addArticle(
                        titleFieldValue.value.text,
                        descFieldValue.value.text,
                        selectedStatus.value.text,
                        selectedCat.value.text,
                        value.value.text.toIntOrNull(),
                        imageUrl.value.map { it.text }
                    )
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                androidx.compose.material.Text(text = "Submit")
            }
        }
    }

}