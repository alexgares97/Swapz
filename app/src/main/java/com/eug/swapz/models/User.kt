package com.eug.swapz.models

import android.location.Location


class User (
    val username: String = "",
    val name: String = "",
    val photo: String = "",
) {
    constructor() : this("", "", "",)
}
