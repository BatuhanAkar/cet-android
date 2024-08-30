package com.batuscode.hosbes.models

data class User(
    val displayName:String? = null ,
    val photoUrl:String? = null ,
    val uid:String? = null ,
    @field:JvmField val isOnline:Boolean? = null ,
    @field:JvmField val inCall:Boolean? = false

)
