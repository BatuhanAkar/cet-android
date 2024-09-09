package com.batuscode.hosbes.views

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.MainActivityVM
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ConnectStremChannels(mainActivityVM: MainActivityVM){
    val context = LocalContext.current
    val selectedChannel by mainActivityVM.selectedChannel.collectAsState()
    val streamChannelType by mainActivityVM.streamChannelType.collectAsState()
    val showMore by mainActivityVM.showMore.collectAsState()

    val lifecycle = LocalLifecycleOwner.current


    Column( verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        
        if (showMore == true){
            More(mainActivityVM = mainActivityVM)
        }
/*
        Text(
            text = if (selectedChannel?.equals("Goygoy") == true) stringResource(id = R.string.connectvideochannel) else  stringResource(id = R.string.connectvoicechannel),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                fontSize = 20.sp , 
                textAlign = TextAlign.Center
            ) ,
            modifier = Modifier
                .weight(1f , false)
            
        )*/

        OutlinedButton(onClick = {
            /**
             * aktiviteyi başlat ...
             * */

            if (streamChannelType?.equals("video") == true){
                mainActivityVM.update_VideoChannelRefused(false)
            } else if (streamChannelType?.equals("voice") == true){
                mainActivityVM.update_VoiceChannelRefused(false)
            }
        }) {
            Text(text = stringResource(id = R.string.connectagain))
        }



    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun VoiceChannelPreview(){
    HoşbeşTheme {
        ConnectStremChannels(MainActivityVM())
    }
}