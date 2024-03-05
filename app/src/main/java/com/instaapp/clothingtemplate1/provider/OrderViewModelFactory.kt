package com.instaapp.clothingtemplate1.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.clothingtemplate1.repositories.CarRepository
import com.instaapp.clothingtemplate1.viewModel.OrderViewModel

@Suppress("UNCHECKED_CAST")
class OrderViewModelFactory(private val carRepository: CarRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return OrderViewModel(carRepository) as T
    }
}