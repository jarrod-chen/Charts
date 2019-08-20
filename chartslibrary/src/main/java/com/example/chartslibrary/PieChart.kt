package com.example.chartslibrary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.min

class PieChart : View {

    private val colors: Array<String> = arrayOf(
        "#37A2DA", "#FF32C5E9", "#FF67E0E3", "#FF9FE6B8", "#FFFFDB5C", "#FFFF9F7F", "#FFFB7293", "#FFE062AE",
        "#FFE690D1", "#FFE7BCF3", "#FF9D96F5", "#FF8378EA", "#FF96BFFF"
    )
    //绘制饼图的初始角度
    private var mStartAngle: Float = 270f

    private var mData: List<PieData>? = null

    private var mWidth: Int = 0

    private var mHeight: Int = 0

    //画笔
    private var mPaint: Paint = Paint()

    private var r: Float = 0f
    //圆心坐标
    private var rx: Float = 0f
    private var ry: Float = 0f


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
        rx = w / 2f
        ry = h / 2f
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
                canvas?.drawLine(0f, -r, 0f, -r * 1.1f, mPaint)

                mPaint.textSize = 42f
                val textWidth = mPaint.measureText("${pieData.value}")
                canvas?.drawText("${pieData.value}", -textWidth / 2, -r * 1.1f - 5, mPaint)

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
            pieData.color = Color.parseColor(colors[i])
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x: Double = (event.x - rx).toDouble()
                val y: Double = (event.y - ry).toDouble()

                if (x * x + y * y > r * r) {
                    Log.d("PieChart", "不在圆区域")
                    mData?.forEachIndexed { index, pieData ->
                        pieData.color = Color.parseColor(colors[index])
                    }
                    invalidate()
                    return false
                }

                var angle: Double = 0.0
                //第一象限
                if (x > 0 && y < 0) {
                    angle = atan2((event.x - rx).toDouble(), (ry - event.y).toDouble()) * 180 / PI
                } else if (x > 0 && y > 0) {
                    //第二象限
                    angle = atan2((event.y - ry).toDouble(), (event.x - rx).toDouble()) * 180 / PI + 90
                } else if (x < 0 && y > 0) {
                    //第三象限
                    angle = atan2(rx - event.x, event.y - ry) * 180 / PI + 180
                } else if (x < 0 && y < 0) {
                    //第四象限
                    angle = atan2(ry - event.y, rx - event.x) * 180 / PI + 270
                }

                mData?.apply {
                    var countAngle = 0f
                    forEachIndexed { index, pieData ->
                        pieData.color = Color.parseColor(colors[index])
                    }
                    forEachIndexed { index, pieData ->
                        countAngle += pieData.angle
                        if (angle < countAngle) {
                            val colorT = Color.parseColor(colors[index])
                            //hsv数组 色调 饱和度 亮度
                            val hsv: FloatArray = floatArrayOf(0f, 0f, 0f)
                            Color.RGBToHSV(colorT.red, colorT.green, colorT.blue, hsv)
                            hsv[2] = hsv[2] / 1.2f
                            pieData.color = Color.HSVToColor(hsv)
                            invalidate()
                            return@apply
                        }
                    }
                }
            }
        }
        return true
    }
}