package com.batuscode.hosbes.model

import androidx.annotation.Nullable
import java.util.Objects


data class PrivateRoom(
    val roomName:String? = null ,
    val roomId:String? = null ,
    val photoUrl:String? = null ,
    val parCount:Long = 0 ,
    var activePar:Long = 0 ,
    val ownerId: String? = null
) {


    override fun hashCode(): Int {
        return Objects.hash(roomId)
    }

    override fun equals(@Nullable obj: Any?): Boolean {
        if (obj === this) return true
        if (obj == null || javaClass != obj.javaClass) return false

        val room: PrivateRoom = obj as PrivateRoom

        return roomId == room.roomId
    }

}