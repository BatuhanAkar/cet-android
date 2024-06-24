package com.batuscode.hosbes.utility

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.PrivateRoom

class PrivateRoomsViewModel: ViewModel() {

    private val _rooms = MutableLiveData<List<PrivateRoom>>(emptyList())
    val rooms: LiveData<List<PrivateRoom>> get()  = _rooms


    fun pushRoom(room: PrivateRoom){

        Log.d("privateroomsflow" , "befor list size :: " + _rooms.value?.size)
        val chatIterator = _rooms.value ?: emptyList()
        val updatedList = chatIterator + room

        Log.d("privateroomsflow" , "after list size :: " + _rooms.value?.size)

        _rooms.value = updatedList

    }

    fun modifiedRoom(room:PrivateRoom){

        var position = _rooms.value?.indexOf(room)
        Log.d("privateroomsflow" , "room name :: " + room.roomName)

        Log.d("privateroomsflow" , "position :: " + position)

        val iterator = _rooms.value



        if (iterator != null && position in iterator.indices){
            Log.d("privateroomsflow" , "modified success...")


            val newlist = iterator.toMutableList()

            newlist.set(position!! , room)

            _rooms.value = newlist

        }





    }

    fun refreshRooms(){
        _rooms.value = emptyList()
    }
}