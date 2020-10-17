package com.ticonsys.spotifyclone.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.RequestManager
import com.ticonsys.spotifyclone.data.entities.Song
import com.ticonsys.spotifyclone.databinding.ActivityMainBinding
import com.ticonsys.spotifyclone.exoplayer.toSong
import com.ticonsys.spotifyclone.internal.Status
import com.ticonsys.spotifyclone.ui.adapters.SwipeSongAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>()

    private var curPlayingSong: Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vpSong.apply {
            adapter = swipeSongAdapter
        }

        subscribeToObservers()
    }

    private fun switchViewPagerToCurrentSong(song: Song){
        val newItemIndex = swipeSongAdapter.differ.currentList.indexOf(song)
        if(newItemIndex != -1){
            binding.vpSong.currentItem = newItemIndex
            curPlayingSong = song
        }
    }

    private fun subscribeToObservers(){
        viewModel.mediaItems.observe(this){
            it?.let { result ->
                when(result.status){
                    Status.LOADING -> Unit
                    Status.ERROR -> Unit
                    Status.SUCCESS -> {
                        it.data?.let {songs ->
                            swipeSongAdapter.differ.submitList(
                                songs
                            )
                            if(songs.isNotEmpty()){
                                glide.load((curPlayingSong ?: songs[0]).imageUrl).into(binding.ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                }
            }
        }

        viewModel.curPlayingSong.observe(this){
            if(it == null) return@observe
            curPlayingSong = it.toSong()
            glide.load(curPlayingSong?.imageUrl).into(binding.ivCurSongImage)
            switchViewPagerToCurrentSong(curPlayingSong?: return@observe)
        }
    }
}