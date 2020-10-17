package com.ticonsys.spotifyclone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.ticonsys.baseadapter.BaseAdapter
import com.ticonsys.spotifyclone.data.entities.Song
import com.ticonsys.spotifyclone.databinding.SimpleSwipeSongItemBinding

class SwipeSongAdapter : BaseAdapter<Song, SimpleSwipeSongItemBinding>() {
    override fun initializeDiffItemCallback() = object : DiffUtil.ItemCallback<Song>(){
        override fun areItemsTheSame(
            oldItem: Song, newItem: Song
        ) = oldItem.mediaId == newItem.mediaId

        override fun areContentsTheSame(
            oldItem: Song, newItem: Song
        ) = oldItem.hashCode() == newItem.hashCode()

    }

    override fun initializeViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ) = SimpleSwipeSongItemBinding.inflate(layoutInflater, parent, false)

    override fun onBindViewHolder(
        holder: BaseViewHolder<SimpleSwipeSongItemBinding>,
        position: Int
    ) {
        val song = differ.currentList[position]
        holder.binding.apply {
            tvPrimary.text = song.title
            root.setOnClickListener {view ->
                listener?.let { click ->
                    click(view, song)
                }
            }
        }
    }
}