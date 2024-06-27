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

            _whisper.value.get(position).lm = iter.get(position).lm
            _whisper.value.get(position).lt = iter.get(position).lt

        }


    }

    fun refreshWhispers(){
        _whisper.value = emptyList()
    }

    /*TODO: fısıltı sohbet liste viewModel...*/

    private val _wchats = MutableStateFlow<List<Message>>(emptyList())
    val wchat:StateFlow<List<Message>> get() = _wchats


    fun pushChat(message: Message){

        val chatIterator = _wchats.value.toMutableList()

        chatIterator.add(message)

        var position = chatIterator.indexOf(message)

        Log.d("jokermessage" , "eklenen öğenin pozisyonu :: " + position)

//        val updatedChat = chatIterator + message

        _wchats.value = chatIterator

        var realposition = _wchats.value.indexOf(message)
        Log.d("jokermessage" , "eklenen öğenin gerçek pozisyonu :: " + realposition)

        Log.d("jokermessage" , "yeni mesaj eklenmeden sonraki boyut :: " + _wchats.value?.size)

//        val current = _chats.value
//
//        val lastIndex = current + message
//
//        _chats.value = lastIndex
    }

    fun messageRemmoved(message: Message){

        val oldChats = _wchats.value.toMutableList()

        val position = oldChats.indexOf(message)

        if (position in oldChats.indices){


            oldChats.remove(message)

            _wchats.value = oldChats

        }



    }

    fun messageChanged(message: Message){



        val oldChats = _wchats.value.toMutableList()


        val position = oldChats.indexOf(message)
        Log.d("jokermessage" , "yeni mesajın pozisyonu :: " + position)

        if (oldChats != null && position in oldChats.indices){
            Log.d("jokermessage" , "pozisyon aktif...")


            val old = oldChats[position!!]
            Log.d("jokermessage" , "old message :: " + old.message!!)

            oldChats[position!!].message = message.message
            oldChats[position!!].edited = message.edited
            oldChats[position!!].time = message.time





            val changedmessage = oldChats.get(position)

            Log.d("jokermessage" , "changed message :: " + changedmessage.message!!)

            _wchats.value.get(position).message = oldChats.get(position).message
            _wchats.value.get(position).edited = oldChats.get(position).edited
            _wchats.value.get(position).time = oldChats.get(position).time



            val esas = _wchats.value?.get(position)
            Log.d("jokermessage" , "esas :: " + esas?.message!!)

        } else {
            Log.d("jokermessage" , "pozisyon aktif değil ...")

        }


    }

    fun refreshChat(){

        _wchats.value = emptyList()

    }

}