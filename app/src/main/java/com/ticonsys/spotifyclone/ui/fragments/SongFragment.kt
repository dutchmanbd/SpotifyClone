package com.ticonsys.spotifyclone.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.ticonsys.spotifyclone.R
import com.ticonsys.spotifyclone.data.entities.Song
import com.ticonsys.spotifyclone.databinding.FragmentSongBinding
import com.ticonsys.spotifyclone.exoplayer.isPlaying
import com.ticonsys.spotifyclone.exoplayer.toSong
import com.ticonsys.spotifyclone.internal.Status
import com.ticonsys.spotifyclone.ui.viewmodels.MainViewModel
import com.ticonsys.spotifyclone.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : BaseFragment<FragmentSongBinding>(
    R.layout.fragment_song
) {

    @Inject
    lateinit var glide: RequestManager


    lateinit var mainViewModel: MainViewModel
    private val songViewModel by viewModels<SongViewModel>()

    private var curPlayingSong: Song? = null
    private var playbackState: PlaybackStateCompat? = null


    private var shouldUpdateSeekBar = true

    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    override fun initializeViewBinding(view: View) = FragmentSongBinding.bind(view)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        subscribeToObservers()

        binding.ivPlayPauseDetail.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    setCurPlayerPositionToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekBar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekBar = true
                }
            }

        })

        binding.ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }

        binding.ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }
    }

    private fun updateSongTitleAndImage(song: Song){
        val title = "${song.title} - ${song.subtitle}"
        binding.tvSongName.text = title
        glide.load(song.imageUrl).into(binding.ivSongImage)
    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItems.observe(viewLifecycleOwner){
            it?.let { result ->
                when(result.status){
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            if(curPlayingSong == null && songs.isNotEmpty()){
                                curPlayingSong = songs[0]
                                updateSongTitleAndImage(songs[0])
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(viewLifecycleOwner){
            it ?: return@observe
            curPlayingSong = it.toSong()
            updateSongTitleAndImage(curPlayingSong!!)
        }

        mainViewModel.playbackState.observe(viewLifecycleOwner){
            playbackState = it
            binding.ivPlayPauseDetail.setImageResource(
                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            binding.seekBar.progress = it?.position?.toInt() ?: 0
        }

        songViewModel.curPlayerPosition.observe(viewLifecycleOwner){
            if(shouldUpdateSeekBar){
                binding.seekBar.progress = it.toInt()
                setCurPlayerPositionToTextView(it)
            }
        }

        songViewModel.curSongDuration.observe(viewLifecycleOwner){
            setCurPlayingSongDuration(it)
        }

    }


    private fun setCurPlayerPositionToTextView(ms: Long) {
        binding.tvCurTime.text = dateFormat.format(ms)
    }

    private fun setCurPlayingSongDuration(ms: Long) {
        binding.seekBar.max = ms.toInt()
        binding.tvSongDuration.text = dateFormat.format(ms)
    }


}