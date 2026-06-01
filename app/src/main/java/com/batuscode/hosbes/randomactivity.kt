package com.batuscode.hosbes

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.view.RandomActivityMeetScreen
import com.batuscode.hosbes.view.RandomActivityView
import com.batuscode.hosbes.view.RandomPreJoinScreen
import com.batuscode.hosbes.view.ShowMeetInfo
import com.batuscode.hosbes.viewmodel.RandomActivityViewModel
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

class RandomActivity:JitsiMeetActivity() , JitsiMeetActivityInterface{

    var session by mutableStateOf("")
    lateinit var view:JitsiMeetView

    lateinit var  prejoinView:ComposeView
    lateinit var swipeScreen:ComposeView
    lateinit var randomconnectionview:ComposeView
    lateinit var randomactivitymeetscreen:ComposeView
    lateinit var context: Context
    lateinit var mrandomActivityViewModel: RandomActivityViewModel
    lateinit var showMeetInfoScreen:ComposeView
    lateinit var mUid: String

    var fromIn by mutableStateOf(false)

    var matchDefeat by mutableStateOf(false)

    var _hangUp by mutableStateOf(false)

    var timerComplated by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        Log.d("randomActivity" , "aktivite oluşturuldu ... ")

        val randomActivityViewModel: RandomActivityViewModel by viewModels()

        /**
         * Rastgele aktivitesinin her start aldığı noktadan geçirilen oturum parametresini al ...
         * */
        session = intent.getStringExtra("session").toString()

        mrandomActivityViewModel = randomActivityViewModel
        context = this
        view = jitsiView

        MainActivity.mMainActivityVM.update_inRandom(true)





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
        
        showMeetInfoScreen = ComposeView(this).apply { 
            setContent { 
                HoşbeşTheme {
                    ShowMeetInfo(mainActivityVM = MainActivity.mMainActivityVM , randomActivityViewModel = randomActivityViewModel)
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


        val uid = MainActivity.PreferenceManager?.getuidShared("uid")
        var name = MainActivity.PreferenceManager?.getString("displayName")
        var photo = MainActivity.PreferenceManager?.getString("photoUrl")


        /**
         * İLK SWİPE OLAYI ...
         * */
        randomActivityViewModel.swiped.observe(this , Observer {
            if (it?.equals(true) == true){

                /**
                 * sola kaydırdı ve kendi karşılaşma isteğini true olarak güncelledi ...
                 * */

                MainActivity.fm.updateMatchRequest(true , uid!!)

                Log.d("randomActivity" , "ekran kaydırıldı random connection view eklendi ... ")

                /**
                 * ilk oturum kaydırma ekranını sil ...
                 * */

                view!!.removeView(swipeScreen)

                /**
                 * Karşılaşma arama aktivitesini başlat ...
                 * */

                val intent = Intent(this , RandomConnectionActivity::class.java)
                startActivity(intent)
                leave()

            }
        })


        /**
         * Görüşme değiştirme ...
         * */
        randomActivityViewModel.changeMatch.observe(this , Observer {
            if (it == true){

                randomActivityViewModel.update_changeMatch(false)
                MainActivity.fm.updateMatchRequest(true , uid!!)

                val intent = Intent(this , RandomConnectionActivity::class.java)
                startActivity(intent)
                leave()
            }
        })

        /**
         * Karşılaştırma bilgi sayfasındaki sayacın bitip bitmediğini gözlemle ...
         * */

        randomActivityViewModel.countTimerComplated.observe(this , Observer {
            if (it == true){
                timerComplated = it

                view!!.removeView(showMeetInfoScreen)
                view!!.addView(randomactivitymeetscreen)
            }
        })

        registerForBroadcastMessages()

        setDefaultMeetingOptions()



        /**
         * AKTİVİTE İLK BAŞLADIĞINDA KLASİK GİRİŞİ YAP ...
         * */


        when(session){
            "first" -> {



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
            }

            "next" -> {

               val Rname = intent.getStringExtra("displayName")
               val photoUrl = intent.getStringExtra("photoUrl")
               val uid = intent.getStringExtra("uid")
               val match = intent.getBooleanExtra("match" , false)
               val rm = intent.getStringExtra("rm")
               val tfc = intent.getBooleanExtra("tfc" , false)
               val outId = intent.getStringExtra("outId")


                val userinfo = JitsiMeetUserInfo().apply {
                    displayName = name
                    avatar = URL(photo)
                }




                var options = JitsiMeetConferenceOptions.Builder()
                    .setRoom("https://meet.recommyz.com/$rm")
                    .setUserInfo(userinfo)
                    .build()

                Log.d("randomActivity" , "prejoin view eklendi ... ")
                view!!.addView(showMeetInfoScreen)
                join(options)

            }
        }


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
        if (!_hangUp){
            /**
             * Kişi rastgele modundan çıkmak istemedi ise yeni karşılaşmalara it ...
             * */
            val uid = MainActivity.PreferenceManager?.getuidShared("uid")

            MainActivity.fm.updateMatchRequest(true , uid!!)

            val intent = Intent(this , RandomConnectionActivity::class.java)
            startActivity(intent)
            leave()
        } else {
            val uid = MainActivity.PreferenceManager?.getuidShared("uid")
            MainActivity.fm.removeRandomParticipant(uid!!)
            HandleHangUpBroadCastAction()
        }
    }

    override fun onParticipantJoined(extraData: HashMap<String, Any>?) {
        super.onParticipantJoined(extraData)
    }

    override fun onConferenceWillJoin(extraData: HashMap<String, Any>?) {
        super.onConferenceWillJoin(extraData)
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

                /**
                 * Rastgele katılımcıyı ekle ...
                 * */
                val uid = MainActivity.PreferenceManager?.getuidShared("uid")
                var name = MainActivity.PreferenceManager?.getString("displayName")
                var photo = MainActivity.PreferenceManager?.getString("photoUrl")

                /**
                 * Rastgele katılımıcını ekle ...
                 * */

                MainActivity.fm.addRandomParticipant(uid = uid!! , displayName = name!! , photoUrl = photo!!) // önce random a kaydet ...
            }

            "next" -> {
                if (timerComplated){
                    view!!.removeView(showMeetInfoScreen)
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

    private fun setDefaultMeetingOptions(){

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
    }
}