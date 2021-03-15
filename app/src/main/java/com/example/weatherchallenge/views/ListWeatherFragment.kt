package com.example.weatherchallenge.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherchallenge.R
import com.example.weatherchallenge.data.models.remote.toListWeatherInfo
import com.example.weatherchallenge.databinding.FragmentSecondBinding
import com.example.weatherchallenge.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListWeatherFragment : Fragment() {

    private lateinit var binding: FragmentSecondBinding
    private lateinit var weatherAdapter: WeatherAdapter
    private val viewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherAdapter = WeatherAdapter(WeatherListener {
            viewModel.getDetailWeatherInfo(it)
        })
        binding.rvWeatherData.adapter = weatherAdapter
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.listWeatherInfo.observe(viewLifecycleOwner, {
            it?.let {
                weatherAdapter.data = it
            }
        })

        viewModel.navigateToDetail.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                findNavController()
                    .navigate(ListWeatherFragmentDirections
                            .actionSecondFragmentToDetailFragment()) }
        })

        viewModel.nameCity.observe(viewLifecycleOwner, {
            it?.let {
                (requireActivity() as AppCompatActivity)
                        .findViewById<Toolbar>(R.id.toolbar).title = it }
        })
    }
}