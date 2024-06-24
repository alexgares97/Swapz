package com.eug.swapz.ui.scenes.main

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.R
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import com.eug.swapz.ui.scenes.filters.FilterViewModel
import com.eug.swapz.ui.scenes.login.LoginFactory
import com.eug.swapz.ui.theme.SwapzTheme

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScene(viewModel: MainViewModel) {
    val articles by viewModel.articles.observeAsState(emptyList())
    var filteredArticles by remember { mutableStateOf(emptyList<Article>()) }
    var searchText by remember { mutableStateOf(TextFieldValue()) }
    val articlesByCategory = articles.groupBy { it.cat }
    var isSearchPerformed by remember { mutableStateOf(false) } // Track search status
    viewModel.fetch()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                isSearchPerformed = isSearchPerformed, // Pass the search status
                viewModel = viewModel, // Pass viewModel to CustomTopAppBar
                searchText = searchText,
                onSearchTextChanged = { newText ->
                    searchText = newText
                },
                onSearchPerformed = {
                    performSearch(articles, searchText.text) { searchResults ->
                        filteredArticles = searchResults
                        isSearchPerformed = true // Set to true when search is performed
                    }
                }
            )
        }
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            if (filteredArticles.isEmpty() && !isSearchPerformed) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .padding(top = 55.dp)
                        .padding(horizontal = 50.dp),
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                ) {
                    articlesByCategory.keys.forEach { category ->
                        val iconResourceId = getIconResourceIdForCategory(category ?: "")
                        Box(
                            modifier = Modifier
                                .clickable {
                                    viewModel.navigateToFilter(
                                        category ?: ""
                                    )
                                }
                                .wrapContentWidth()
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = iconResourceId),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = category ?: "Unknown",
                                    style = TextStyle(fontSize = 12.sp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 130.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    articlesByCategory.forEach { (category, categoryArticles) ->
                        item {
                            Text(
                                text = category ?: "Other",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                content = {
                                    items(categoryArticles) { article ->
                                        Box(
                                            Modifier
                                                .width(150.dp)
                                                .padding(end = 8.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { viewModel.navigateToDetail(article) }
                                        ) {
                                            Column(
                                                Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Image(
                                                    painter = rememberAsyncImagePainter(
                                                        article.carrusel?.get(0)
                                                    ),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(110.dp)
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop,
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    val max_title_length = 19
                                                    Text(
                                                        text = if (article.title.length <= max_title_length) {
                                                            article.title
                                                        } else {
                                                            "${article.title.take(max_title_length)}..."
                                                        },
                                                        style = TextStyle(
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        modifier = Modifier.padding(
                                                            start = 13.dp,
                                                            end = 10.dp
                                                        ),
                                                        maxLines = 1
                                                    )
                                                }
                                                Text(
                                                    text = article.value.toString() + " €",
                                                    style = TextStyle(fontSize = 10.sp),
                                                    modifier = Modifier.padding(start = 14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 128.dp)
                ) {
                    items(
                        items = filteredArticles,
                        itemContent = { article ->
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.navigateToDetail(article) }
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(article.carrusel?.get(0)),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .padding(top = 70.dp, start = 12.dp, end = 12.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop,
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val max_title_length = 19
                                    Text(
                                        text = if (article.title.length <= max_title_length) {
                                            article.title
                                        } else {
                                            "${article.title.take(max_title_length)}..."
                                        },
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(start = 13.dp, end = 10.dp),
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = article.value.toString() + " €",
                                    style = TextStyle(fontSize = 10.sp),
                                    modifier = Modifier.padding(start = 14.dp)
                                )
                            }
                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2F96D8).copy(alpha = 0.9f), Color(0xFF1A73E8).copy(alpha = 0.9f))
                        )
                    )
                    .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationItem(
                    icon = Icons.Filled.Home,
                    label = "Inicio",
                    onClick = { viewModel.navigateToMain() },
                    iconSize = 24.dp
                )
                NavigationItem(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    label = "Chats",
                    onClick = { viewModel.navigateToChatList() },
                    iconSize = 24.dp
                )
                NavigationItem(
                    icon = Icons.Filled.AddCircle,
                    label = "Añadir",
                    onClick = { viewModel.navigateToAddArticle() },
                    iconSize = 25.dp
                )
                NavigationItem(
                    icon = Icons.Filled.Inventory,
                    label = "Inventario",
                    onClick = { viewModel.navigateToInventory() },
                    iconSize = 24.dp
                )
                NavigationItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    label = "Salir",
                    onClick = { viewModel.signOut() },
                    iconSize = 24.dp
                )
            }
        }
    }
}
@Composable
fun NavigationItem(icon: ImageVector, label: String, onClick: () -> Unit, iconSize: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 0.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.height(0.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.caption.copy(
                fontSize = 10.sp,
                color = Color.White
            )
        )
    }
}
private fun performSearch(
    articles: List<Article>,
    query: String,
    onSearchResults: (List<Article>) -> Unit
) {
    val searchResults = articles.filter { article ->
        article.title.contains(query, ignoreCase = true)
    }
    onSearchResults(searchResults)
}
fun getIconResourceIdForCategory(categoryName: String): Int {
    return when (categoryName) {
        "Hogar" -> R.drawable.hogar
        "Deportes" -> R.drawable.atletismo
        "Moda" -> R.drawable.camisa
        "Otros" -> R.drawable.otros
        else -> R.drawable.otros
    }
}
@Composable
fun CustomTopAppBar(
    isSearchPerformed: Boolean, // Parameter to determine if the search has been performed
    viewModel: MainViewModel,
    searchText: TextFieldValue,
    onSearchTextChanged: (TextFieldValue) -> Unit,
    onSearchPerformed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSearchPerformed) { // Conditionally show the back arrow
            IconButton(
                onClick = { viewModel.navigateToMain() }, // Navigate back on click
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black // Adjust color as needed
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Box(
            modifier = Modifier
                .weight(30F)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE0E0E0))
                .clickable { /* Make the TextField editable on click */ }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.width(1.dp))
                BasicTextField(
                    value = searchText,
                    onValueChange = onSearchTextChanged,
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearchPerformed() // Perform search on Enter key press
                        }
                    )
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun MainScenePreview() {
    SwapzTheme {
        LoginFactory(
            navController = rememberNavController(),
            sessionDataSource = SessionDataSource()
        )
    }
}