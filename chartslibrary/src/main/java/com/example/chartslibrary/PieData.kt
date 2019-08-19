package com.example.chartslibrary

data class PieData (
    var lable: String? = "",
    var value: Float,
    var percenterage: Float = 0f,

    var color: Int = 0,
    var angle: Float = 0f
)