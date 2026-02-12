package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.ui.main.favorite.model.TuripPlaceUiModel

class FavoritePlaceAdapter(
    private val favoritePlaceListener: FavoritePlaceViewHolder.FavoritePlaceListener,
    private val onCommit: (List<TuripPlaceUiModel>) -> Unit,
) : RecyclerView.Adapter<FavoritePlaceViewHolder>() {
    private val items = mutableListOf<TuripPlaceUiModel>()

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

    fun submitList(newItems: List<TuripPlaceUiModel>) {
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
