package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.utility.VoiceChannelViewModel
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

class VoiceChannel:JitsiMeetActivity() , JitsiMeetActivityInterface{

    lateinit var view: JitsiMeetView
    lateinit var mainActivityVM: MainActivityVM
    lateinit var corridorView: ComposeView
    lateinit var voicechannelmeetscreen: ComposeView

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
        view!!.addView(voicechannelmeetscreen)
    }
    override fun onParticipantLeft(extraData: HashMap<String, Any>?) {
        super.onParticipantLeft(extraData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val voiceChannelViewModel:VoiceChannelViewModel by viewModels()



        mainActivityVM = MainActivity.mMainActivityVM
        view = jitsiView

        corridorView = ComposeView(this).apply {
            setContent {
                ConnectionCorridor(mainActivityVM = mainActivityVM)
            }
        }
        
        voicechannelmeetscreen = ComposeView(this).apply { 
            setContent { 
                HoşbeşTheme {
                    VoiceChannelMeetScreen(voiceChannelViewModel = voiceChannelViewModel)
                }
            }
        }

        view!!.addView(corridorView)

        val serverURL: URL
        serverURL = URL("https://meet.recommyz.com")
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
                .setRoom("https://meet.recommyz.com/$name")
                .setUserInfo(userinfo)
                .setAudioOnly(true)
                .setAudioMuted(false)
                .setVideoMuted(true)
                .setFeatureFlag("video-mute.enabled" , false)
                .setFeatureFlag("video-share.enabled" , false)

                .setFeatureFlag("everyoneIsModerator" , false)
                .setFeatureFlag("tile-view.enabled" , true)

                .setFeatureFlag("prejoinpage.enabled" , false)

                .setFeatureFlag("invite.enabled" , false)

                .setFeatureFlag("add-people.enabled" , false)

                .setFeatureFlag("speakerstats.enabled" , true)

                .setFeatureFlag("car-mode.enabled" , false)

                .setFeatureFlag("close-captions.enabled" , false)

                .setFeatureFlag("help.enabled" , false)

                .setFeatureFlag("ios.screensharing.enabled" , false)

                .setFeatureFlag("ios.recording.enabled" , false)

                .setFeatureFlag("android.screensharing.enabled" , false)

                .setFeatureFlag("recording.enabled" , false)

                .setFeatureFlag("overflow-menu.enabled" , false)

                .setFeatureFlag("pip-while-screensharing.enabled" , false)

                .setFeatureFlag("lobby-mode.enabled" , false)

                .setFeatureFlag("meeting-password.enabled" , false)

                .setFeatureFlag("kick-out.enabled" , false)

                .setFeatureFlag("breakout-rooms.enabled" , false)

                .setFeatureFlag("settings.enabled" , false)

                .setFeatureFlag("filmstrip.enabled" , true)


                .setFeatureFlag("disableSimulcast" , false)
                .setFeatureFlag("disableAEC" , false)
                .setFeatureFlag("disableNS" , false)
                .setFeatureFlag("disableAGC" , false)
                .setFeatureFlag("fullscreen.enabled" , true)
                .setFeatureFlag("end-conference.enabled" , false)

                .setFeatureFlag("enableLayerSuspension" , true)

                .setFeatureFlag("toolbox.alwaysVisible" , false)
                .setFeatureFlag("toolbox.enabled" , false)

                .setFeatureFlag("unsaferoomwarning.enabled" , false)

                .setFeatureFlag("replace.participant" , true)

                .setFeatureFlag("invite-dial-in.enabled" , false)

                .setFeatureFlag("live-streaming.enabled" , false)

                .setFeatureFlag("server-url-change.enabled" , false)

                .setFeatureFlag("security-options.enabled" , false)


                .setFeatureFlag("welcomepage.enabled" , false)
                .build()

            join(options)

            voiceChannelViewModel.voicechannelVideoMute.observe(this , Observer {
                if (it == true){
                    HandleVoiceChannelCameraBroadCastAction(it)
                } else if (it == false){
                    HandleVoiceChannelCameraBroadCastAction(it)
                }
            })

            voiceChannelViewModel.voicechannelAudioMute.observe(this , Observer {
                if (it == true){
                    HandleVoiceChannelAudioBroadcastAction(it)
                } else if (it == false){
                    HandleVoiceChannelAudioBroadcastAction(it)
                }
            })

            voiceChannelViewModel.voicechannelhangup.observe(this , Observer {
                if (it == true){
                    mainActivityVM.update_VoiceChannelRefused(true)
                    voiceChannelViewModel.update_voicechannelhangup(false)
                    HandleVoiceChannelHangUpBroadCastAction()
                }
            })

        })


    }
    private fun HandleVoiceChannelAudioBroadcastAction(value:Boolean){
        val muteBroadcastActionIntent = BroadcastIntentHelper.buildSetAudioMutedIntent(value)
        LocalBroadcastManager.getInstance(this).sendBroadcast(muteBroadcastActionIntent)
    }

    private fun HandleVoiceChannelCameraBroadCastAction(value: Boolean){

        val muteBroadcastActionIntent = BroadcastIntentHelper.buildSetVideoMutedIntent(value)
        LocalBroadcastManager.getInstance(this).sendBroadcast(muteBroadcastActionIntent)

    }

    private fun HandleVoiceChannelHangUpBroadCastAction(){
        val muteBroadcastActionIntent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(this).sendBroadcast(muteBroadcastActionIntent)
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