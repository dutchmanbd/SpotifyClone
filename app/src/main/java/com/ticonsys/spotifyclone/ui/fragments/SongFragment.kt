package com.ticonsys.spotifyclone.ui.fragments

import android.os.Bundle
import android.view.View
import com.ticonsys.spotifyclone.R
import com.ticonsys.spotifyclone.databinding.FragmentSongBinding

class SongFragment : BaseFragment<FragmentSongBinding>(
    R.layout.fragment_song
) {
    override fun initializeViewBinding(view: View) = FragmentSongBinding.bind(view)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
}