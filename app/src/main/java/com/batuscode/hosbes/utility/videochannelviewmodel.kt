package com.batuscode.hosbes.utility

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoChannelViewModel:ViewModel(){

    private val _videochannelAudioMute = MutableLiveData<Boolean>(false)
    val videochannelAudioMute : LiveData<Boolean> get() = _videochannelAudioMute

    fun update_videochannelAudioMute(value: Boolean){
        _videochannelAudioMute.value = value
    }

    private val _videochannelVideoMute = MutableLiveData<Boolean>(false)
    val videochannelVideoMute : LiveData<Boolean> get() = _videochannelVideoMute

    fun update_videochannelVideoMute(value: Boolean){
        _videochannelVideoMute.value = value
    }

    private val _videochannelhangup = MutableLiveData<Boolean>(false)
    val videochannelhangup : LiveData<Boolean> get() = _videochannelhangup

    fun update_videochannelhangup(value: Boolean){
        _videochannelhangup.value = value
    }
}