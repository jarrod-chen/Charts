package com.example.viewlesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chartslibrary.PieData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pieA = PieData("A", 55.5f)
        val pieB = PieData("B", 111.1f)
        val pieC = PieData("C", 55f)
        val pieD = PieData("D", 324.79f)
        val pieF = PieData("F", 55.8f)

        val list: List<PieData> = listOf(pieA, pieB, pieC, pieD, pieF)

        pieChart.setData(list)

    }
}
