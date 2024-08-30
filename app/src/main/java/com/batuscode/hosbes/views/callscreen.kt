package com.batuscode.hosbes.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.Participnat
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.InCallActivityViewModel
import com.batuscode.hosbes.utility.MainActivityVM
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


fun applyBlur(context:Context , mimage:Bitmap):ImageBitmap{
    val rs = RenderScript.create(context)
    val input = Allocation.createFromBitmap(rs , mimage)
    val output = Allocation.createTyped(rs , input.type)
    val script = ScriptIntrinsicBlur.create(rs , Element.U8_4(rs))

    script.setRadius(10f)
    script.setInput(input)
    script.forEach(output)
    output.copyTo(mimage)

    rs.destroy()
    return mimage.asImageBitmap()
}

@Composable
fun CallScreen(historycalls:Participnat , inCallActivityViewModel: InCallActivityViewModel , type:String){

    /**
     * Jitsi aktivitesinin görünümüne eklenecek olan ekran bu ... bu ekranda
     * görünecek olan şey kullanıcı resmi , ismi , ve arama sonlandırma butonu ...
     * */
    val context = LocalContext.current
    val historyCalls = historycalls
  //  val whisperItem by voiceCallActivityVM.whisperItem.collectAsState()

    val WillJoin by inCallActivityViewModel.WillJoin.collectAsState()
/*
    val endCall by voiceCallActivityVM.showEndedCallText.collectAsState()
    val participantJoined by voiceCallActivityVM.participantJoined.collectAsState()*/

    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }
    var backgroundImage by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var callStateText by remember {
        mutableStateOf("")
    }

    GlideApp.with(context)
        .asBitmap()
        .load(historyCalls?.photoUrl)
        .into(object : CustomTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                image = resource.asImageBitmap()
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }


        })

    val backgroundBrush = ShaderBrush(ImageShader(image!!))


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    )
    { innerPadding ->

        Column (
            verticalArrangement = Arrangement.spacedBy(400.dp),
            horizontalAlignment = Alignment.CenterHorizontally ,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(colorResource(id = R.color.pianoblack))
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
                            .width(120.dp)
                            .height(120.dp),
                        contentScale = ContentScale.Fit
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

                when(WillJoin){
                    null -> callStateText = "Bağlaniyor ..."
                    true -> callStateText = "Aranıyor ..."
                    false -> callStateText = "Arama devam ediyor..."
                }

                Text(
                    text = historyCalls?.displayName!! ,
                    color = Color.White
                )
                Text(
                    text = callStateText ,
                    color = Color.White
                )



            }



            IconButton(onClick = {
                inCallActivityViewModel.updateEndCall(true)
            } ,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Red
                ),
                modifier = Modifier
                    .size(60.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_call_end_24) ,
                    contentDescription = "" ,
                    tint = Color.White,
                    modifier = Modifier
                        .size(25.dp)
                )
            }
        }

    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun CallScreenPreview(){
    HoşbeşTheme {
        CallScreen( Participnat() , InCallActivityViewModel() , "")
    }
}