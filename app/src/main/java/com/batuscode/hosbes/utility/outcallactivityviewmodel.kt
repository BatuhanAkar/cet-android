package com.batuscode.hosbes.utility

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OutCallActivityViewModel:ViewModel(){
    private val _Ljoin = MutableLiveData<Boolean?>(null)
    val Ljoin : LiveData<Boolean?> get() = _Ljoin

    fun updateLjoin(value:Boolean){
        _Ljoin.value = value
    }

    private val _WillJoin = MutableStateFlow<Boolean?>(null)
    val WillJoin: StateFlow<Boolean?> get() = _WillJoin

    fun updateWillJoin(value: Boolean){
        _WillJoin.value = value
    }
}