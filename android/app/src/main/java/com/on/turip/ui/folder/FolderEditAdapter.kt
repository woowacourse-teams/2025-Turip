package com.on.turip.ui.folder

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.folder.model.FolderEditModel

class FolderEditAdapter(
    private val folderEditListener: FolderEditViewHolder.FolderEditListener,
) : ListAdapter<FolderEditModel, FolderEditViewHolder>(FolderDiffUtil) {
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

    private object FolderDiffUtil : DiffUtil.ItemCallback<FolderEditModel>() {
        override fun areItemsTheSame(
            oldItem: FolderEditModel,
            newItem: FolderEditModel,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: FolderEditModel,
            newItem: FolderEditModel,
        ): Boolean = oldItem == newItem
    }
}
