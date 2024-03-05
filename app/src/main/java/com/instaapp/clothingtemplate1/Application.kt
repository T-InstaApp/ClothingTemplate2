package com.instaapp.clothingtemplate1

import android.app.Application
import com.instaapp.clothingtemplate1.network.MyApi
import com.instaapp.clothingtemplate1.network.NetworkConnectionInterceptor
import com.instaapp.clothingtemplate1.provider.AuthViewModelFactory
import com.instaapp.clothingtemplate1.provider.OrderViewModelFactory
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.repositories.AuthRepository
import com.instaapp.clothingtemplate1.repositories.CarRepository
import com.instaapp.clothingtemplate1.repositories.HomeRepository
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class Application : Application(), KodeinAware {
/* Test First Commit */
    override val kodein = Kodein.lazy {
        import(androidXModule(this@Application))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { HomeRepository(instance()) }
        bind() from singleton { CarRepository(instance()) }
        bind() from singleton { AuthRepository(instance()) }
        //Providers
        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from provider { OrderViewModelFactory(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
    }
}