package com.batuscode.hosbes.views

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.Whisper
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.utility.VoiceCallsViewModel
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


/**
 * fısıltıdan (whisperchat) arama koridoruna attı (callscorridor) ...
 * ilk yapacağın şey arama isteği göndermek ...
 *
 * */

fun startCall(context: Context, uid:String, displayName:String, photoUrl:String){
    val intent = Intent(context , VoiceCalls::class.java)
    intent.putExtra("uid" , uid)
    intent.putExtra("displayName" , displayName)
    intent.putExtra("photoUrl" , photoUrl)
    intent.putExtra("type" , "calling")
    context.startActivity(intent)
}
@Composable
fun CallsCorridor( mainActivityVM: MainActivityVM , whisperItem: Whisper){
    MainActivity.fm.mainActivityVM = mainActivityVM
    val context = LocalContext.current


    /**
     * aranan kişinin arama durumu ... koridorda arama durumu buna göre şekillencek ...
     * */
    val _requestCall by mainActivityVM.requestCall.collectAsState()

    val wuid = whisperItem?.wuid
    val wphotoUrl = whisperItem?.wphotoUrl
    val wdisplayName = whisperItem?.wdisplayName
    val ownerId = MainActivity.PreferenceManager?.getuidShared("uid")


    val calls by mainActivityVM.calls.collectAsState() // karşı tarafın arama geçmişindeki son arama bilgileri ...
    val wcalls by mainActivityVM.Wcalls.collectAsState()

    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }
    var handler = Handler(Looper.getMainLooper())

    var activityStarted by remember {
        mutableStateOf(false)
    }

    GlideApp.with(context)
        .asBitmap()
        .load(calls?.displayName)
        .into(object : CustomTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                image = resource.asImageBitmap()
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }


        })

    val lifecycle = LocalLifecycleOwner.current

    DisposableEffect(lifecycle){

        val observer = LifecycleEventObserver{
            _, event ->

            when(event){
                Lifecycle.Event.ON_CREATE -> {
                    Log.d("yanit" , "koridor oluştu...")
                    /**
                     * arama yapılmadan önce bir istek atılır .... fısıldamada arama yapılmak istendiğinde arama yapan kişi
                     * kabul veya red cevabını beklemeli ... bunun için bir bekleme ekranı olmalı ...
                     * aranan kişinin resmi , adı ve çağrı durumu , iptal butonu görünmeli ...
                     * */
                    MainActivity.fm.callrequest(ownerId = ownerId!! , uid = wuid!! , displayName = wdisplayName!! , photoUrl = wphotoUrl!! , mainActivityVM)

                }
                Lifecycle.Event.ON_START -> {

                }
                Lifecycle.Event.ON_RESUME -> {

                }
                Lifecycle.Event.ON_PAUSE -> {

                }
                Lifecycle.Event.ON_STOP -> {

                }
                Lifecycle.Event.ON_DESTROY -> {

                }
                Lifecycle.Event.ON_ANY -> {

                }
            }
        }

        lifecycle.lifecycle.addObserver(observer)

        onDispose {
            lifecycle.lifecycle.removeObserver(observer)
        }

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
                        text = calls?.photoUrl!!
                    )
                }

                /**
                 * aranan kişinin arama durumuna göre akışı düzenle ...
                 * */

                if (_requestCall == null){
                    Text(text = "Bağlanıyor...")

                }
                else if (wcalls?.act == true){
                    Log.d("yanit" , "onayladi...")

                    /**
                     * aranan kişi aramayı kabul etti ... görüşmeyi başlat ...
                     * */
                    if (!activityStarted){
                        activityStarted = true
                        startCall(context,wuid!!,wdisplayName!!,wphotoUrl!!)
                        Log.d("yanit" , "aktivite başlamamış...")
                    }
                    MainActivity.navigate?.popBackStack()
                }
                else if (_requestCall == true) { // arama isteğinin yanıtı karşı taraf bir aramada ise ...
                    /**
                     * karşı taraf aramada ise ...
                     * */
                    Text(text = "Meşgul...")
                    handler.postDelayed({
                        MainActivity.navigate?.popBackStack()
                    },500)
                }
                else if (_requestCall == false) { // karşı taraf aramada değil ise ...
                    Text(text = "Çalıyor...")
                    Log.d("ikinci" , "kullanici kapattı... ve bu yine çalişti ...")

                    /**
                     * karşı taraf görüşmede değil arama başladığı için karşı tarafın ICC (incomingcall) field'ını güncelle ...
                     * burada karşı tarafın arama geçmişindeki son aramayı dinle ve arama durumunu çek ...
                     * */
                    MainActivity.fm.calling(uid = wuid!!)

                }

            }



            IconButton(onClick = {
                MainActivity.fm.declineCall(ownerId!! , wuid!!)
                MainActivity.navigate?.popBackStack()
            } ,
                modifier = Modifier
            ) {
                Icon(painter = painterResource(id = R.drawable.call_end_24px), contentDescription = "")
            }
        }
    }

}