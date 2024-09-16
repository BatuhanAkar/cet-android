package com.batuscode.hosbes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VoiceChannelViewModel:ViewModel(){
    private val _voicechannelAudioMute = MutableLiveData<Boolean>(false)
    val voicechannelAudioMute : LiveData<Boolean> get() = _voicechannelAudioMute

    fun update_voicechannelAudioMute(value: Boolean){
        _voicechannelAudioMute.value = value
    }

    private val _voicechannelVideoMute = MutableLiveData<Boolean>(false)
    val voicechannelVideoMute : LiveData<Boolean> get() = _voicechannelVideoMute

    fun update_voicechannelVideoMute(value: Boolean){
        _voicechannelVideoMute.value = value
    }

    private val _voicechannelhangup = MutableLiveData<Boolean>(false)
    val voicechannelhangup : LiveData<Boolean> get() = _voicechannelhangup

    fun update_voicechannelhangup(value: Boolean){
        _voicechannelhangup.value = value
    }
}