package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.Calls
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.VoiceCallsViewModel
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.react.modules.core.PermissionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.BroadcastIntentHelper
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate
import org.jitsi.meet.sdk.JitsiMeetActivityInterface
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import org.jitsi.meet.sdk.JitsiMeetView
import timber.log.Timber
import java.net.URL
import java.util.HashMap
import kotlin.properties.Delegates

class VoiceCalls:JitsiMeetActivity(), JitsiMeetActivityInterface{

    private fun hangUp(){
        val hangUpBroadcastIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(this.applicationContext).sendBroadcast(hangUpBroadcastIntent)
    }

    override fun onConferenceJoined(extraData: HashMap<String, Any>?) {
        super.onConferenceJoined(extraData)
    }

    override fun onConferenceTerminated(extraData: HashMap<String, Any>?) {
        super.onConferenceTerminated(extraData)
        finish()
    }

    override fun onParticipantLeft(extraData: HashMap<String, Any>?) {
        super.onParticipantLeft(extraData)
        hangUp()
    }


    val mainActivityVM = MainActivity.mMainActivityVM

    val voiceCallsViewModel:VoiceCallsViewModel by viewModels()

    var handler = Handler(Looper.getMainLooper())
    val ownerId = MainActivity.PreferenceManager?.getuidShared("uid")
    lateinit var uid:String // karşı tarafın id'si
    lateinit var photoUrl:String
    lateinit var WdisplayName:String
    lateinit var type:String
    lateinit var roomId:String
    var answered by mutableStateOf(false)

    lateinit var view:JitsiMeetView

    companion object{
        lateinit var VoiceCallsContext:Context
    }

    private fun onBroadcastReceived(intent: Intent?){
        if (intent != null){
            val event = BroadcastEvent(intent)
            when(event.type){
                BroadcastEvent.Type.CONFERENCE_JOINED -> Timber.i("Conference joined with url%s" , event.data.get("url"))
                BroadcastEvent.Type.PARTICIPANT_JOINED -> Timber.i("Participant joined%s" , event.data.get("name"))
                else -> Timber.i("Received event: %s" , event.type)
            }
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            onBroadcastReceived(intent)
        }

    }




    private fun registerForBroadcastMessages(){
        val intentFilter = IntentFilter()

        intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.action)
        intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.action)

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver , intentFilter)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        JitsiMeetActivityDelegate.onActivityResult(this,requestCode, resultCode, data)
    }

    override fun requestPermissions(p0: Array<out String>?, p1: Int, p2: PermissionListener?) {
        JitsiMeetActivityDelegate.requestPermissions(this , p0 , p1 , p2)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.fm.declineCall(ownerId!! , uid)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        JitsiMeetActivityDelegate.onHostDestroy(this)
        MainActivity.fm.detachCallsListener()

    }

    override fun onStop() {
        super.onStop()
        MainActivity.fm.declineCall(ownerId!! , uid)
        JitsiMeetActivityDelegate.onHostDestroy(this)
        MainActivity.fm.detachCallsListener()
    }



    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        view = jitsiView // jitsi aktivitesinin görünümünü al ...
        VoiceCallsContext = this
        val VoiceCallsIntent = intent
        uid = VoiceCallsIntent.getStringExtra("uid").toString()
        photoUrl = VoiceCallsIntent.getStringExtra("photoUrl").toString()
        WdisplayName = VoiceCallsIntent.getStringExtra("displayName").toString()
        type = VoiceCallsIntent.getStringExtra("type").toString()
        roomId = VoiceCallsIntent.getStringExtra("roomId").toString()

        mainActivityVM.endCall.observe(this , Observer { endCall ->
            if (endCall == true){
                mainActivityVM.updateShowEndedCallText(true)
                handler.postDelayed({
                    finish()
                } , 500)
            } else {

            }
        })

        val serverURL: URL
        serverURL = URL("https://recommyz.com")
        var defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setFeatureFlag("welcomepage.enabled" , false)
            .build()

        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        registerForBroadcastMessages()

        /***
         * arama yapan kişi kendi aramalarını dinleyecek ... arama durumu önemlisi ... bide kişi bilgiler çekilecek ...
         */


        enableEdgeToEdge()



        if (type.equals("calling")){
            val WuserInfo = JitsiMeetUserInfo().apply {
                displayName = photoUrl
            }

            val options = JitsiMeetConferenceOptions.Builder()
                .setUserInfo(WuserInfo)
                .setServerURL(URL("https://recommyz.com"))
                .setRoom(ownerId)
                .setAudioOnly(true)
                .setAudioMuted(false)
                .setVideoMuted(true)
                .setFeatureFlag("prejoinpage.enabled" , false)

                .setFeatureFlag("invite.enabled" , false)
                .setFeatureFlag("chat.enabled" , false)
                .setFeatureFlag("add-people.enabled" , false)
                .setFeatureFlag("car-mode.enabled" , false)
                .setFeatureFlag("close-captions.enabled" , false)
                .setFeatureFlag("help.enabled" , false)
                .setFeatureFlag("ios.screensharing.enabled" , false)
                .setFeatureFlag("ios.recording.enabled" , false)
                .setFeatureFlag("android.screensharing.enabled" , false)
                .setFeatureFlag("video-mute.enabled" , false)
                .setFeatureFlag("video-share.enabled" , false)
                .setFeatureFlag("overflow-menu.enabled" , false)
                .setFeatureFlag("participants.enabled" , false)
                .setFeatureFlag("pip.enabled" , false)
                .setFeatureFlag("notifications.enabled" , false)
                .setFeatureFlag("pip-while-screensharing.enabled" , false)
                .setFeatureFlag("meeting-password.enabled" , false)
                .setFeatureFlag("kick-out.enabled" , false)

                .setFeatureFlag("meeting-name.enabled" , false)
                .setFeatureFlag("lobby-mode.enabled" , false)
                .setFeatureFlag("replace.participant" , false)
                .setFeatureFlag("settings.enabled" , false)
                .setFeatureFlag("title-view.enabled" , false)


                .setFeatureFlag("filmstrip.enabled" , false)
                .setFeatureFlag("call-integration.enabled" , false)
                .setFeatureFlag("invite-dial-in.enabled" , false)
                .setFeatureFlag("server-url-change.enabled" , false)
                .setFeatureFlag("security-options.enabled" , false)


                .setFeatureFlag("welcomepage.enabled" , false)

                .build()

            join(options)

            val composeView = ComposeView(this).apply {
                setContent {
                    CallScreen(mainActivityVM , "calling")
                }
            }

            view!!.addView(composeView)


        }
        else if (type.equals("answered")){

            val callOwnerName = MainActivity.PreferenceManager?.getuidShared("callOwnerName")

            val roomName: String
            roomName = "https://recommyz.com/$roomId"

            val OwuserInfo = JitsiMeetUserInfo().apply {
                displayName = callOwnerName
            }

            var options = JitsiMeetConferenceOptions.Builder()
                .setUserInfo(OwuserInfo)
                .setRoom(roomName)
                .setAudioOnly(true)
                .setAudioMuted(false)
                .setVideoMuted(true)
                .setFeatureFlag("prejoinpage.enabled" , false)
                .setFeatureFlag("welcomepage.enabled" , false)


                .setFeatureFlag("invite.enabled" , false)
                .setFeatureFlag("chat.enabled" , false)
                .setFeatureFlag("add-people.enabled" , false)
                .setFeatureFlag("car-mode.enabled" , false)
                .setFeatureFlag("close-captions.enabled" , false)
                .setFeatureFlag("help.enabled" , false)
                .setFeatureFlag("ios.screensharing.enabled" , false)
                .setFeatureFlag("ios.recording.enabled" , false)
                .setFeatureFlag("android.screensharing.enabled" , false)
                .setFeatureFlag("video-mute.enabled" , false)
                .setFeatureFlag("video-share.enabled" , false)
                .setFeatureFlag("overflow-menu.enabled" , false)
                .setFeatureFlag("participants.enabled" , false)
                .setFeatureFlag("pip.enabled" , false)
                .setFeatureFlag("notifications.enabled" , false)
                .setFeatureFlag("pip-while-screensharing.enabled" , false)
                .setFeatureFlag("meeting-password.enabled" , false)
                .setFeatureFlag("kick-out.enabled" , false)


                .setFeatureFlag("meeting-name.enabled" , false)
                .setFeatureFlag("lobby-mode.enabled" , false)
                .setFeatureFlag("replace.participant" , false)
                .setFeatureFlag("settings.enabled" , false)
                .setFeatureFlag("title-view.enabled" , false)

                .setFeatureFlag("filmstrip.enabled" , false)
                .setFeatureFlag("call-integration.enabled" , false)
                .setFeatureFlag("invite-dial-in.enabled" , false)
                .setFeatureFlag("server-url-change.enabled" , false)
                .setFeatureFlag("security-options.enabled" , false)



                .build()

            join(options)

            val composeView = ComposeView(this).apply {
                setContent {
                    CallScreen(mainActivityVM , "answered")
                }
            }

            view!!.addView(composeView)

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        JitsiMeetActivityDelegate.onBackPressed()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        JitsiMeetActivityDelegate.onNewIntent(intent)
    }




}