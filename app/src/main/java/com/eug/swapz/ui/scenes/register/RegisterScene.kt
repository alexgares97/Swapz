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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
fun RegisterScene (viewModel: RegisterViewModel) {
    val context = LocalContext.current
    var imageUrl = remember { mutableStateOf(TextFieldValue())}
    val mapView = viewModel.rememberMapViewWithLifecycle()
    val isMapVisible = remember { mutableStateOf(false) }


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
                // Sube la imagen a Firebase Storage
                CoroutineScope(Dispatchers.Main).launch {
                    uploadImageToFirebaseStorage(bitmap)
                }
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permiso de la cámara concedido, iniciar la actividad de la cámara
                cameraLauncher.launch(null)
            }
        }
    fun deleteImg() {
        // Remove the corresponding photo URL from the list
        imageUrl.value = TextFieldValue("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        val usernameState = remember { mutableStateOf("") }
        val nameState = remember { mutableStateOf("") }
        val emailState = remember { mutableStateOf("") }
        val passwordState = remember { mutableStateOf("") }
        //val locationState = remember { mutableStateOf<Location?>(null) }
        OutlinedTextField(
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            label = { Text("Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        OutlinedTextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Nombre completo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (imageUrl.value.text.isNotBlank()) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl.value.text),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp) // Adjust size as needed
                        .clip(RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = {
                        // Remove the corresponding photo URL from the list
                        deleteImg()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Align the icon to the top end of the Box
                        .padding(end = 8.dp, top = 8.dp) // Adjust the padding to move the icon outside
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = null,
                        Modifier.size(24.dp)
                    )
                }
            }
        }

        IconButton(
            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                Icons.Default.PhotoCamera,
                contentDescription = null,
                Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(modifier = Modifier.clickable{}) {


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
                            } else if (username.length < 4) {
                                Toast.makeText(
                                    context,
                                    "Usernames must be at least 4 characters long.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else if (password.length < 6) {
                                Toast.makeText(
                                    context,
                                    "Password must be at least 6 characters long.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                context,
                                "Please fill in all fields.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("Enviar")
                }
            }

            Button(
                onClick = {viewModel.navigateToLogin() },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun RegisterScenePreview() {
    SwapzTheme {
        RegisterFactory (
            navController = rememberAnimatedNavController(),
            sessionDataSource = SessionDataSource()
        )
    }
}