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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.models.Participnat
import com.batuscode.hosbes.models.RandomParticipant
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.RandomActivityViewModel
import com.facebook.react.ReactInstanceManager
import com.facebook.react.modules.core.PermissionListener
import com.th3rdwave.safeareacontext.getReactContext
import kotlinx.coroutines.delay
import org.jitsi.meet.sdk.BroadcastAction
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

class RandomActivity:JitsiMeetActivity() , JitsiMeetActivityInterface{

    var session by mutableStateOf("")
    lateinit var view:JitsiMeetView

    lateinit var  prejoinView:ComposeView
    lateinit var swipeScreen:ComposeView
    lateinit var randomconnectionview:ComposeView
    lateinit var randomactivitymeetscreen:ComposeView
    lateinit var context: Context
    lateinit var mrandomActivityViewModel: RandomActivityViewModel
    lateinit var mUid: String

    var fromIn by mutableStateOf(false)

    var matchDefeat by mutableStateOf(false)

    var _hangUp by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        Log.d("randomActivity" , "aktivite oluşturuldu ... ")

        val randomActivityViewModel:RandomActivityViewModel by viewModels()

        randomActivityViewModel.session.observe(this , Observer {
            session = it
        })

        mrandomActivityViewModel = randomActivityViewModel
        context = this
        view = jitsiView

        MainActivity.mMainActivityVM.update_inRandom(true)
        var name = MainActivity.PreferenceManager?.getString("displayName")
        var photo = MainActivity.PreferenceManager?.getString("photoUrl")

        val uid = MainActivity.PreferenceManager?.getuidShared("uid")
        mUid = uid!!
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


        randomactivitymeetscreen = ComposeView(this).apply {
            setContent {
                HoşbeşTheme {
                    RandomActivityMeetScreen(randomActivityViewModel = randomActivityViewModel)
                }
            }
        }

        /**
         * KAYDIRMA ÖNCESİNDEKİ RASTGELEDEN ÇIKMA OLAYI ...
         * */
        randomActivityViewModel.closeActivity.observe(this , Observer {
            if (it?.equals(true) == true){
                finish()
            }
        })


        /**
         * İLK SWİPE OLAYI ...
         * */
        randomActivityViewModel.swiped.observe(this , Observer {
            if (it?.equals(true) == true){
                MainActivity.fm.updateMatchRequest(true , uid)
                Log.d("randomActivity" , "ekran kaydırıldı random connection view eklendi ... ")
                randomActivityViewModel.update_session("next")
                view!!.removeView(swipeScreen)
                view!!.addView(randomconnectionview)
            }
        })

        var participant:RandomParticipant? = null


        var tfc:Boolean? = null
        val outhandler = Handler()
        val inhandler = Handler()

        /**
         * KARŞILAŞMA SONUCUNDA LISTEN MATCH DEN TETİKLENEREK GEÇMİŞTEKİ SON KİŞİ GELMİŞTİR ...
         * */

        randomActivityViewModel.liverandomParticipant.observe(this , Observer {

            if (it != null){

                Log.d("matchstat" , "live par observleendi ... " + it?.toString())
                participant = it!!



                if (participant?.tfc == true){


                    fromIn = false

                    randomActivityViewModel.update_fromLobby(true)
                    Log.d("matchstat" , "tfc true çıktı ... " )

                    outhandler.postDelayed(
                        {

                            Log.d("matchstat" , "outhandler tetikledi ... " + it?.toString())

                            val room = participant?.rm

                            val userinfo = JitsiMeetUserInfo().apply {
                                displayName = name
                                avatar = URL(photo)
                            }

                            var options = JitsiMeetConferenceOptions.Builder()
                                .setRoom("https://meet.recommyz.com/$room")
                                .setUserInfo(userinfo)
                                .setFeatureFlag("lobby-mode.enabled" , true)
                                .build()

                            Log.d("randomActivity" , "prejoin view eklendi ... ")

                            join(options)

                        } ,
                        800
                    )


                } else if (participant?.tfc == false){
                    Log.d("matchstat" , "tfc false çıktı ... " )
                    fromIn = true

                    inhandler.postDelayed(
                        {

                            randomActivityViewModel.update_fromLobby(true)



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
                        } ,
                        800
                    )


                }
            } else {

                Log.d("matchstat" , "live par observleendi ... sonuç boş ... ")
            }
        })

        randomActivityViewModel.changeMatch.observe(this , Observer {
            if (it == true){
                _hangUp = false
                leave()
                randomActivityViewModel.update_changeMatch(false)
                MainActivity.fm.updateOwnerMatchedStatus(uid!! , true)
                view!!.addView(randomconnectionview)
            }
        })

        registerForBroadcastMessages()

        /**
         * AKTİVİTE İLK BAŞLADIĞINDA KLASİK GİRİŞİ YAP ...
         * */

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
            .setFeatureFlag("lobby-mode.enabled" , true)
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

        Log.d("randomActivity" , "prejoin view eklendi ... ")
        view!!.addView(prejoinView)
        join(options)


        /**
         * Kullanıcı isteklerini işle ...
         * */

        randomActivityViewModel.AudioMute.observe(this , Observer {
            if (it == false){

                Log.d("randomActivity" , "Audio ... " + it)
                HandleAudioBroadcastAction(it)
            } else if (it == true){

                Log.d("randomActivity" , "Audio ... " + it)
                HandleAudioBroadcastAction(it)
            }
        })

        randomActivityViewModel.VideoMute.observe(this , Observer {
            if (it == false){

                Log.d("randomActivity" , "video ... " + it)
                HandleCameraBroadCastAction(it)
            } else if (it == true){

                Log.d("randomActivity" , "video ... " + it)
                HandleCameraBroadCastAction(it)
            }
        })

        randomActivityViewModel.hangup.observe(this , Observer {
            if (it == true){
                HandleHangUpBroadCastAction()
                _hangUp = it
            }
        })

        randomActivityViewModel.flipCamera.observe(this , Observer {
            HandleFlipCameraBroadcastAction()
        })

    }


    override fun onParticipantLeft(extraData: HashMap<String, Any>?) {
        super.onParticipantLeft(extraData)

        when(session){
            "next" -> {
                MainActivity.fm.updateOwnerMatchedStatus(uid = mUid , true)
                view!!.removeView(randomactivitymeetscreen)
                view!!.addView(randomconnectionview)
            }
        }


    }

    override fun onParticipantJoined(extraData: HashMap<String, Any>?) {
        super.onParticipantJoined(extraData)


        when(session){
            "next" -> {

                if (fromIn){
                    view!!.removeView(randomconnectionview)
                    view!!.addView(randomactivitymeetscreen)
                }

            }
        }
    }

    override fun onConferenceWillJoin(extraData: HashMap<String, Any>?) {
        super.onConferenceWillJoin(extraData)
        when(session){
            "first" -> {
            }
        }

    }

    override fun onConferenceJoined(extraData: HashMap<String, Any>?) {
        super.onConferenceJoined(extraData)

        when(session){
            "first" -> {
                /**
                 * İlk sefer girişte ...
                 * */
                view!!.removeView(prejoinView)
                view!!.addView(swipeScreen)
            }

            "next" -> {

                if (!fromIn){
                    view!!.removeView(randomconnectionview)
                    view!!.addView(randomactivitymeetscreen)
                }

            }
        }



        Log.d("randomActivity" , "kendi odasını oluştrudu ... ")
    }



    private fun onBroadcastReceived(intent: Intent?){
        if (intent != null){
            val event = BroadcastEvent(intent)
            when(event.type){
                BroadcastEvent.Type.CONFERENCE_JOINED -> Timber.i("Conference joined with url%s" , event.data.get("url"))
                BroadcastEvent.Type.PARTICIPANT_JOINED -> Timber.i("Participant joined%s" , event.data.get("name"))
                BroadcastEvent.Type.AUDIO_MUTED_CHANGED -> Timber.i("audio muted" , event.data.get("muted"))
                BroadcastEvent.Type.VIDEO_MUTED_CHANGED -> Timber.i("video muted" , event.data.get("muted"))
                else -> Timber.i("Received event: %s" , event.type)
            }
        }
    }


    private fun HandleAudioBroadcastAction(value:Boolean){
        val muteBroadcastActionIntent = BroadcastIntentHelper.buildSetAudioMutedIntent(value)
        LocalBroadcastManager.getInstance(this).sendBroadcast(muteBroadcastActionIntent)
    }

    private fun HandleFlipCameraBroadcastAction(){
        val flipCameraAction = BroadcastIntentHelper.buildToggleCameraIntent()
        LocalBroadcastManager.getInstance(this).sendBroadcast(flipCameraAction)
    }

    private fun HandleCameraBroadCastAction(value: Boolean){

        val muteBroadcastActionIntent = BroadcastIntentHelper.buildSetVideoMutedIntent(value)
        LocalBroadcastManager.getInstance(this).sendBroadcast(muteBroadcastActionIntent)

    }

    private fun HandleHangUpBroadCastAction(){
        val muteBroadcastActionIntent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(this).sendBroadcast(muteBroadcastActionIntent)
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
        intentFilter.addAction(BroadcastEvent.Type.VIDEO_MUTED_CHANGED.action)

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
        MainActivity.mMainActivityVM.update_inRandom(false)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        JitsiMeetActivityDelegate.onNewIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (_hangUp){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
            JitsiMeetActivityDelegate.onHostDestroy(this)
            MainActivity.mMainActivityVM.update_inRandom(false)
        }

    }

    override fun onStop() {
        super.onStop()
        if (_hangUp){
            JitsiMeetActivityDelegate.onHostDestroy(this)
            MainActivity.mMainActivityVM.update_inRandom(false)
        }
    }
}