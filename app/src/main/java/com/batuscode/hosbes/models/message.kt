package com.batuscode.hosbes.models

import androidx.annotation.Nullable
import com.google.firebase.database.IgnoreExtraProperties
import java.util.Objects

@IgnoreExtraProperties
data class Message(
    val senderId:String? = null ,
    val senderImage:String? = null ,
    val senderName:String? = null ,
    val message:String? = null ,
    val messageId:String? = null ,
    val type:String? = null ,
    val time:Long? = null
    ){


    override fun hashCode(): Int {
        return Objects.hash(messageId)
    }

    override fun equals(@Nullable obj: Any?): Boolean {
        if (obj === this) return true
        if (obj == null || javaClass != obj.javaClass) return false

        val message: Message = obj as Message

        return messageId == message.messageId
    }

}
