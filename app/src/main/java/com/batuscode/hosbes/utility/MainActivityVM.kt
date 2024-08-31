package com.batuscode.hosbes.utility

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batuscode.hosbes.models.Calls
import com.batuscode.hosbes.models.Message
import com.batuscode.hosbes.models.PrivateRoom
import com.batuscode.hosbes.models.RandomParticipant
import com.batuscode.hosbes.models.User
import com.batuscode.hosbes.models.Whisper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivityVM:ViewModel() {


    companion object {
        val self = MainActivityVM()
    }

    private val _loadingChat = MutableStateFlow<Boolean>(false)
    val loadingChat: StateFlow<Boolean?> get() = _loadingChat

    fun updateChatLoading(status: Boolean){
        _loadingChat.value = status
    }

    fun sharedPreference(context:Context , key:String):SharedPreferences{
        return context.getSharedPreferences(key , Context.MODE_PRIVATE)
    }

    fun saveSessionOnPreference(context: Context , key: String , value: Boolean){
        val editor = sharedPreference(context , "session").edit()
        editor.putBoolean(key , value)
        editor.apply()
    }


    fun getSession(context: Context , key: String): Boolean? {
        return sharedPreference(context , key).getBoolean(key , false)
    }

    private val _firstSession = MutableStateFlow<Boolean>(false)
    val firstSession: StateFlow<Boolean?> get() = _firstSession

    fun updatefirstSession(status:Boolean){
        _firstSession.value = status
    }
    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> get() = _photoUrl

    fun updatePhotoUrl(photoUrl:String){

        _photoUrl.value = photoUrl
    }

    private val _uploadComplated = MutableStateFlow<Boolean?>(false)
    val uploadComplated: StateFlow<Boolean?> get() = _uploadComplated

    fun uploadComlated(Status:Boolean){
        _uploadComplated.value = Status
    }
    private val _signUpC = MutableStateFlow<Boolean>(false)
    val signUpC: StateFlow<Boolean?> get() = _signUpC

    fun updateSessionC(status:Boolean){
        _signUpC.value = status
    }

    private val _signUp = MutableStateFlow<Boolean>(false)
    val signUp: StateFlow<Boolean?> get() = _signUp

    fun updateSession(status:Boolean){
        _signUp.value = status
    }

    private val _channelId = MutableStateFlow<String?>(null)
    val channelId: StateFlow<String?> get() = _channelId

    fun connectChannel(channelId:String){
        _channelId.value = channelId
    }

    private val _showMore = MutableStateFlow<Boolean>(false)
    val showMore: StateFlow<Boolean?> get() = _showMore

    fun updateShowMore(state:Boolean){
        _showMore.value = state
    }


    private val _showEditProfileCard = MutableStateFlow<Boolean>(false)
    val showEditProfileCard: StateFlow<Boolean?> get() = _showEditProfileCard

    fun updateShowEditProfileCard(state:Boolean){
        _showEditProfileCard.value = state
    }

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> get() = _username

    fun updateUsername(username:String){
        _username.value = username
    }


    private val _verificationCode = MutableStateFlow<String?>(null)
    val verificationCode: StateFlow<String?> get() = _verificationCode

    fun updateVerificationCode(verificationCode:String){
        _verificationCode.value = verificationCode
    }

    // user profile photo

    private val _Photo = MutableStateFlow<ImageBitmap?>(null)
    val photo: StateFlow<ImageBitmap?> get() = _Photo

    fun updatePhoto(imageBitmap:ImageBitmap){
        _Photo.value = imageBitmap
    }


    private val _newPhoto = MutableStateFlow<Bitmap?>(null)
    val newPhoto: StateFlow<Bitmap?> get() = _newPhoto

    fun updatenewPhoto(bitmap: Bitmap){
        _newPhoto.value = bitmap
    }

    private val _startUpdate = MutableStateFlow<Boolean>(false)
    val startUpdate: StateFlow<Boolean> get() = _startUpdate

    fun updateStartUpdate(status : Boolean){
        _startUpdate.value = status
    }

    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?>  get() = _displayName

    fun updateDisplayName(displayName:String){
        _displayName.value = displayName
    }


    private val _showCreatePrivateRoom = MutableStateFlow<Boolean?>(false)
    val showCreatePrivateRoom: StateFlow<Boolean?>  get() = _showCreatePrivateRoom

    fun updateShowCreatePrivateRoom(status:Boolean){
        _showCreatePrivateRoom.value = status
    }

    private val _newPrivateRoomImage = MutableStateFlow<ImageBitmap?>(null)
    val newPrivateRoomImage: StateFlow<ImageBitmap?> get() = _newPrivateRoomImage

    fun updateNewPrivateRoomImage(image:ImageBitmap){
        _newPrivateRoomImage.value = image
    }

    private val _creatingPrivateRoom = MutableStateFlow<Boolean>(false)
    val creatingPrivateRoom: StateFlow<Boolean> get() = _creatingPrivateRoom

    fun updateCreatingPrivateRoom(status : Boolean){
        _creatingPrivateRoom.value = status
    }

    private val _createPrivateRoom = MutableStateFlow<Boolean>(false)
    val createPrivateRoom: StateFlow<Boolean> get() = _createPrivateRoom

    fun updateCreateRoom(status : Boolean){
        _createPrivateRoom.value = status
    }

    private val _PrivateRoom = MutableStateFlow<PrivateRoom?>(null)
    val privateRoom: StateFlow<PrivateRoom?> get() = _PrivateRoom

    fun updatePrivateRoom(room: PrivateRoom){
        _PrivateRoom.value = room
    }


    private val _chatFlowState = MutableStateFlow<String?>(null)
    val chatFlowState: StateFlow<String?> get() = _chatFlowState

    fun updateChatFlowState(state:String){
        _chatFlowState.value = state
    }

    private val _selectedChannel = MutableStateFlow<String?>(null)
    val selectedChannel: StateFlow<String?> get() = _selectedChannel

    fun updateSelectedChannel(state:String){
        _selectedChannel.value = state
    }

    private val _showPermissionDialog = MutableStateFlow<Boolean?> (false)
    val showPermissionDialog: StateFlow<Boolean?> get() = _showPermissionDialog

    fun updateShowPermissionDialog(state:Boolean){
        _showPermissionDialog.value = state
    }

    private val _privateChatPlaceHolderImage = MutableStateFlow<ImageBitmap?>(null)
    val privateChatPlaceHolderImage: StateFlow<ImageBitmap?> get() = _privateChatPlaceHolderImage

    fun updatePrivateChatPlaceHolderImage(bitmap:ImageBitmap){
        _privateChatPlaceHolderImage.value = bitmap
    }

    private val _messageId = MutableStateFlow<String?> (null)
    val messageId: StateFlow<String?> get() = _messageId

    fun updateMessageId(id:String){
        _messageId.value = id
    }

    private val _prMessageWrited = MutableStateFlow<Boolean?>(false)
    val prMessageWrited: StateFlow<Boolean?> get() = _prMessageWrited

    fun updatePrMessageWrited(state: Boolean){
        _prMessageWrited.value = state
    }

    private val _mediaUri = MutableStateFlow<Uri?>(null)
    val mediaUri: StateFlow<Uri?> get() = _mediaUri

    fun updateMediaUri(uri: Uri){
        _mediaUri.value = uri
    }

    private val _mediaUploaded = MutableStateFlow<Boolean?>(false)
    val mediauploaded:StateFlow<Boolean?> get() = _mediaUploaded

    fun updateMediaUploaded(state: Boolean){
        _mediaUploaded.value = state
    }

    private val _newMediaSended = MutableStateFlow<Boolean?>(false)
    val newMediaSended:StateFlow<Boolean?> get() = _newMediaSended

    fun updateNewMediaSended(state:Boolean){

        _newMediaSended.value = state

    }

    private val _outForSelectImage = MutableStateFlow<Boolean?>(false)
    val outForSelectImage:StateFlow<Boolean?> get() = _outForSelectImage

    fun updateOutForSelectImage(state:Boolean){

        _outForSelectImage.value = state

    }

    private val _mediaMessageProgress = MutableStateFlow<Double?>(null)
    val mediaMessageProgress:StateFlow<Double?> get() = _mediaMessageProgress

    fun updateMediaMessageProgress(value:Double){

        _mediaMessageProgress.value = value

    }

    private val _messageSended = MutableStateFlow<Boolean?>(false)
    val messageSended:StateFlow<Boolean?> get() = _messageSended

    fun updateMessageSended(state:Boolean){
        _messageSended.value = state
    }

    private val _showMessageOption = MutableStateFlow<Boolean?>(false)
    val showMessageOption:StateFlow<Boolean?> get() = _showMessageOption

    fun updateShowMessageOption(state:Boolean){
        _showMessageOption.value = state
    }

    private val _editMessageFlag = MutableStateFlow<Boolean?>(false)
    val editMessageFlag:StateFlow<Boolean?> get() = _editMessageFlag

    fun updateEditMessageFlag(state: Boolean){
        _editMessageFlag.value = state
    }


    private val _editMessageFieldMode = MutableStateFlow<Boolean?>(false)
    val editMessageFieldMode:StateFlow<Boolean?> get() = _editMessageFieldMode

    fun updateEditMessageFieldMode(state: Boolean){
        _editMessageFieldMode.value = state
    }

    private val _message = MutableStateFlow<TextFieldValue?>(TextFieldValue(""))
    val message:StateFlow<TextFieldValue?> get() = _message

    fun updateMessage(value:TextFieldValue){

        _message.value = value

    }

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    fun updateUser(user: User){
        _user.value = user
    }

    private val _whisperUserUid = MutableStateFlow<String?>(null)
    val whisperUserUid: StateFlow<String?> get() = _whisperUserUid

    fun updateWhisperUserUid(uid:String){
        _whisperUserUid.value = uid
    }

    private val _whisperItem = MutableStateFlow<Whisper?>(null)
    val whisperItem:StateFlow<Whisper?> get() = _whisperItem

    fun updateWhisperItem(whisper: Whisper){
        _whisperItem.value = whisper
    }

    private val _whisper = MutableStateFlow<Boolean?>(false)
    val whisper: StateFlow<Boolean?> get() = _whisper

    fun update_whisper(state:Boolean){
        _whisper.value = state
    }


    private val _whisperfirst = MutableStateFlow<Boolean?>(false)
    val whisperfirst: StateFlow<Boolean?> get() = _whisperfirst

    fun updatewhisperfirst(state:Boolean){
        _whisperfirst.value = state
    }

    private val _messageItem = MutableStateFlow<Message?>(null)
    val messageItem: StateFlow<Message?> get() = _messageItem

    fun updateMessageItem(message: Message){
        _messageItem.value = message
    }

    private val _whisperId = MutableStateFlow<String?>(null)
    val whisperId:StateFlow<String?> get() = _whisperId

    fun updateWhisperId(id:String){
        _whisperId.value = id
    }

    private val _inWhisper = MutableStateFlow<Boolean?>(false)
    val inWhisper:StateFlow<Boolean?> get() = _inWhisper

    fun updateInWhisper(state: Boolean){
        _inWhisper.value = state
    }

    private val _inHosbeslerim = MutableStateFlow<Boolean?>(false)
    val inHosbeslerim:StateFlow<Boolean?> get() = _inHosbeslerim

    fun updateInHosbeslerim(state: Boolean){
        _inHosbeslerim.value = state
    }

    private val _showMenu = MutableStateFlow<Boolean?>(false)
    val showMenu:StateFlow<Boolean?> get() = _showMenu

    fun updateShowMenu(state: Boolean){
        _showMenu.value = state
    }

    private val _showRoomInfo = MutableStateFlow<Boolean?>(false)
    val showRoomInfo:StateFlow<Boolean?> get() = _showRoomInfo

    fun updateShowRoomInfo(state:Boolean){
        _showRoomInfo.value = state
    }

    private val _roomExist = MutableStateFlow<Boolean?>(true)
    val roomExist:StateFlow<Boolean?> get() = _roomExist

    fun updateRoomExist(state: Boolean){
        _roomExist.value = state
    }

    private val _loadMoreChat = MutableStateFlow<Boolean?>(false)
    val loadMoreChat:StateFlow<Boolean?> get() = _loadMoreChat

    fun updateLoadMoreChat(state: Boolean){
        _loadMoreChat.value = state
    }

    private val _Incall = MutableStateFlow<Boolean?>(false)
    val incall: StateFlow<Boolean?> get() = _Incall

    fun updateCall(state: Boolean){
        _Incall.value = state
    }
    private val _Historycalls = MutableStateFlow<Calls?>(null)
    val Historycalls: StateFlow<Calls?> get() = _Historycalls

    fun updateHistoryCalls(calls: Calls){
        _Historycalls.value = calls
    }

    private val _isOnline = MutableStateFlow<Boolean?>(null)
    val isOnline:StateFlow<Boolean?> get() = _isOnline

    fun updateIsOnline(status: Boolean){
        _isOnline.value = status
    }




    private val _inVoiceChannel = MutableStateFlow<Boolean?>(false)
    val inVoiceChannel:StateFlow<Boolean?> get() = _inVoiceChannel

    fun updateInVoiceChannel(state: Boolean){
        _inVoiceChannel.value = state
    }

    private val _streamChannelType = MutableStateFlow<String?>(null)
    val streamChannelType:StateFlow<String?> get() = _streamChannelType

    fun updateStreamChannelType(type:String){
        _streamChannelType.value = type
    }

    private val _channelName = MutableLiveData<String?>(null)
    val channelName:LiveData<String?> get() = _channelName

    fun updateChannelName(name:String){
        _channelName.value = name
    }

    private val _profileUpdating = MutableStateFlow<Boolean?>(false)
    val profileUpdating:StateFlow<Boolean?> get() = _profileUpdating

    fun updateProfileUpdating(state: Boolean){
        _profileUpdating.value = state
    }












    private val _d = MutableStateFlow<Boolean??>(false)
    val d:StateFlow<Boolean?> get() = _d

    fun update_d(status: Boolean?){
        _d.value = status
    }

    private val _userVerified = MutableStateFlow<Boolean?>(false)
    val userVerified:StateFlow<Boolean?> get() = _userVerified

    fun updateUserVerified(status: Boolean){
        _userVerified.value = status
    }


}