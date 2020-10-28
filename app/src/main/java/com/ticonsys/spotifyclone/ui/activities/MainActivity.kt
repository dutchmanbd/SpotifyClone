package com.ticonsys.spotifyclone.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.ticonsys.spotifyclone.R
import com.ticonsys.spotifyclone.data.entities.Song
import com.ticonsys.spotifyclone.databinding.ActivityMainBinding
import com.ticonsys.spotifyclone.exoplayer.isPlaying
import com.ticonsys.spotifyclone.exoplayer.toSong
import com.ticonsys.spotifyclone.internal.Status
import com.ticonsys.spotifyclone.ui.adapters.SwipeSongAdapter
import com.ticonsys.spotifyclone.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
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

    private var playbackState: PlaybackStateCompat? = null

    private val navController by lazy {
        navMainHostFragment.findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeToObservers()

        binding.vpSong.adapter = swipeSongAdapter

        binding.vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(playbackState?.isPlaying == true){
                    viewModel.playOrToggleSong(
                        swipeSongAdapter.differ.currentList[position]
                    )
                } else{
                    curPlayingSong = swipeSongAdapter.differ.currentList[position]
                }
            }
        })

        binding.ivPlayPause.setOnClickListener {
            curPlayingSong?.let {
                viewModel.playOrToggleSong(it, true)
            }
        }

        swipeSongAdapter.setOnItemClickListener { view, item ->
            navController.navigate(R.id.globalActionToSongFragment)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.homeFragment -> showBottomBar()
                R.id.songFragment -> hideBottomBar()
                else -> showBottomBar()
            }
        }

    }

    private fun hideBottomBar(){
        binding.ivCurSongImage.isVisible = false
        binding.ivPlayPause.isVisible = false
        binding.vpSong.isVisible = false
    }

    private fun showBottomBar(){
        binding.ivCurSongImage.isVisible = true
        binding.ivPlayPause.isVisible = true
        binding.vpSong.isVisible = true
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

        viewModel.playbackState.observe(this){
            playbackState = it
            binding.ivPlayPause.setImageResource(
                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        viewModel.isConnected.observe(this){
            it?.getContentIfNotHandled()?.let { result ->
                when(result.status){
                    Status.ERROR -> Snackbar.make(
                        binding.root,
                        result.message ?: "An unknown error occurred",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }

        viewModel.networkError.observe(this){
            it?.getContentIfNotHandled()?.let { result ->
                when(result.status){
                    Status.ERROR -> Snackbar.make(
                        binding.root,
                        result.message ?: "An unknown error occurred",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
    }
}