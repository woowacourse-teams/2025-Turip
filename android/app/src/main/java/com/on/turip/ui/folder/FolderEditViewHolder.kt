package com.on.turip.ui.folder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemFolderEditBinding
import com.on.turip.ui.folder.model.FolderEditModel

class FolderEditViewHolder(
    private val binding: ItemFolderEditBinding,
    folderEditListener: FolderEditListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var folderEditModel: FolderEditModel? = null

    init {
        binding.ivFolderEditRemove.setOnClickListener {
            folderEditModel?.let { folderEditListener.onRemoveClick(it.id) }
        }

        binding.clFolderEditItem.setOnClickListener {
            folderEditModel?.let { folderEditListener.onItemClick(it.id) }
        }
    }

    fun bind(folderEditModel: FolderEditModel) {
        this.folderEditModel = folderEditModel

        binding.tvFolderEditName.text = folderEditModel.name
        binding.tvFolderPlaceCount.text =
            itemView.context.getString(R.string.all_total_place_count, folderEditModel.count)
    }

    companion object {
        fun of(
            parent: ViewGroup,
            folderEditListener: FolderEditListener,
        ): FolderEditViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemFolderEditBinding =
                ItemFolderEditBinding.inflate(inflater, parent, false)
            return FolderEditViewHolder(binding, folderEditListener)
        }
    }

    interface FolderEditListener {
        fun onRemoveClick(folderId: Long)

        fun onItemClick(folderId: Long)
    }
}
