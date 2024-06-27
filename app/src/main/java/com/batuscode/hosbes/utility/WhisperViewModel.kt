package com.batuscode.hosbes.utility

import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.PrivateRoom
import com.batuscode.hosbes.models.Whisper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WhisperViewModel: ViewModel() {

    private val _whisper = MutableStateFlow<List<Whisper>>(emptyList())
    val whisper: StateFlow<List<Whisper>> get()  = _whisper

    fun pushWhisper(whisper: Whisper){

    }
}