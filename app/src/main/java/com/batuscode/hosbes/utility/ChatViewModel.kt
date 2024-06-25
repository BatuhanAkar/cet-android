package com.batuscode.hosbes.utility

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel: ViewModel() {

    private val _chats = MutableStateFlow<List<Message>>(emptyList())
    val chat:StateFlow<List<Message>> get() = _chats


    fun pushChat(message: Message){

        val chatIterator = _chats.value ?: emptyList()
        val updatedChat = chatIterator + message

        _chats.value = updatedChat

//        val current = _chats.value
//
//        val lastIndex = current + message
//
//        _chats.value = lastIndex
    }

    fun messageChanged(message: Message){
        val position = _chats.value?.indexOf(message)

        val iterator = _chats.value


        if (position in iterator.indices){

            val newList = iterator.toMutableList()


            newList.set(position!! , message)

            val changedmessage = newList.get(position)

            Log.d("jokermessage" , changedmessage.message!!)


            _chats.value = newList



        }
    }

    fun refreshChat(){

        _chats.value = emptyList()

    }

}