package com.batuscode.hosbes.views

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.Calls
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.MainActivityVM
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

fun answerCall(context: Context , calls:Calls){
    val intent = Intent(context , VoiceCalls::class.java)
    intent.putExtra("type" , "answered")
    intent.putExtra("roomId" , calls.uid!!)

    context.startActivity(intent)
}
@Composable
fun ICC(mainActivityVM: MainActivityVM){
    val context = LocalContext.current

    val uid = MainActivity?.PreferenceManager?.getuidShared("uid")

    /**
     * gelen arama ile birlikte getirilen arama geçmişindeki son arama ...
     * */

    val calls by mainActivityVM.calls.collectAsState()


    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }

    if (calls != null){
        MainActivity.PreferenceManager?.saveuid("callOwnerName" , calls?.displayName!!)

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

                Text(text = "Gelen sesli arama...")


            }


            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                IconButton(onClick = {
                    //TODO: aramayi cevapla butonu ...
                    MainActivity.fm.acceptCall(ownerId = calls?.uid!! , uid = uid!!)
                    answerCall(MainActivity.context , calls = calls!!)

                } ,
                    modifier = Modifier
                ) {
                    Icon(painter = painterResource(id = R.drawable.call_24px), contentDescription = "")
                }

                IconButton(onClick = {
                    //TODO: aramayi reddet butonu ...
                    MainActivity.fm.declineCall(calls?.uid!! , uid!!)
                    MainActivity.navigate?.popBackStack()
                } ,
                    modifier = Modifier
                ) {
                    Icon(painter = painterResource(id = R.drawable.call_end_24px), contentDescription = "")
                }
            }
        }
    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun ICCPreview(){
    HoşbeşTheme {
        ICC(mainActivityVM = MainActivityVM())
    }
}