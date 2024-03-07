package com.eug.swapz.datasources.interfaces

import com.eug.swapz.models.Article

interface IMainDataSource {

    suspend fun fetch(): List<Article>
    fun get(id: String): Article?
}