package com.eug.swapz.helpers


import androidx.compose.runtime.Composable

interface ComposableFactory<in Any> {
    @Composable
    fun create(id: String?): kotlin.Any
}