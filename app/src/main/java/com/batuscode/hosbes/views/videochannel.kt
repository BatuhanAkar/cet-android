package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.utility.MainActivityVM
import com.facebook.react.modules.core.PermissionListener
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

class VideoChannel:JitsiMeetActivity() , JitsiMeetActivityInterface{

    lateinit var view:JitsiMeetView
    lateinit var mainActivityVM: MainActivityVM
    lateinit var corridorView:ComposeView
    var roomName by mutableStateOf("")

    private fun hangUp(){
        val hangUpBroadcastIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(this.applicationContext).sendBroadcast(hangUpBroadcastIntent)
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
    override fun onConferenceJoined(extraData: HashMap<String, Any>?) {
        super.onConferenceJoined(extraData)
        view!!.removeView(corridorView!!)
    }
    override fun onParticipantLeft(extraData: HashMap<String, Any>?) {
        super.onParticipantLeft(extraData)
        hangUp()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityVM = MainActivity.mMainActivityVM
        view = jitsiView

        corridorView = ComposeView(this).apply {
            setContent { 
                ConnectionCorridor(mainActivityVM = mainActivityVM)
            }
        }

        view!!.addView(corridorView)

        val serverURL: URL
        serverURL = URL("https://recommyz.com")
        var defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)

            .build()

        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        registerForBroadcastMessages()

        var name = MainActivity.PreferenceManager?.getString("displayName")
        var photo = MainActivity.PreferenceManager?.getString("photoUrl")

        val userinfo = JitsiMeetUserInfo().apply {
            displayName = name
            avatar = URL(photo)
        }

        mainActivityVM.channelName.observe(this , Observer {
                name ->



            var options = JitsiMeetConferenceOptions.Builder()
                .setRoom("https://recommyz.com/$name")
                .setUserInfo(userinfo)
                .setFeatureFlag("tile-view.enabled" , true)
                .setFeatureFlag("prejoinpage.enabled" , false)
                .setFeatureFlag("invite.enabled" , false)
                .setFeatureFlag("add-people.enabled" , false)
                .setFeatureFlag("car-mode.enabled" , false)
                .setFeatureFlag("close-captions.enabled" , false)
                .setFeatureFlag("help.enabled" , false)
                .setFeatureFlag("ios.screensharing.enabled" , false)
                .setFeatureFlag("ios.recording.enabled" , false)
                .setFeatureFlag("android.screensharing.enabled" , false)
                .setFeatureFlag("overflow-menu.enabled" , false)
                .setFeatureFlag("pip-while-screensharing.enabled" , false)
                .setFeatureFlag("meeting-password.enabled" , false)
                .setFeatureFlag("kick-out.enabled" , false)

                .setFeatureFlag("meeting-name.enabled" , false)
                .setFeatureFlag("settings.enabled" , false)


                .setFeatureFlag("invite-dial-in.enabled" , false)
                .setFeatureFlag("server-url-change.enabled" , false)
                .setFeatureFlag("security-options.enabled" , false)


                .setFeatureFlag("welcomepage.enabled" , false)
                .build()

            join(options)

        })


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

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        JitsiMeetActivityDelegate.onHostDestroy(this)

    }

    override fun onStop() {
        super.onStop()
        JitsiMeetActivityDelegate.onHostDestroy(this)
    }

}