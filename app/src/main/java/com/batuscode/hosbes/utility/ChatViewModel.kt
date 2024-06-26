package com.batuscode.hosbes.utility

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuscode.hosbes.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {

    private val _chats = MutableStateFlow<List<Message>>(emptyList())
    val chat:StateFlow<List<Message>> get() = _chats

    private val _messageItem = MutableStateFlow<Message?>(null)
    val messageItem: StateFlow<Message?> get() = _messageItem

    fun updateMessageItem(message: Message){
        _messageItem.value = message
    }

    fun pushChat(message: Message){

        val chatIterator = _chats.value.toMutableList()

        chatIterator.add(message)

        var position = chatIterator.indexOf(message)

        Log.d("jokermessage" , "eklenen öğenin pozisyonu :: " + position)

//        val updatedChat = chatIterator + message

        _chats.value = chatIterator

        var realposition = _chats.value.indexOf(message)
        Log.d("jokermessage" , "eklenen öğenin gerçek pozisyonu :: " + realposition)

        Log.d("jokermessage" , "yeni mesaj eklenmeden sonraki boyut :: " + _chats.value?.size)

//        val current = _chats.value
//
//        val lastIndex = current + message
//
//        _chats.value = lastIndex
    }

    fun messageChanged(message: Message){



        val oldChats = _chats.value.toMutableList()



        val position = oldChats.indexOf(messageItem.value)
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

            _chats.value.get(position).message = oldChats.get(position).message
            _chats.value.get(position).edited = oldChats.get(position).edited
            _chats.value.get(position).time = oldChats.get(position).time



            val esas = _chats.value?.get(position)
            Log.d("jokermessage" , "esas :: " + esas?.message!!)

        } else {
            Log.d("jokermessage" , "pozisyon aktif değil ...")

        }


    }

    fun refreshChat(){

        _chats.value = emptyList()

    }

}