package com.example.weatherchallenge.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weatherchallenge.databinding.FragmentDetailBinding
import com.example.weatherchallenge.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater)

        setUpObservers()

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.detailInfo.observe(viewLifecycleOwner, {
            it?.let { binding.detailData = it }
        })
        viewModel.nameCity.observe(viewLifecycleOwner, {
            it?.let { (requireActivity() as AppCompatActivity).title = it }
        })
    }
}