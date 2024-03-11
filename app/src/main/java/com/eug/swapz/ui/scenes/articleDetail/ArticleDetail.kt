package com.eug.swapz.ui.scenes.articleDetail

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddToQueue
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import com.google.android.gms.location.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ArticleDetail(viewModel: ArticleDetailViewModel) {
    val article = viewModel.article ?: return
    var onClickImg by remember { mutableStateOf(false) }
    val images = article.carrusel ?: emptyList()
    val pagerState = rememberPagerState(pageCount = { images.size })
    var addArticleDialog by remember { mutableStateOf(false) }
    var titleFieldValue by remember { mutableStateOf(TextFieldValue()) } // Mutable state variable for text field value
    var description by remember { mutableStateOf(TextFieldValue()) }
    var selectedStatus = remember { mutableStateOf(TextFieldValue()) }
    var value = remember { mutableStateOf(TextFieldValue()) }
    var category by remember { mutableStateOf(TextFieldValue()) }
    val statusOptions = listOf("Usado", "Bueno", "Muy bueno", "Excelente", "Sin abrir")
    var showOptions by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }






    if (!onClickImg) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.app_name)) },
                    actions = {IconButton(onClick = { viewModel.signOut() })
                        {
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
                        IconButton(onClick = { viewModel.home() })
                        {
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
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(article.carrusel?.get(0)),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { onClickImg = true }
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
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )
                Button(
                    onClick = {
                        val requester = ""// Get the requester user
                        val requested = ""// Get the requested user
                        val requestedArticle = "" // Get the article requested by the requester
                        val exchangeArticle = ""// Get the article to be exchanged by the requested
                        //viewModel.exchange(requester, requested, requestedArticle, exchangeArticle)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) { Text(text = "Intercambiar", color = Color.White) }
            }

            Box(modifier = Modifier)
            {
                Row(
                    modifier = Modifier
                        .padding(top = 800.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                ) {
                    IconButton(
                        onClick = { addArticleDialog = true },
                        modifier = Modifier
                            .padding(vertical = 16.dp) // Add vertical padding for better alignment
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
            }
        }
    } else {
        Column(
            modifier = Modifier
                .background(Color.Black)
        ) {
            IconButton(
                onClick = { onClickImg = false },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                Box(modifier = Modifier.fillMaxSize()) {
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
            Spacer(modifier = Modifier.height(16.dp))
            PagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

    }
    if (addArticleDialog) {

        Dialog(onDismissRequest = { addArticleDialog = false })
        {
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
                        IconButton(
                            onClick = { addArticleDialog = false }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                   // Text("Añade título")
                    OutlinedTextField(
                        value = titleFieldValue,
                        onValueChange = { titleFieldValue = it },
                        label = { Text("Título") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                    )
                   // Text("Añade descripción")
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                    )
                    Box(
                        modifier = Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                expanded = true
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
                                .clickable { expanded = true}
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier,
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(onClick = {
                                    selectedStatus.value = TextFieldValue(option)
                                    expanded = false
                                }) {
                                    Text(option)
                                }
                            }
                        }
                    }
                    //Text("Añade valor nuevo")
                    OutlinedTextField(
                        value = value.value,
                        onValueChange = { value.value = it },
                        label = { Text("Valor Nuevo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                    )
                    //Text("Añade categoría")
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                    )
                    Button(
                        onClick = {
                            /*onArticleSubmitted(
                            titleFieldValue,
                            description,
                            estadoOptions.getOrNull(estadoSelectedIndex) ?: "",
                            newValue,
                            category
                        )
                        onDismissRequest()*/
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
fun PagerIndicator(
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
            Indicator(
                color = if (index == pagerState.currentPage) activeColor else inactiveColor,
                size = indicatorSize
            )
        }
    }
}

@Composable
private fun Indicator(
    color: Color,
    size: Int
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(size.dp)
            .background(color)
    )
}
