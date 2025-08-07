package com.on.turip.ui.common

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDividerDecoration(
    private val height: Float,
    private val padding: Float = 0f,
    private val color: Int,
) : RecyclerView.ItemDecoration() {
    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val paint: Paint = Paint().apply { color = this@ItemDividerDecoration.color }
        val left: Float = parent.paddingStart + padding
        val right: Float = parent.width - parent.paddingEnd - padding
        for (i in 0 until parent.childCount - 1) {
            val child: View = parent.getChildAt(i)
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top: Float = (child.bottom + params.bottomMargin).toFloat()
            val bottom: Float = top + height
            c.drawRect(left, top, right, bottom, paint)
        }
    }
}
