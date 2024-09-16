package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.models.Participnat
import com.batuscode.hosbes.viewmodel.InCallActivityViewModel
import com.facebook.react.modules.core.PermissionListener
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.BroadcastIntentHelper
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import timber.log.Timber
import java.net.URL
import java.util.HashMap

class InCallActivity:JitsiMeetActivity(){
    var wuid:String = ""
    var wphotoUrl:String = ""
    var wdisplayName:String = ""
    lateinit var minCallActivityViewModel: InCallActivityViewModel
    private lateinit var callHistoryItem: Participnat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = jitsiView

        registerForBroadcastMessages()

        wuid = intent.getStringExtra("wuid").toString()
        wphotoUrl = intent.getStringExtra("wphotoUrl").toString()
        wdisplayName = intent.getStringExtra("wdisplayName").toString()

        val inCallActivityViewModel: InCallActivityViewModel by viewModels()
        minCallActivityViewModel = inCallActivityViewModel

        callHistoryItem = Participnat(
            displayName = wdisplayName ,
            photoUrl = wphotoUrl ,
            uid = wuid
        )
        val ownerId = MainActivity.PreferenceManager?.getString("uid")
        MainActivity.fm.callrequest(ownerId = ownerId!! , uid = wuid!! , displayName = wdisplayName!! , photoUrl = wphotoUrl!! , inCallActivityViewModel = inCallActivityViewModel)

        inCallActivityViewModel.requestCall.observe(this , Observer {
            if (it?.equals(false) == true){
                MainActivity.fm.calling(wuid)
            } else if (it?.equals(true) == true){
                finish()
            }

        })

        val WuserInfo = JitsiMeetUserInfo().apply {
            displayName = wdisplayName
            avatar = URL(wphotoUrl)
        }

        val serverURL: URL
        serverURL = URL("https://meet.recommyz.com")
        var defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setFeatureFlag("welcomepage.enabled" , false)
            .build()

        JitsiMeet.setDefaultConferenceOptions(defaultOptions)


        val options = JitsiMeetConferenceOptions.Builder()
            .setUserInfo(WuserInfo)
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
            .setFeatureFlag("fullscreen.enabled" , false)
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
            .setFeatureFlag("call-integration.enabled" , true)
            .setFeatureFlag("invite-dial-in.enabled" , false)
            .setFeatureFlag("server-url-change.enabled" , false)
            .setFeatureFlag("security-options.enabled" , false)


            .setFeatureFlag("welcomepage.enabled" , false)

            .build()

        val composeView = ComposeView(this).apply {
            setContent {
                CallScreen( callHistoryItem , inCallActivityViewModel = inCallActivityViewModel , "calling")
            }
        }

        view!!.addView(composeView)
        join(options)


        inCallActivityViewModel.WcallMuteAudio.observe(this , Observer {
            if (it == true){
                HandleMuteAudioBroadcastAction(it)
            } else if (it == false){
                HandleMuteAudioBroadcastAction(it)
            }
        })

        inCallActivityViewModel.WcallHangUp.observe(this , Observer {
            HandleHangUpBroadcastAction()
        })

    }


    private fun HandleHangUpBroadcastAction(){
        val intent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun HandleMuteAudioBroadcastAction(value: Boolean){
        val intent = BroadcastIntentHelper.buildSetAudioMutedIntent(value)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

    }
    override fun onConferenceJoined(extraData: HashMap<String, Any>?) {
        super.onConferenceJoined(extraData)
        minCallActivityViewModel.updateWillJoin(true)
    }

    override fun onParticipantJoined(extraData: HashMap<String, Any>?) {
        super.onParticipantJoined(extraData)
        minCallActivityViewModel.updateWillJoin(false)

        val displayname = extraData?.toString()


        Log.d("extraDataForjoined" , displayname!!.toString())
    }

    override fun onParticipantLeft(extraData: HashMap<String, Any>?) {
        super.onParticipantLeft(extraData)
        HandleHangUpBroadcastAction()
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
        intentFilter.addAction(BroadcastEvent.Type.PARTICIPANT_JOINED.action)
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
        val ownerId = MainActivity.PreferenceManager?.getString("uid")
        MainActivity.fm.declineCall(ownerId!! , wuid)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        JitsiMeetActivityDelegate.onHostDestroy(this)

    }

    override fun onStop() {
        super.onStop()
        val ownerId = MainActivity.PreferenceManager?.getString("uid")
        MainActivity.fm.declineCall(ownerId!! , wuid)
        JitsiMeetActivityDelegate.onHostDestroy(this)
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