package com.on.turip.ui.common

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDividerDecoration(
    private val height: Int,
    private val padding: Int = 0,
    private val color: Int,
) : RecyclerView.ItemDecoration() {
    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val paint: Paint = Paint().apply { color = this@ItemDividerDecoration.color }
        val left: Float = parent.paddingStart + padding.dpToPxFloat
        val right: Float = parent.width - parent.paddingEnd - padding.dpToPxFloat
        for (i in 0 until parent.childCount - 1) {
            val child: View = parent.getChildAt(i)
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top: Float = (child.bottom + params.bottomMargin).toFloat()
            val bottom: Float = top + height.dpToPxFloat
            c.drawRect(left, top, right, bottom, paint)
        }
    }
}
