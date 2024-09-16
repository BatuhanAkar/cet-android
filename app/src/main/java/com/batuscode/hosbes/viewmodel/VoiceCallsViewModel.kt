package com.batuscode.hosbes.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VoiceCallsViewModel:ViewModel() {




    /**
     * aranan kişinin aramada oolup olmadığını tutan stateflow ...
     * */
    private val _call = MutableStateFlow<Boolean?>(false)
    val call:StateFlow<Boolean?> get() = _call

    fun updateCall(state: Boolean){
        _call.value = state
    }


}