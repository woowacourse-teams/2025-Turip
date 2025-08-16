package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.main.favorite.model.HelpInformationModel

class FavoriteHelpInformationAdapter(
    private val onHelpInformationListener: FavoriteHelpInformationViewHolder.OnHelpInformationListener,
) : ListAdapter<HelpInformationModel, FavoriteHelpInformationViewHolder>(
        FavoriteHelpInformationDiffUtil,
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoriteHelpInformationViewHolder = FavoriteHelpInformationViewHolder.of(parent, onHelpInformationListener)

    override fun onBindViewHolder(
        holder: FavoriteHelpInformationViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object FavoriteHelpInformationDiffUtil : DiffUtil.ItemCallback<HelpInformationModel>() {
        override fun areItemsTheSame(
            oldItem: HelpInformationModel,
            newItem: HelpInformationModel,
        ): Boolean = oldItem.title == newItem.title

        override fun areContentsTheSame(
            oldItem: HelpInformationModel,
            newItem: HelpInformationModel,
        ): Boolean = oldItem == newItem
    }
}
