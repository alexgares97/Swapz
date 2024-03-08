package com.eug.swapz.models


data class Article(
    var title: String = "",
    var desc: String = "",
    var img: String = "",
    var value: Int = 0,
    // var imageUrl: String = "",
    var locations: HashMap<String, Location> = hashMapOf(),
    var id: String? = "",
    var user: String = "",
    val carrusel: List<String>?

) {

    constructor() : this("", "", "",0,hashMapOf(), "","",listOf())
}