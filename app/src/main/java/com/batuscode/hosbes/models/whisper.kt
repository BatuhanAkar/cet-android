package com.batuscode.hosbes.models

import androidx.annotation.Nullable
import com.google.firebase.database.Exclude
import java.util.Objects

data class Whisper(
    val wdisplayName:String? = null ,
    val wphotoUrl:String? = null ,
    val wuid:String? = null ,
    val wid:String? = null
    ){

    override fun hashCode(): Int {
        return Objects.hash(wuid)
    }

    override fun equals(@Nullable obj: Any?): Boolean {
        if (obj === this) return true
        if (obj == null || javaClass != obj.javaClass) return false

        val whisper: Whisper = obj as Whisper

        return wuid == whisper.wuid
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "wdisplayName" to wdisplayName ,
            "wphotoUrl" to wphotoUrl ,
            "wuid" to wuid ,
            "wid" to wid
        )
    }
}