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



    private val _ParticipantJoined = MutableLiveData<Boolean>(false)
    val ParticipantJoined:LiveData<Boolean> get() = _ParticipantJoined

    fun update_ParticipantJoined(value:Boolean){
        _ParticipantJoined.value = value
    }

    private val _fromLobby = MutableLiveData<Boolean?>(null)
    val fromLobby:LiveData<Boolean?> get() = _fromLobby

    fun update_fromLobby(value: Boolean){
        _fromLobby.value = value
    }

    private val _AudioMute = MutableLiveData<Boolean>(false)
    val AudioMute : LiveData<Boolean> get() = _AudioMute

    fun update_AudioMute(value: Boolean){
        _AudioMute.value = value
    }

    private val _VideoMute = MutableLiveData<Boolean>(false)
    val VideoMute : LiveData<Boolean> get() = _VideoMute

    fun update_VideoMute(value: Boolean){
        _VideoMute.value = value
    }

    private val _hangup = MutableLiveData<Boolean>(false)
    val hangup : LiveData<Boolean> get() = _hangup

    fun update_hangup(value: Boolean){
        _hangup.value = value
    }

    private val _flipCamera = MutableLiveData<Boolean>(false)
    val flipCamera : LiveData<Boolean> get() = _flipCamera

    fun  update_flipCamera(value: Boolean){
        _flipCamera.value = value
    }

    private val _changeMatch = MutableLiveData<Boolean>(false)
    val changeMatch : LiveData<Boolean> get() = _changeMatch

    fun update_changeMatch(value: Boolean){
        _changeMatch.value = value
    }

    private val _session = MutableLiveData<String>("first")
    val session : LiveData<String> get() = _session

    fun update_session(value: String){
        _session.value = value
    }

    private val _countTimerComplated = MutableLiveData<Boolean>(false)
    val countTimerComplated : LiveData<Boolean> get() = _countTimerComplated

    fun update_countTimerComplated(value: Boolean){
        _countTimerComplated.value = value
    }

}