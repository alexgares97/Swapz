package com.eug.swapz.ui.scenes.articleDetail

import android.annotation.SuppressLint
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.eug.swapz.R

import com.google.android.gms.location.*


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetail(viewModel: ArticleDetailViewModel) {
    val article = viewModel.article ?: return
    // val mapView = viewModel.rememberMapViewWithLifecycle()
    // val selectedLocation = remember { mutableStateOf<Location?>(null) }
    val showAddLocationPopup = remember { mutableStateOf(false) }
    val newLocationTitle = remember { mutableStateOf(TextFieldValue()) }
    val newLocationCategory = remember { mutableStateOf(TextFieldValue()) }
    val newLocationDescription = remember { mutableStateOf(TextFieldValue()) }
    val newLocationImg = remember { mutableStateOf(TextFieldValue()) }
    var selectedCategory = remember { mutableStateOf(TextFieldValue()) }
    var showOptions by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = article.title) },
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
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            )
        }) { innerPadding ->
    }

}
