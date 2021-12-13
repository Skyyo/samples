package com.skyyo.samples.features.realTimeUpdates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.repositories.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealTimeUpdatesViewModel @Inject constructor(
    private val repository: CryptoRepository
) : ViewModel() {

    val assets = repository.getAssets().flow

    fun providePriceUpdateFlow(id: String) = repository.subscribeToPriceUpdates(id)

    fun onPriceUpdate(id: String, price: String) = viewModelScope.launch {
        repository.updateAsset(id, price)
    }

}