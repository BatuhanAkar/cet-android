package com.batuscode.hosbes.model

import androidx.annotation.Nullable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.Objects

@IgnoreExtraProperties
data class Message(
    val senderId:String? = null,
    val senderImage:String? = null,
    val senderName:String? = null,
    var message:String? = null,
    val messageId:String? = null,
    val type:String? = null,
    var time:Long? = null,
    var edited:Boolean? = false
    ){


    override fun hashCode(): Int {
        return Objects.hash(messageId , edited)
    }

    override fun equals(@Nullable obj: Any?): Boolean {
        if (obj === this) return true
        if (obj == null || javaClass != obj.javaClass) return false

        val message: Message = obj as Message

        return messageId == message.messageId
    }
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "senderId" to senderId,
            "senderImage" to senderImage,
            "senderName" to senderName,
            "message" to message,
            "messageId" to messageId,
            "type" to type,
            "time" to time,
            "edited" to edited
        )
    }
}
