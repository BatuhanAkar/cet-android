package com.batuscode.hosbes.views

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.MainActivityVM
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun startMeeting(uid:String,name:String,photoUrl:String,
                 Ruid:String,Rname:String,RphotoUrl:String , context: Context
){
    val intent = Intent(context , MatchStream::class.java)
    intent.putExtra("uid" , uid)
    intent.putExtra("name" , name)
    intent.putExtra("photoUrl" , photoUrl)

    intent.putExtra("Ruid" , Ruid)
    intent.putExtra("Rname" , Rname)
    intent.putExtra("RphotoUrl" , RphotoUrl)

    context.startActivity(intent)
}

@Composable
fun MatchConnectCorridor(mainActivityVM: MainActivityVM){


    val context = LocalContext.current

    val randomParticipant by mainActivityVM.randomParticipant.collectAsState()

    val uid = MainActivity.PreferenceManager?.getuidShared("uid")
    val displayName = MainActivity.PreferenceManager?.getString("displayName")
    val photoUrl = MainActivity.PreferenceManager?.getString("photoUrl")

    val Ruid = randomParticipant?.uid
    val Rname = randomParticipant?.displayName
    val RphotoUrl = randomParticipant?.photoUrl

    val scope = CoroutineScope(Dispatchers.Default)
    val lifecycle = LocalLifecycleOwner.current

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver{
                _,event ->

            when(event){
                Lifecycle.Event.ON_CREATE -> {
                    scope.launch {
                        delay(8000)

                        startMeeting(uid = uid!! , name = displayName!! , photoUrl = photoUrl!! ,
                            Ruid = Ruid!! , Rname = Rname!! , RphotoUrl = RphotoUrl!! , context = context)

                        MainActivity.navigate?.popBackStack()
                    }
                }
                Lifecycle.Event.ON_START -> {}
                Lifecycle.Event.ON_RESUME -> {}
                Lifecycle.Event.ON_PAUSE -> {}
                Lifecycle.Event.ON_STOP -> {
                    scope.cancel()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    scope.cancel()
                }
                Lifecycle.Event.ON_ANY -> {}
            }
        }

        lifecycle.lifecycle.addObserver(observer)

        onDispose {
            lifecycle.lifecycle.removeObserver(observer)
        }
    }

    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }
    GlideApp.with(context)
        .asBitmap()
        .load(RphotoUrl)
        .into(object : CustomTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                image = resource.asImageBitmap()
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }


        })
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
            innerPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally ,
            verticalArrangement = Arrangement.Center ,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
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

            Text(text = Rname!!)


            OutlinedButton(onClick = {
                MainActivity.navigate?.popBackStack()
            }) {
                Text(text = stringResource(id = R.string.cancel))
            }



        }
    }


}

@Composable
fun cvd(mainActivityVM: MainActivityVM){


}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun MatchConnectCorridorPreview(){
    HoşbeşTheme {
        cvd(MainActivityVM())
    }
}