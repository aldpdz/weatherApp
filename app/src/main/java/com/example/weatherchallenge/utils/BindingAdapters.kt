package com.example.weatherchallenge.utils

import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter

@BindingAdapter( "statusProgress")
fun bindStatusProgress(statusProgressBar: ProgressBar, status: RemoteStatus?){
    when(status){
        RemoteStatus.LOADING -> {
            statusProgressBar.visibility = View.VISIBLE
        }
        else -> {
            statusProgressBar.visibility = View.GONE
        }
    }
}