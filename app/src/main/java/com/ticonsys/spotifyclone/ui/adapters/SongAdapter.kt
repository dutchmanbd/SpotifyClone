package com.ticonsys.spotifyclone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.RequestManager
import com.ticonsys.baseadapter.BaseAdapter
import com.ticonsys.spotifyclone.data.entities.Song
import com.ticonsys.spotifyclone.databinding.SimpleSongItemBinding
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
): BaseAdapter<Song, SimpleSongItemBinding>() {

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
    ) = SimpleSongItemBinding.inflate(layoutInflater, parent, false)

    override fun onBindViewHolder(holder: BaseViewHolder<SimpleSongItemBinding>, position: Int) {
        val song = differ.currentList[position]
        with(holder.binding){
            tvPrimary.text = song.title
            tvSecondary.text = song.subtitle
            glide.load(song.imageUrl).into(ivItemImage)
            root.setOnClickListener {view ->
                listener?.let { click ->
                    click(view, song)
                }
            }
        }
    }

}