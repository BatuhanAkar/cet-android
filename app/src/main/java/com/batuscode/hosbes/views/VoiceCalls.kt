package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.VoiceCallsViewModel
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.react.modules.core.PermissionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate
import org.jitsi.meet.sdk.JitsiMeetActivityInterface
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetView
import timber.log.Timber
import java.net.URL
import kotlin.properties.Delegates

class VoiceCalls:FragmentActivity(), JitsiMeetActivityInterface{

    var view:JitsiMeetView? = null


    val mainActivityVM = MainActivity.mMainActivityVM

    val voiceCallsViewModel:VoiceCallsViewModel by viewModels()

    var handler = Handler(Looper.getMainLooper())
    val ownerId = MainActivity.PreferenceManager?.getuidShared("uid")
    lateinit var uid:String // karşı tarafın id'si
    lateinit var photoUrl:String
    lateinit var displayName:String
    lateinit var type:String
    lateinit var roomId:String
    var answered by mutableStateOf(false)



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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        JitsiMeetActivityDelegate.onHostDestroy(this)
        MainActivity.fm.detachCallsListener()

    }

    override fun onStop() {
        super.onStop()
        MainActivity.fm.detachCallsListener()
    }



    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VoiceCallsContext = this
        val VoiceCallsIntent = intent
        uid = VoiceCallsIntent.getStringExtra("uid").toString()
        photoUrl = VoiceCallsIntent.getStringExtra("photoUrl").toString()
        displayName = VoiceCallsIntent.getStringExtra("displayName").toString()
        type = VoiceCallsIntent.getStringExtra("type").toString()
        roomId = VoiceCallsIntent.getStringExtra("roomId").toString()
        MainActivity.fm.voiceCallsViewModel = voiceCallsViewModel


        val serverURL: URL
        serverURL = URL("https://recommyz.com")
        var defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setFeatureFlag("welcomepage.enabled" , false)
            .build()

        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        /***
         * arama yapan kişi kendi aramalarını dinleyecek ... arama durumu önemlisi ... bide kişi bilgiler çekilecek ...
         */


        enableEdgeToEdge()

        if (type.equals("calling")){
            setContent {
                HoşbeşTheme {
                    if (answered == false){

                        /**
                         * arama yapılmadan önce bir istek atılır .... fısıldamada arama yapılmak istendiğinde arama yapan kişi
                         * kabul veya red cevabını beklemeli ... bunun için bir bekleme ekranı olmalı ...
                         * aranan kişinin resmi , adı ve çağrı durumu , iptal butonu görünmeli ...
                         * */

                        CallsCorridor(voiceCallsViewModel = voiceCallsViewModel)

                        /**
                         * arama koridoruna girildikten sonra 5 salise sonra aranan kişinin aramada olup olmadığına bak ve buna göre ICC yolla ...
                         * */

                        handler.postDelayed({
                            MainActivity.fm.callrequest(ownerId = ownerId!! , uid = uid!! , displayName = displayName!! , photoUrl = photoUrl!!, voiceCallsViewModel = voiceCallsViewModel)

                        } , 500)
                    } else if (answered == true){



                        val options = JitsiMeetConferenceOptions.Builder()
                            .setServerURL(URL("https://recommyz.com"))
                            .setRoom(ownerId)
                            .setAudioOnly(true)
                            .setAudioMuted(false)
                            .setVideoMuted(true)
                            .build()

                        AndroidView(factory = {
                                ctx ->
                            JitsiMeetView(ctx).apply {
                                join(options)
                            }
                        } ,

                            modifier = Modifier
                                .fillMaxSize()
                            )

                    }
                }

            }

        }
        else if (type.equals("answered")){

            view = JitsiMeetView(this)
            setContentView(view)
            val roomName: String
            roomName = "https://recommyz.com/$roomId"


            var options = JitsiMeetConferenceOptions.Builder()
                .setRoom(roomName)
                .setAudioOnly(true)
                .build()

            view!!.join(options)

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (answered == true){
            JitsiMeetActivityDelegate.onBackPressed()
        } else {
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        JitsiMeetActivityDelegate.onNewIntent(intent)
    }


    @Composable
    fun CallsCorridor(voiceCallsViewModel: VoiceCallsViewModel){
        val context = LocalContext.current
        val _requestCall by voiceCallsViewModel.requestCall.collectAsState()

        /**
         * karşı tarafın arama geçmişindeki arama durumu ...
         * */
        val Wcalls by voiceCallsViewModel.Wcalls.collectAsState()

        if (Wcalls != null){
            Log.d("answeredcall" , "değeri == " + Wcalls?.act)
            answered = Wcalls?.act == true
        }

        /**
         * aranan kişinin arama durumu ... koridorda arama durumu buna göre şekillencek ...
         * */
        val call by voiceCallsViewModel.call.collectAsState()
        val calls by voiceCallsViewModel.calls.collectAsState()

        var image by remember{
            mutableStateOf<ImageBitmap?>(null)
        }

        GlideApp.with(context)
            .asBitmap()
            .load(calls?.photoUrl)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    image = resource.asImageBitmap()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }


            })



        if ( calls != null && calls?.act == false){

            finish()
        }
        Scaffold {innerPadding ->
            Column (
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally ,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .wrapContentSize()
                ){

                    if (image != null) {
                        Image(
                            bitmap = image!!,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .width(80.dp)
                                .height(80.dp),
                            contentScale = ContentScale.FillBounds
                        )
                    } else {

                        Image(
                            painter = painterResource(id = R.drawable.istockphoto_517188688_612x612),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .width(80.dp)
                                .height(80.dp),
                            contentScale = ContentScale.FillBounds
                        )

                    }


                    if (calls != null){

                        Text(
                            text = calls?.displayName!!
                        )
                    }

                    /**
                     * aranan kişinin arama durumuna göre akışı düzenle ...
                     * */

                    if (_requestCall == null){
                        Text(text = "Bağlanıyor...")

                    } else if (_requestCall == true) {
                        Text(text = "Meşgul...")
                        handler.postDelayed({
                            finish()

                        } , 400)
                    }
                    else if (calls?.act == false){ // bu karşı tarafın arama geçmişindeki act durumu olmalı ...
                        Text(text = "Meşgul...")
                        handler.postDelayed({
                            finish()

                        } , 400)
                    }else if (_requestCall == false) {
                        Text(text = "Çalıyor...")

                        /**
                         * aranan kişi bir aramada değilmiş ... kişiyi ara ...
                         * */
                        MainActivity.fm.calling(ownerId = ownerId!! , uid = uid , voiceCallsViewModel = voiceCallsViewModel)
                    }

                }



                IconButton(onClick = {
                    MainActivity.fm.declineCall(ownerId!! , uid)
                    finish()
                } ,
                    modifier = Modifier
                ) {
                    Icon(painter = painterResource(id = R.drawable.call_end_24px), contentDescription = "")
                }
            }
        }

    }

}