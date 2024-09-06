package com.batuscode.hosbes.utility

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.RandomParticipant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RandomConnectionActivityViewModel:ViewModel(){

    private val _matched = MutableStateFlow<Boolean?>(null)
    val matched: StateFlow<Boolean?> get() = _matched

    fun updateMatched(state: Boolean?){
        _matched.value = state
    }



    /**
     * Random Connection Activity içinde live ile almak için ...
     * */
    private val _liverandomParticipant = MutableLiveData<RandomParticipant?>(null)
    val liverandomParticipant: LiveData<RandomParticipant?> get() = _liverandomParticipant

    fun updateliveRandomParticipant(liverandomParticipant: RandomParticipant){
        _liverandomParticipant.value = liverandomParticipant
    }
}