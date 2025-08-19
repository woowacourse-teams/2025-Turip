package com.on.turip.ui.main.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemHelpInformationBinding
import com.on.turip.ui.main.favorite.model.HelpInformationModel

class FavoriteHelpInformationViewHolder(
    private val binding: ItemHelpInformationBinding,
    onHelpInformationListener: OnHelpInformationListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var helpInformationModel: HelpInformationModel? = null

    init {
        itemView.setOnClickListener {
            helpInformationModel?.let {
                onHelpInformationListener.onHelpInformationClick(it)
            }
        }
    }

    fun bind(helpInformationModel: HelpInformationModel) {
        this.helpInformationModel = helpInformationModel
        binding.ivHelpInformationIcon.setImageResource(helpInformationModel.iconResource)
        binding.tvHelpInformationTitle.setText(helpInformationModel.title)
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onHelpInformationListener: OnHelpInformationListener,
        ): FavoriteHelpInformationViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemHelpInformationBinding =
                ItemHelpInformationBinding.inflate(inflater, parent, false)
            return FavoriteHelpInformationViewHolder(binding, onHelpInformationListener)
        }
    }

    fun interface OnHelpInformationListener {
        fun onHelpInformationClick(helpInformationModel: HelpInformationModel)
    }
}
