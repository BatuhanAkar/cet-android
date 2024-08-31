package com.batuscode.hosbes.utility

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.RandomParticipant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RandomActivityViewModel:ViewModel(){
    private val _closeActivity = MutableLiveData<Boolean?>(false)
    val closeActivity : LiveData<Boolean?> get() = _closeActivity

    fun update_closeActivity(value:Boolean){
        _closeActivity.value = value
    }

    private val _swiped = MutableLiveData<Boolean?>(false)
    val swiped : LiveData<Boolean?> get() = _swiped

    fun update_swiped(value:Boolean){
        _swiped.value = value
    }

    private val _randomParticipant = MutableStateFlow<RandomParticipant?>(null)
    val randomParticipant: StateFlow<RandomParticipant?> get() = _randomParticipant

    fun updateRandomParticipant(randomParticipant: RandomParticipant){
        _randomParticipant.value = randomParticipant
    }

    private val _matched = MutableStateFlow<Boolean?>(null)
    val matched:StateFlow<Boolean?> get() = _matched

    fun updateMatched(state: Boolean?){
        _matched.value = state
    }

    private val _xmatched = MutableLiveData<Boolean?>(false)
    val xmatched:LiveData<Boolean?> get() = _xmatched

    fun update_xmatched(value: Boolean){
        _xmatched.value = value
    }

    // katılımcı ekranını ayarlamak için ...
    private val _Tfc = MutableStateFlow<Boolean?>(false)
    val Tfc:StateFlow<Boolean?> get() = _Tfc

    fun updateTfc(state: Boolean?){
        _Tfc.value = state
    }


    private val _tfc = MutableLiveData<Boolean?>(false)
    val tfc:LiveData<Boolean?> get() = _tfc

    fun updateTFC(state: Boolean?){
        _tfc.value = state
    }

    private val _randomParticipantUid = MutableStateFlow<String?>(null)
    val randomParticipantUid:StateFlow<String?> get() = _randomParticipantUid

    fun updateRandomParticipantUid(uid: String?){
        _randomParticipantUid.value = uid
    }

    private val _updatedOutId = MutableStateFlow<Boolean?>(false)
    val updatedOutId:StateFlow<Boolean?> get() = _updatedOutId

    fun updatedOutIdSatus(status: Boolean){
        _updatedOutId.value = status
    }

    private val _c = MutableStateFlow<Boolean?>(false)
    val c:StateFlow<Boolean?> get() = _c

    fun update_c(status: Boolean){
        _c.value = status
    }

    private val _x = MutableStateFlow<Boolean?>(false)
    val x:StateFlow<Boolean?> get() = _x

    fun update_x(status: Boolean){
        _x.value = status
    }

    private val _X = MutableLiveData<Boolean?>(false)
    val X:LiveData<Boolean?> get() = _X

    fun update_X(status: Boolean){
        _X.value = status
    }

    private val _liverandomParticipant = MutableLiveData<RandomParticipant?>(null)
    val liverandomParticipant:LiveData<RandomParticipant?> get() = _liverandomParticipant

    fun updateliveRandomParticipant(liverandomParticipant: RandomParticipant){
        _liverandomParticipant.value = liverandomParticipant
    }

    private val _ParticipantJoined = MutableLiveData<Boolean>(false)
    val ParticipantJoined:LiveData<Boolean> get() = _ParticipantJoined

    fun update_ParticipantJoined(value:Boolean){
        _ParticipantJoined.value = value
    }

}