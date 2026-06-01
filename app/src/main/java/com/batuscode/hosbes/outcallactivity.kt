package com.batuscode.hosbes

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batuscode.hosbes.model.Participnat
import com.batuscode.hosbes.view.ICC
import com.batuscode.hosbes.view.OutCallMeetScreen
import com.batuscode.hosbes.viewmodel.OutCallActivityViewModel
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

class OutCallActivity:JitsiMeetActivity(){

    private lateinit var callHistoryItem: Participnat
    private lateinit var mOutCallActivityViewModel: OutCallActivityViewModel
    private lateinit var wuid:String

    lateinit var outcallmeetscreen:ComposeView
    lateinit var incomingcallscreen:ComposeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val outCallActivityViewModel: OutCallActivityViewModel by viewModels()
        mOutCallActivityViewModel = outCallActivityViewModel
        registerForBroadcastMessages()
        val view = jitsiView

        wuid = intent.getStringExtra("wuid").toString()
        val wphotoUrl = intent.getStringExtra("wphotoUrl")
        val wdisplayName = intent.getStringExtra("wdisplayName")


        callHistoryItem = Participnat(
            displayName = wdisplayName ,
            photoUrl = wphotoUrl ,
            uid = wuid
        )


        incomingcallscreen = ComposeView(this).apply {
            setContent {
                ICC(outCallActivityViewModel = mOutCallActivityViewModel , Historycalls = callHistoryItem)
            }
        }

        outcallmeetscreen = ComposeView(this).apply {
            setContent {
                OutCallMeetScreen(outCallActivityViewModel = outCallActivityViewModel , historyCalls = callHistoryItem)
            }
        }

        view!!.addView(incomingcallscreen)

        val serverURL: URL
        serverURL = URL("https://meet.recommyz.com")
        var defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setFeatureFlag("welcomepage.enabled" , false)
            .build()

        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        val WuserInfo = JitsiMeetUserInfo().apply {
            displayName = wdisplayName
            avatar = URL(wphotoUrl)
        }

        val options = JitsiMeetConferenceOptions.Builder()
            .setUserInfo(WuserInfo)
            .setRoom(wuid)
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


        outCallActivityViewModel.Ljoin.observe(this , Observer {
            if (it?.equals(true) == true){
                view!!.removeView(incomingcallscreen)
                view!!.addView(outcallmeetscreen)
                join(options)

            } else if (it?.equals(false) == true) {
                finish()

            }

        })

        outCallActivityViewModel.WcallMuteAudio.observe(this , Observer {
            if (it == true){
                HandleMuteAudioBroadcastAction(it)
            } else if (it == false){
                HandleMuteAudioBroadcastAction(it)
            }
        })

        outCallActivityViewModel.WcallHangUp.observe(this , Observer {
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

    override fun onConferenceWillJoin(extraData: HashMap<String, Any>?) {
        super.onConferenceWillJoin(extraData)
        mOutCallActivityViewModel.updateWillJoin(true)
    }

    override fun onConferenceJoined(extraData: HashMap<String, Any>?) {
        super.onConferenceJoined(extraData)
        mOutCallActivityViewModel.updateWillJoin(false)
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
        val ownerId = MainActivity.PreferenceManager?.getuidShared("uid")
        MainActivity.fm.declineCall(ownerId!! , wuid)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        JitsiMeetActivityDelegate.onHostDestroy(this)

    }

    override fun onStop() {
        super.onStop()
        val ownerId = MainActivity.PreferenceManager?.getuidShared("uid")
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