package com.batuscode.hosbes.utility

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.Message

class ChatViewModel: ViewModel() {

    private val _chat = MutableLiveData<List<Message>>(mutableListOf())
    val chat: LiveData<List<Message>> get()  = _chat


    fun pushChat(message: Message){

        val chatIterator = _chat.value ?: mutableListOf()
        val updatedList = chatIterator + message

        _chat.value = updatedList

    }

    fun messageChanged(message: Message){
        var position = _chat.value?.indexOf(message)


        Log.d("privateroomsflow" , "position :: $position")

        val iterator = _chat.value?.toMutableList()



        if (iterator != null && position in iterator.indices){
            Log.d("privateroomsflow" , "modified success...")

            iterator[position!!] = message
//
//            val newlist = iterator.toMutableList()
//
//            newlist.set(position!! , message)

            _chat.value = iterator!!

        }
    }

    fun refreshChat(){
        _chat.value = mutableListOf()
    }

}