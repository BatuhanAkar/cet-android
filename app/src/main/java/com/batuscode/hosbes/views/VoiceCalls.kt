package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import androidx.fragment.app.FragmentActivity
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
import org.jitsi.meet.sdk.JitsiMeetActivityInterface
import kotlin.properties.Delegates

class VoiceCalls:FragmentActivity(), JitsiMeetActivityInterface{

    val mainActivityVM = MainActivity.mMainActivityVM

    val voiceCallsViewModel:VoiceCallsViewModel by viewModels()

    var CScope = CoroutineScope(Dispatchers.Default)
    val ownerId = MainActivity.PreferenceManager?.getuidShared("uid")
    lateinit var uid:String // karşı tarafın id'si
    lateinit var photoUrl:String
    lateinit var displayName:String


    companion object{
        lateinit var VoiceCallsContext:Context
    }
    override fun requestPermissions(p0: Array<out String>?, p1: Int, p2: PermissionListener?) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        CScope.cancel()
        MainActivity.fm.detachCallsListener()

    }

    override fun onStop() {
        super.onStop()
        CScope.cancel()
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
        MainActivity.fm.voiceCallsViewModel = voiceCallsViewModel

        /***
         * arama yapan kişi kendi aramalarını dinleyecek ... arama durumu önemlisi ... bide kişi bilgiler çekilecek ...
         */


        enableEdgeToEdge()
        setContent {
            HoşbeşTheme {
                /**
                 * arama yapılmadan önce bir istek atılır .... fısıldamada arama yapılmak istendiğinde arama yapan kişi
                 * kabul veya red cevabını beklemeli ... bunun için bir bekleme ekranı olmalı ...
                 * aranan kişinin resmi , adı ve çağrı durumu , iptal butonu görünmeli ...
                 * */

                CallsCorridor(voiceCallsViewModel = voiceCallsViewModel)

                CScope.launch {
                    delay(500)
                    MainActivity.fm.callrequest(ownerId = ownerId!! , uid = uid!! , displayName = displayName!! , photoUrl = photoUrl!!, voiceCallsViewModel = voiceCallsViewModel)

                }

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    @Composable
    fun CallsCorridor(voiceCallsViewModel: VoiceCallsViewModel){
        val context = LocalContext.current
        val _requestCall by voiceCallsViewModel.requestCall.collectAsState()
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
                    TODO("Not yet implemented")
                }


            })
        if (calls?.act == false){

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




                    Text(
                        text = calls?.displayName!!
                    )

                    if (_requestCall == null){
                        Text(text = "Bağlanıyor...")

                    } else if (_requestCall == false) {
                        Text(text = "Çalıyor...")
                        MainActivity.fm.calling(ownerId = ownerId!! , uid = uid , voiceCallsViewModel = voiceCallsViewModel)
                    }
                    else if (calls?.act == false){ // bu karşı tarafın arama geçmişindeki act durumu olmalı ...
                        Text(text = "Meşgul...")
                        finish()
                    }

                }



                IconButton(onClick = {
                    MainActivity.fm.declineCall(ownerId!! , uid , voiceCallsViewModel)
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