package com.eug.swapz.ui.scenes.articleDetail

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddBox
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.R
import com.google.android.gms.location.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArticleDetail(viewModel: ArticleDetailViewModel) {
    val article = viewModel.article ?: return
    var onClickImg by remember { mutableStateOf(false) }
    val images = article.carrusel ?: emptyList()
    val pagerState = rememberPagerState(pageCount = { images.size })

    // Conditionally show the top bar only if image is not clicked
    if (!onClickImg) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.app_name)) },
                    actions = {

                            Button(
                                onClick = { /* Handle button click */ },
                                modifier = Modifier.padding(horizontal = 50.dp)
                                    .align(Alignment.CenterVertically),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),


                                ) {
                                Text(text = "Intercambiar", color = Color.White)
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
                    }
                )
            }
        ) { innerPadding ->
            // Column content
            // Use innerPadding for content padding
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(10.dp)
                ) {
                    // Content of the composable
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
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "${article.value} €",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(5.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center, // Center horizontally
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .clickable { viewModel.navigateToAddArticle() }
                    ) {
                        Icon(
                            Icons.Filled.AddBox,
                            contentDescription = null,
                            tint = Color.Green,
                            modifier = Modifier.align(Alignment.Center)
                                .size(50.dp)// Center icon within Box
                        )
                    }
                }
            }
        }
    } else {
        // Show the image gallery when image is clicked
        Column(modifier = Modifier.background(Color.Black)) {
            // Content of the image gallery
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
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    indicatorSize: Int = 12,
    indicatorSpacing: Int = 8,
    activeColor: Color = MaterialTheme.colors.primary,
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