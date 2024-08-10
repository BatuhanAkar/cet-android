package com.batuscode.hosbes.models

data class RandomParticipant(

    val displayName:String? = null ,
    val photoUrl:String? = null ,
    val uid:String? = null ,
    var match:Boolean? = false ,
    var matched:Boolean? = false ,
    var outId:String? = null ,
)