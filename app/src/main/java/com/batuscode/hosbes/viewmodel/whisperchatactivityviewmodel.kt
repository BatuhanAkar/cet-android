package com.batuscode.hosbes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WhisperChatActivityViewModel:ViewModel(){
    private val _finish = MutableLiveData<Boolean>(false)
    val finish:LiveData<Boolean> get() = _finish

    fun update_finish(value:Boolean){
        _finish.value = value
    }
}