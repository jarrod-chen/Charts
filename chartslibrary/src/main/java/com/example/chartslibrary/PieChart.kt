package com.example.chartslibrary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PieChart : View {

    private val colors: Array<Long> = arrayOf(
        0xFF37A2DA, 0xFF32C5E9, 0xFF67E0E3, 0xFF9FE6B8, 0xFFFFDB5C, 0xFFFF9F7F, 0xFFFB7293, 0xFFE062AE,
        0xFFE690D1, 0xFFE7BCF3, 0xFF9D96F5, 0xFF8378EA, 0xFF96BFFF
    )

    //绘制饼图的初始角度
    private var mStartAngle: Float = 270f

    private var mData: List<PieData>? = null

    private var mWidth: Int = 0

    private var mHeight: Int = 0

    //画笔
    private var mPaint: Paint = Paint()

    private var r: Float = 0f

    private lateinit var mRect: RectF

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w - paddingLeft - paddingRight
        mHeight = h - paddingTop - paddingBottom
        r = (min(mWidth, mHeight) / 2 * 0.8).toFloat()

        mRect = RectF(-r, -r, r, r)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mData?.let {
            var currentAngle = mStartAngle
            canvas?.translate((mWidth / 2).toFloat(), (mHeight / 2).toFloat())

            canvas?.save()
            it.forEachIndexed { index, pieData ->
                mPaint.color = pieData.color
                canvas?.drawArc(mRect, mStartAngle, pieData.angle, true, mPaint)

                canvas?.rotate(pieData.angle / 2)
                canvas?.drawLine(0f, 0f, 0f, -r * 1.1f, mPaint)

                mPaint.textSize = 42f
                val textWidth = mPaint.measureText("${pieData.value}")
                canvas?.drawText("${pieData.value}", -textWidth / 2, -r * 1.1f, mPaint)

                canvas?.rotate(pieData.angle / 2)
                currentAngle += pieData.angle
            }
            canvas?.restore()
        }
    }

    fun setData(data: List<PieData>?) {
        data?.let {
            mData = it
            initData(it)
            invalidate()
        }
    }

    private fun initData(data: List<PieData>) {
        var sumValue = 0f
        data.forEachIndexed { index, pieData ->
            sumValue += pieData.value
            val i = index % colors.size
            pieData.color = colors[i].toInt()
        }

        var sumAngle: Float = 0f

        data.forEach { pie ->
            //百分比
            val percentage = pie.value / sumValue
            //对应的角度
            val angle = percentage * 360

            pie.angle = angle
            pie.percenterage = percentage

            sumAngle += angle

        }
    }
}