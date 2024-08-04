package com.batuscode.hosbes.utility

import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.Calls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VoiceCallsViewModel:ViewModel() {

    private val _requestCall = MutableStateFlow<Boolean?>(null)
    val requestCall: StateFlow<Boolean?> get() = _requestCall

    fun updateRequestCall(state: Boolean){
        _requestCall.value = state
    }


    /**
     * aranan kişinin aramada oolup olmadığını tutan stateflow ...
     * */
    private val _call = MutableStateFlow<Boolean?>(false)
    val call:StateFlow<Boolean?> get() = _call

    fun updateCall(state: Boolean){
        _call.value = state
    }

    private val _calls = MutableStateFlow<Calls?>(null)
    val calls:StateFlow<Calls?> get() = _calls

    fun updateCalls(calls: Calls){
        _calls.value = calls
    }

    private val _Wcalls = MutableStateFlow<Calls?>(null)
    val Wcalls:StateFlow<Calls?> get() = _Wcalls

    fun updateWCalls(calls: Calls){
        _Wcalls.value = calls
    }
}