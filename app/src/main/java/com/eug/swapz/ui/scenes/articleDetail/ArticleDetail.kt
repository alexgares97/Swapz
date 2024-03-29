package com.eug.swapz.ui.scenes.articleDetail

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.R
import com.eug.swapz.models.Article
import com.google.android.gms.location.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArticleDetail(viewModel: ArticleDetailViewModel) {
    val article = viewModel.article ?: return
    var isImageClicked by remember { mutableStateOf(false) }
    val images = article.carrusel ?: emptyList()
    val pagerState = rememberPagerState(pageCount = { images.size })
    var addArticleDialog by remember { mutableStateOf(false) }
    var titleFieldValue = remember { mutableStateOf(TextFieldValue()) }
    val descFieldValue = remember { mutableStateOf(TextFieldValue()) }
    val selectedStatus = remember { mutableStateOf(TextFieldValue()) }
    val selectedCat = remember { mutableStateOf(TextFieldValue()) }
    val value = remember { mutableStateOf(TextFieldValue()) }
    val category by remember { mutableStateOf(TextFieldValue()) }

    val catOptions = listOf("Deportes", "Hogar", "Moda", "Otros")
    val statusOptions = listOf("Usado", "Bueno", "Muy bueno", "Excelente", "Sin abrir")
    var expandedCat = remember { mutableStateOf(false) }
    var expandedStatus = remember { mutableStateOf(false) }

    var openCameraEvent by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    var imageUrl = remember { mutableStateOf(TextFieldValue()) }


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
                                .background(Color.Green)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(4.dp)
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.home() }) {
                        Box(
                            Modifier
                                .size(37.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Green)
                        ) {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            )
        }
    ) {
        if (!isImageClicked) {
            ArticleDetailsContent(article, pagerState, onImageClick = { isImageClicked = true }) {
                IconButton(
                    onClick = { addArticleDialog = true },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        Modifier
                            .size(37.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            Icons.Filled.AddBox,
                            contentDescription = null,
                            tint = Color.Green,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        } else {
            ImageGallery(images, pagerState, onBackClick = { isImageClicked = false })
        }
        AddArticleDialog(
            addArticleDialog = addArticleDialog,
            titleFieldValue = titleFieldValue,
            descFieldValue = descFieldValue,
            selectedStatus = selectedStatus,
            selectedCat = selectedCat,
            value = value,
            category = category,
            catOptions = catOptions,
            statusOptions = statusOptions,
            interactionSource = interactionSource,
            expandedCat = expandedCat,
            expandedStatus = expandedStatus,
            imageUrl = imageUrl,
            onAddArticle = {
                addArticleDialog = false
                viewModel.addArticle(titleFieldValue.value.text,
                    descFieldValue.value.text,
                    selectedStatus.value.text,
                    selectedCat.value.text,
                    value.value.text.toIntOrNull(),
                    imageUrl.value.text)

            },
            onOpenCameraEvent = { openCameraEvent = true }
        ) { addArticleDialog = false }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArticleDetailsContent(
    article: Article,
    pagerState: PagerState,
    onImageClick: () -> Unit,
    actionButton: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = rememberAsyncImagePainter(article.carrusel?.get(0)),
            contentDescription = null,
            modifier = Modifier
                .clickable { onImageClick() }
                .fillMaxWidth()
        )
        Text(
            text = article.title,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp, top = 10.dp)
        )
        Text(
            text = article.desc,
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "${article.value} €",
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* Handle button click */
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Intercambiar", color = Color.White)
        }
        actionButton()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageGallery(
    images: List<String>,
    pagerState: PagerState,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.background(Color.Black)) {
        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        ) {
            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(top = 8.dp) // Adjust the top padding to move the arrow slightly up
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp) // Adjust the start and top padding as needed
        ) {
           // Spacer(modifier = Modifier.width(64.dp)) // Adjust the width as needed
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(images[page]),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddArticleDialog(
    addArticleDialog: Boolean,
    titleFieldValue: MutableState<TextFieldValue>,
    descFieldValue: MutableState<TextFieldValue>,
    selectedStatus: MutableState<TextFieldValue>,
    selectedCat: MutableState<TextFieldValue>,
    value: MutableState<TextFieldValue>,
    category: TextFieldValue,
    catOptions: List<String>,
    statusOptions: List<String>,
    imageUrl: MutableState<TextFieldValue>,
    interactionSource: MutableInteractionSource,
    expandedCat: MutableState<Boolean>,
    expandedStatus: MutableState<Boolean>,
    onAddArticle: () -> Unit,
    onOpenCameraEvent: () -> Unit,
    onDismissRequest: () -> Unit
) {
    if (addArticleDialog) {

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
                imageUrl.value = TextFieldValue(downloadUrl.toString()) // Use TextFieldValue to wrap the downloadUrl
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
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        IconButton(onClick = { onDismissRequest() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    TextField(
                        value = titleFieldValue.value,
                        onValueChange = {titleFieldValue.value = it},
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Description text field
                    OutlinedTextField(
                        value = descFieldValue.value,
                        onValueChange = {descFieldValue.value = it},
                        label = { Text("Description") },
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
                            label = { Text("Estado") },
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
                                    Text(option)
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
                            label = { Text("Categoría") },
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
                                    Text(option)
                                }
                            }
                        }
                    }
                    OutlinedTextField(
                        value = value.value,
                        onValueChange = { value.value = it},
                        label = { Text("Valor Nuevo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    IconButton(
                        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA)},
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = null,
                            Modifier.size(50.dp)
                        )
                    }

                    Button(
                        onClick = {
                            onAddArticle()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "Submit")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    indicatorSize: Int = 12,
    indicatorSpacing: Int = 8,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.Gray
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { index ->
            Spacer(modifier = Modifier.width(indicatorSpacing.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(indicatorSize.dp)
                    .background(if (index == pagerState.currentPage) activeColor else inactiveColor)
            )
        }
    }
}