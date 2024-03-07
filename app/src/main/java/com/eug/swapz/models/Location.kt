package com.eug.swapz.models


data class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var title: String = "",
    var description:String="",
    var img:String=""
){
    constructor() : this(0.0, 0.0, "", "","")

}