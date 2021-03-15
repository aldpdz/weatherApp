package com.example.weatherchallenge.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherchallenge.R
import com.example.weatherchallenge.databinding.FragmentFirstBinding
import com.example.weatherchallenge.viewmodels.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.setKey(getString(R.string.api))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLookup.setOnClickListener {
            val city = binding.textInputLayoutCity.editText?.text.toString()

            if(city.isEmpty()){
                binding.textInputLayoutCity.error = getString(R.string.valid_city)
            }else{
                viewModel.searchCity(city)
                binding.textInputLayoutCity.error = null
            }
        }

        setUpObservers()
    }

    private fun setUpObservers(){
        viewModel.navigateToListWeather.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let{
                findNavController()
                    .navigate(SearchFragmentDirections.actionFirstFragmentToSecondFragment())
            }
        })
        viewModel.showError.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let{ message ->
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        })
    }
}