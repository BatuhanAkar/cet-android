package com.batuscode.hosbes.utility

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.PrivateRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PrivateRoomsViewModel: ViewModel() {

    private val _rooms = MutableStateFlow<List<PrivateRoom>>(emptyList())
    val rooms: StateFlow<List<PrivateRoom>> get()  = _rooms


    fun pushRoom(room: PrivateRoom){

        Log.d("privateroomsflow" , "room added :: " + room.roomName)

        val roomIter = _rooms.value.toMutableList()
        roomIter.add(room)
        _rooms.value = roomIter

    }

    fun modifiedRoom(room:PrivateRoom){

        val oldRooms = _rooms.value.toMutableList()

        val position = oldRooms.indexOf(room)

        if (position in oldRooms.indices){

            oldRooms[position].activePar = room.activePar

            _rooms.value.get(position).activePar = oldRooms.get(position).activePar

        }

    }

    fun refreshRooms(){
        _rooms.value = emptyList()
    }
}