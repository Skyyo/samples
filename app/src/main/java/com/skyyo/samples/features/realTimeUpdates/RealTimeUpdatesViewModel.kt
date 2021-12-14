package com.skyyo.samples.features.realTimeUpdates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.skyyo.samples.application.repositories.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealTimeUpdatesViewModel @Inject constructor(
    private val repository: CryptoRepository
) : ViewModel() {

    val assets = repository.getAssets().flow
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)

    fun providePriceUpdateFlow(id: String) = repository.subscribeToPriceUpdates(id)

    fun onPriceUpdate(id: String, price: String) = viewModelScope.launch {
        repository.updateAsset(id, price)
    }

}