package com.batuscode.hosbes.views

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.MainActivityVM

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ConnectStremChannels(mainActivityVM: MainActivityVM){
    val context = LocalContext.current
    val selectedChannel by mainActivityVM.selectedChannel.collectAsState()
    val streamChannelType by mainActivityVM.streamChannelType.collectAsState()
    val showMore by mainActivityVM.showMore.collectAsState()
    Column( verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        
        if (showMore == true){
            More(mainActivityVM = mainActivityVM)
        }

        Text(
            text = if (selectedChannel?.equals("Goygoy") == true) stringResource(id = R.string.connectvideochannel) else  stringResource(id = R.string.connectvoicechannel),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                fontSize = 20.sp , 
                textAlign = TextAlign.Center
            ) ,
            modifier = Modifier
                .weight(1f , false)
            
        )

        OutlinedButton(onClick = {
            /**
             * aktiviteyi başlat ...
             * */

            if (streamChannelType?.equals("video") == true){
                val intent = Intent(context , VideoChannel::class.java)
                context.startActivity(intent)
            } else if (streamChannelType?.equals("voice") == true){
                val intent = Intent(context , VoiceChannel::class.java)
                context.startActivity(intent)
            }
        }) {
            Text(text = stringResource(id = R.string.connect))
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