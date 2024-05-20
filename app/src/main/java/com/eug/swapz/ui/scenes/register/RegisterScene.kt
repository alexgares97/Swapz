@file:OptIn(ExperimentalMaterial3Api::class)

package com.eug.swapz.ui.scenes.register

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.R
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.ui.theme.SwapzTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
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
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterScene(viewModel: RegisterViewModel) {
    val context = LocalContext.current
    val imageUrl = remember { mutableStateOf(TextFieldValue()) }

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
            imageUrl.value = TextFieldValue(downloadUrl.toString())
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

    fun deleteImg() {
        imageUrl.value = TextFieldValue("")
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
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .size(160.dp)
                .padding(8.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.Gray),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {
                if (imageUrl.value.text.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl.value.text),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                    IconButton(
                        onClick = { deleteImg() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 8.dp, top = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
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

        val usernameState = remember { mutableStateOf("") }
        val nameState = remember { mutableStateOf("") }
        val emailState = remember { mutableStateOf("") }
        val passwordState = remember { mutableStateOf("") }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = usernameState.value,
                    onValueChange = { usernameState.value = it },
                    label = { Text("Usuario", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF2F96D8),
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    label = { Text("Nombre completo", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF2F96D8),
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = { Text("Email", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF2F96D8),
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = { Text("Contraseña", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF2F96D8),
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color(0xFF2F96D8),
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = MaterialTheme.shapes.small
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    val name = nameState.value
                    val email = emailState.value
                    val username = usernameState.value
                    val password = passwordState.value
                    val photo = imageUrl.value.text

                    if (name.isNotEmpty() && email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && photo.isNotEmpty()) {
                        if (username.length > 3) {
                            viewModel.signUp(email, password, username, name, photo)
                            viewModel.navigateToLogin()
                        } else {
                            Toast.makeText(
                                context,
                                "El nombre de usuario debe tener al menos 4 caracteres.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Por favor, complete todos los campos.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F96D8)),
                shape = RoundedCornerShape(50), // More rounded corners
                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 20.dp) // Adjust padding between buttons
            ) {
                Text("Enviar", color = Color.White)
            }

            Button(
                onClick = { viewModel.navigateToLogin() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F96D8)),
                shape = RoundedCornerShape(50), // More rounded corners
                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 20.dp) // Adjust padding between buttons
            ) {
                Text("Cancelar", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun RegisterScenePreview() {
    SwapzTheme {
        RegisterFactory(
            navController = rememberAnimatedNavController(),
            sessionDataSource = SessionDataSource()
        )
    }
}


