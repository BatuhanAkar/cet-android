package com.batuscode.hosbes.views

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.MainActivityVM
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@Composable
fun CallScreen(mainActivityVM: MainActivityVM , type:String){

    /**
     * Jitsi aktivitesinin görünümüne eklenecek olan ekran bu ... bu ekranda
     * görünecek olan şey kullanıcı resmi , ismi , ve arama sonlandırma butonu ...
     * */
    val context = LocalContext.current
    val historyCalls by mainActivityVM.Historycalls.collectAsState()
    val whisperItem by mainActivityVM.whisperItem.collectAsState()


    val endCall by mainActivityVM.showEndedCallText.collectAsState()

    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }

    GlideApp.with(context)
        .asBitmap()
        .load(

            if (type.equals("calling")){
                whisperItem?.wphotoUrl
            } else if (type.equals("answered")){
                historyCalls?.photoUrl

            } else {}
        )
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
    ) { innerPadding ->

        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally ,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Column ( verticalArrangement = Arrangement.Center,
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
                (if (type.equals("calling")) whisperItem?.wdisplayName else historyCalls?.displayName)?.let {
                    Text(
                        text = it
                    )
                }

                Text(
                    text = if (endCall == true) stringResource(id = R.string.callisended) else "arama devam ediyor"
                )

            }



            IconButton(onClick = {
                mainActivityVM.updateEndCall(true)
            } ,
                modifier = Modifier
            ) {
                Icon(painter = painterResource(id = R.drawable.call_end_24px), contentDescription = "")
            }
        }

    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun CallScreenPreview(){
    HoşbeşTheme {
        CallScreen(MainActivityVM() , "")
    }
}