package com.batuscode.hosbes.models

data class Calls(
    /**
     * Çağrı yapanın bilgileri ...
     * */
    val displayName:String? = null ,
    val photoUrl:String? = null ,
    val uid:String? = null ,
    val type:String? = null ,
    var time:Long? = null ,
    var act:Boolean? = null ,
    var roomId:String? = null

    )