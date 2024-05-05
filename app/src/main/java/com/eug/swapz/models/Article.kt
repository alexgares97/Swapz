package com.eug.swapz.models


data class Article(
    var title: String = "",
    var desc: String = "",
    var status: String = "",
    var cat: String = "",
    var value: Int = 0,
    var id: String? = "",
    val carrusel: List<String>,
    var img: String = "",
    var user: String = ""

    //var imageUrl: String = "",
    //var locations: HashMap<String, Location> = hashMapOf(),
    //var id: String? = "",
    //var user: String = "",

) {

    constructor() : this("", "", "", "",0,"", listOf(), "", "")
}