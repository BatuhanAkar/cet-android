package com.batuscode.hosbes.views

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.OptIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.Participnat
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.viewmodel.OutCallActivityViewModel
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition



@OptIn(UnstableApi::class)
@Composable
fun ICC(outCallActivityViewModel: OutCallActivityViewModel, Historycalls:Participnat){
    val context = LocalContext.current

    val uid = MainActivity?.PreferenceManager?.getuidShared("uid")


    val WillJoin by outCallActivityViewModel.WillJoin.collectAsState()



    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }

    if (Historycalls != null){
        MainActivity.PreferenceManager?.saveuid("callOwnerName" , Historycalls?.displayName!!)

        GlideApp.with(context)
            .asBitmap()
            .load(Historycalls?.photoUrl)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    image = resource.asImageBitmap()
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }


            })
    }



    Scaffold(
        containerColor = Color.White
    ) {innerPadding ->
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


                if (Historycalls != null){
                    Text(
                        text = Historycalls?.displayName!!
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
                    outCallActivityViewModel.updateLjoin(true)
                } ,
                    modifier = Modifier
                ) {
                    Icon(painter = painterResource(id = R.drawable.call_24px), contentDescription = "")
                }

                IconButton(onClick = {
                    //TODO: aramayi reddet butonu ...

                    outCallActivityViewModel.updateLjoin(false)

                  //  MainActivity.fm.declineCall(Historycalls?.uid!! , uid!!)
                  //  MainActivity.navigate?.popBackStack()
                } ,
                    modifier = Modifier
                ) {
                    Icon(painter = painterResource(id = R.drawable.call_end_24px), contentDescription = "")
                }
            }
        }
    }

}
/*

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun ICCPreview(){
    HoşbeşTheme {
        ICC(mainActivityVM = MainActivityVM())
    }
}*/
