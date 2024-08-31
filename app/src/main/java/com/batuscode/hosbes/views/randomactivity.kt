package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.models.Participnat
import com.batuscode.hosbes.models.RandomParticipant
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.RandomActivityViewModel
import com.facebook.react.modules.core.PermissionListener
import kotlinx.coroutines.delay
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import org.jitsi.meet.sdk.JitsiMeetView
import timber.log.Timber
import java.net.URL
import java.util.HashMap

class RandomActivity:JitsiMeetActivity(){

    lateinit var view:JitsiMeetView

    lateinit var  prejoinView:ComposeView
    lateinit var swipeScreen:ComposeView
    lateinit var randomconnectionview:ComposeView
    lateinit var context: Context
    lateinit var mrandomActivityViewModel: RandomActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("randomActivity" , "aktivite oluşturuldu ... ")

        val randomActivityViewModel:RandomActivityViewModel by viewModels()
        mrandomActivityViewModel = randomActivityViewModel
        context = this
        view = jitsiView

        var name = MainActivity.PreferenceManager?.getString("displayName")
        var photo = MainActivity.PreferenceManager?.getString("photoUrl")

        val uid = MainActivity.PreferenceManager?.getuidShared("uid")
        MainActivity.fm.addRandomParticipant(uid = uid!! , displayName = name!! , photoUrl = photo!! , randomActivityViewModel) // önce random a kaydet ...
        MainActivity.fm.ListenMatch(uid!! , randomActivityViewModel) // sonra random'ı dinle ...
        prejoinView = ComposeView(this).apply {
            setContent {
                HoşbeşTheme {
                    RandomPreJoinScreen()
                }
            }
        }

        swipeScreen = ComposeView(this).apply {
            setContent {
                HoşbeşTheme {
                    RandomActivityView(randomActivityViewModel = randomActivityViewModel)
                }
            }
        }

        randomconnectionview = ComposeView(this).apply {
            setContent {
                HoşbeşTheme {
                    RandomConnection(randomActivityViewModel = randomActivityViewModel)
                }
            }
        }

        randomActivityViewModel.closeActivity.observe(this , Observer {
            if (it?.equals(true) == true){
                finish()
            }
        })

        randomActivityViewModel.swiped.observe(this , Observer {
            if (it?.equals(true) == true){
                MainActivity.fm.updateMatchRequest(true , uid)
                Log.d("randomActivity" , "ekran kaydırıldı random connection view eklendi ... ")
                view!!.removeView(swipeScreen)
                view!!.addView(randomconnectionview)
            }
        })

        var participant:RandomParticipant? = null

        randomActivityViewModel.liverandomParticipant.observe(this , Observer {

            if (it != null){

                Log.d("matchstat" , "live par observleendi ... " + it?.toString())
                participant = it!!
                randomActivityViewModel.update_xmatched(false)
            } else {

                Log.d("matchstat" , "live par observleendi ... sonuç boş ... ")
            }
        })

        val outhandler = Handler()
        val inhandler = Handler()

        randomActivityViewModel.xmatched.observe(this , Observer {

            Log.d("matchstat" , "karşılaştı observe çalişti ... " + it)
            val tfc = participant?.tfc


            if (tfc == true){


                leave()
                outhandler.postDelayed({
                    view!!.removeView(randomconnectionview)


                    val room = participant?.rm

                    val userinfo = JitsiMeetUserInfo().apply {
                        displayName = name
                        avatar = URL(photo)
                    }

                    var options = JitsiMeetConferenceOptions.Builder()
                        .setRoom("https://meet.recommyz.com/$room")
                        .setUserInfo(userinfo)
                        .build()

                    Log.d("randomActivity" , "prejoin view eklendi ... ")

                    join(options)

                },800)


            } else if (tfc == false){
                inhandler.postDelayed({
                    view!!.removeView(randomconnectionview)
                },800)

            }


        })

        registerForBroadcastMessages()

        val serverURL: URL
        serverURL = URL("https://meet.recommyz.com")
        var defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setFeatureFlag("welcomepage.enabled" , false)
            .setFeatureFlag("prejoinpage.enabled" , false)
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
            .setFeatureFlag("toolbox.enabled" , false)

            .setFeatureFlag("meeting-name.enabled" , false)
            .setFeatureFlag("settings.enabled" , false)


            .setFeatureFlag("invite-dial-in.enabled" , false)
            .setFeatureFlag("server-url-change.enabled" , false)
            .setFeatureFlag("security-options.enabled" , false)


            .setFeatureFlag("welcomepage.enabled" , false)
            .build()

        JitsiMeet.setDefaultConferenceOptions(defaultOptions)


        val userinfo = JitsiMeetUserInfo().apply {
            displayName = name
            avatar = URL(photo)
        }

        var options = JitsiMeetConferenceOptions.Builder()
            .setRoom("https://meet.recommyz.com/$name")
            .setUserInfo(userinfo)
            .build()
        view!!.addView(prejoinView)

        Log.d("randomActivity" , "prejoin view eklendi ... ")
        join(options)

    }

    override fun onParticipantJoined(extraData: HashMap<String, Any>?) {
        super.onParticipantJoined(extraData)
        mrandomActivityViewModel.update_ParticipantJoined(true)
    }
    override fun onConferenceJoined(extraData: HashMap<String, Any>?) {
        super.onConferenceJoined(extraData)
        view!!.removeView(prejoinView)
        view!!.addView(swipeScreen)

        Log.d("randomActivity" , "kendi odasını oluştrudu ... ")
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