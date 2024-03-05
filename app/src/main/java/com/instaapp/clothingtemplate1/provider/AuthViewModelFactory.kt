package com.instaapp.clothingtemplate1.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.instaapp.clothingtemplate1.repositories.AuthRepository
import com.instaapp.clothingtemplate1.viewModel.AuthViewModel

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val authRepository: AuthRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AuthViewModel(authRepository) as T
    }
}