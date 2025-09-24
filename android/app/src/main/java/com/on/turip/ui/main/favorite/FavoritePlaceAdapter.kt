package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel

class FavoritePlaceAdapter(
    private val favoritePlaceListener: FavoritePlaceViewHolder.FavoritePlaceListener,
    private val onCommit: (List<FavoritePlaceModel>) -> Unit,
) : RecyclerView.Adapter<FavoritePlaceViewHolder>() {
    private val items = mutableListOf<FavoritePlaceModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoritePlaceViewHolder = FavoritePlaceViewHolder.of(parent, favoritePlaceListener)

    override fun onBindViewHolder(
        holder: FavoritePlaceViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<FavoritePlaceModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun moveItem(
        from: Int,
        to: Int,
    ) {
        if (from == to) return
        val item = items.removeAt(from)
        items.add(to, item)
        notifyItemMoved(from, to)
    }

    fun commitMove() {
        onCommit(items.toList())
    }
}
