package com.ticonsys.spotifyclone.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ticonsys.spotifyclone.R
import com.ticonsys.spotifyclone.databinding.FragmentHomeBinding
import com.ticonsys.spotifyclone.internal.Status
import com.ticonsys.spotifyclone.ui.adapters.SongAdapter
import com.ticonsys.spotifyclone.ui.viewmodels.MainViewModel
import com.zxdmjr.material_utils.hide
import com.zxdmjr.material_utils.show
import com.zxdmjr.material_utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: BaseFragment<FragmentHomeBinding>(
    R.layout.fragment_home
) {

    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var songAdapter: SongAdapter

    override fun initializeViewBinding(view: View) = FragmentHomeBinding.bind(view)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        setupRecyclerView()
        subscribeObservers()
    }

    private fun setupRecyclerView() {
        binding.rvAllSongs.apply {
            adapter = songAdapter
        }

        songAdapter.setOnItemClickListener { _, item ->
            viewModel.playOrToggleSong(item)
        }
    }

    private fun subscribeObservers() {
        viewModel.mediaItems.observe(viewLifecycleOwner, Observer { resource ->
            when(resource.status){
                Status.LOADING -> {
                    binding.pbSong.show()
                }
                Status.SUCCESS -> {
                    binding.pbSong.hide()
                    resource.data?.let {
                        songAdapter.differ.submitList(it)
                    }
                }
                Status.ERROR -> {
                    binding.pbSong.hide()
                    requireContext().toast(resource.message ?: "Failed to connect server")
                }
            }
        })
    }

}