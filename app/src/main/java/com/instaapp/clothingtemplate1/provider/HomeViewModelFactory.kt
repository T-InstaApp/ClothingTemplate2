package com.instaapp.clothingtemplate1.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.clothingtemplate1.repositories.HomeRepository
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val homeRepository: HomeRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return HomeViewModel(homeRepository) as T
    }
}