package com.on.turip.ui.folder

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.folder.model.TuripEditModel

class FolderEditAdapter(
    private val folderEditListener: FolderEditViewHolder.FolderEditListener,
) : ListAdapter<TuripEditModel, FolderEditViewHolder>(FolderDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FolderEditViewHolder = FolderEditViewHolder.of(parent, folderEditListener)

    override fun onBindViewHolder(
        holder: FolderEditViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object FolderDiffUtil : DiffUtil.ItemCallback<TuripEditModel>() {
        override fun areItemsTheSame(
            oldItem: TuripEditModel,
            newItem: TuripEditModel,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: TuripEditModel,
            newItem: TuripEditModel,
        ): Boolean = oldItem == newItem
    }
}
