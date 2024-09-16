package com.batuscode.hosbes.viewmodel

import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.Participnat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ParticipantsViewModel: ViewModel() {

    private val _participants = MutableStateFlow<List<Participnat>>(emptyList())
    val participnats: StateFlow<List<Participnat>> get()  = _participants

    fun pushParticipants(participnat: Participnat){

        val roomIter = _participants.value.toMutableList()
        roomIter.add(participnat)
        _participants.value = roomIter

    }


    fun refreshParticipantList(){
        _participants.value = emptyList()
    }
}