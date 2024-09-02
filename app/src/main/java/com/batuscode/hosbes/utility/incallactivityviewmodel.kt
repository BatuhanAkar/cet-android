package com.batuscode.hosbes.utility

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InCallActivityViewModel: ViewModel(){
    private val _requestCall = MutableLiveData<Boolean?>(null)
    val requestCall: LiveData<Boolean?> get() = _requestCall

    fun updateRequestCall(state: Boolean){
        _requestCall.value = state
    }

    private val _WillJoin = MutableStateFlow<Boolean?>(null)
    val WillJoin: StateFlow<Boolean?> get() = _WillJoin

    fun updateWillJoin(value: Boolean){
        _WillJoin.value = value
    }

    private val _endCall = MutableLiveData<Boolean?>(false)
    val endCall: LiveData<Boolean?> get() = _endCall

    fun updateEndCall(status: Boolean){
        _endCall.value = status
    }

    private val _WcallMuteAudio = MutableLiveData<Boolean>(false)
    val WcallMuteAudio : LiveData<Boolean> get() = _WcallMuteAudio

    fun update_WcallMuteAudio(value: Boolean){
        _WcallMuteAudio.value = value
    }

    private val _WcallHangUp = MutableLiveData<Boolean>(false)
    val WcallHangUp : LiveData<Boolean> get() = _WcallHangUp

    fun update_WcallHangUp(value: Boolean){
        _WcallHangUp.value = value
    }
}