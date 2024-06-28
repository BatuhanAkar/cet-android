package com.batuscode.hosbes.utility

import android.util.Log
import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.Message
import com.batuscode.hosbes.models.PrivateRoom
import com.batuscode.hosbes.models.Whisper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WhisperViewModel: ViewModel() {

    private val _whisper = MutableStateFlow<List<Whisper>>(emptyList())
    val whisper: StateFlow<List<Whisper>> get()  = _whisper

    fun pushWhisper(whisper: Whisper){
        Log.d("whisperflow" , "fısıldama eklendi...")
        val iter = _whisper.value.toMutableList()

        iter.add(whisper)
        _whisper.value = iter
    }

    fun changedWhisper(whisper: Whisper){
        val iter = _whisper.value.toMutableList()

        val position = iter.indexOf(whisper)

        if (position in iter.indices){

            iter[position].lm = whisper.lm
            iter[position].lt = whisper.lt
            iter[position].readed = whisper.readed

            _whisper.value.get(position).lm = iter.get(position).lm
            _whisper.value.get(position).lt = iter.get(position).lt
            _whisper.value.get(position).readed = iter.get(position).readed

        }


    }

    fun removedWhisper(whisper: Whisper){
        Log.d("whisperflow" , "fısıldama eklendi...")
        val iter = _whisper.value.toMutableList()

        val position = iter.indexOf(whisper)

        iter.removeAt(position)
        _whisper.value = iter
    }

    fun refreshWhispers(){
        _whisper.value = emptyList()
    }

}