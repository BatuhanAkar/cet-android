package com.batuscode.hosbes.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.MainActivityVM
import com.google.android.material.button.MaterialButtonToggleGroup

@Composable
fun EntryScreen(mainActivityVM: MainActivityVM){
    val channels = listOf("Mavi Boncuk" , "Hoşbeş" , "Goygoy" , "Dırdır")
    var SelectedChannel by remember {
        mutableStateOf("Goygoy")
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
        innerPadding ->

        ConstraintLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        )
        {

            val (channelList , explainPage , goButton , channelExplainText) = createRefs()


            Text(
                text = stringResource(id = R.string.selectlivechannelexplaintext) ,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                    fontSize = 18.sp
                ),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .constrainAs(explainPage) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(channelList.top)
                    }
            )


            Surface(
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp ,
                color = Color.White,
                modifier = Modifier
                    .wrapContentHeight()
                    .width(200.dp)
                    .constrainAs(channelList) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
            {

                Column(
                    modifier = Modifier
                    
                ) {
                    channels.forEachIndexed { index, channel ->

                        Row (
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .weight(1f, false)
                                .fillMaxWidth()
                                .background(
                                    color = if (channel == SelectedChannel) Color.LightGray.copy(
                                        0.2f
                                    ) else Color.Transparent
                                )
                                .clickable {
                                    Log.d("textClicked", "ok:::")
                                    SelectedChannel = channel
                                    //  selected = true
                                    //  isExpanded = false
                                },
                            verticalAlignment = Alignment.CenterVertically ,
                        ) {



                            Text(
                                text = channel ,
                                  color = if (channel == SelectedChannel) Color.Black else Color.LightGray ,
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                                    fontSize = 23.sp

                                ) ,
                                modifier = Modifier
                                    .padding(8.dp)


                            )


                            if (channel == "Goygoy"){
                                Icon(
                                    painter = painterResource(id = R.drawable.video_chat_24px) ,
                                    contentDescription = "" ,
                                    tint = if (channel == SelectedChannel) Color.Black else Color.LightGray,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp)
                                )
                            } else if (channel == "Dırdır"){
                                Icon(
                                    painter = painterResource(id = R.drawable.voice_chat_24px) ,
                                    contentDescription = "" ,
                                    tint = if (channel == SelectedChannel) Color.Black else Color.LightGray,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp)
                                )

                            } else if (channel == "Mavi Boncuk"){
                                Icon(
                                    painter = painterResource(id = R.drawable.chat_24px) ,
                                    contentDescription = "" ,
                                    tint = if (channel == SelectedChannel) Color.Black else Color.LightGray,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp)
                                )
                            } else if (channel == "Hoşbeş"){
                                Icon(
                                    painter = painterResource(id = R.drawable.chat_24px) ,
                                    contentDescription = "" ,
                                    tint = if (channel == SelectedChannel) Color.Black else Color.LightGray,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp)
                                )
                            }
//
//                                Spacer(modifier = Modifier.padding(10.dp))
//
//                                (if (channel == SelectedChannel) Icons.Filled.Check else null)?.let { Image(imageVector = it, contentDescription = null) }



                        }
                    }

                }

            }

            FilledTonalButton(onClick = {

                when(SelectedChannel){
                    "Mavi Boncuk" -> {
                        mainActivityVM.connectChannel("C2")
                        MainActivity.navigate?.navigate("chat")

                    }
                    "Hoşbeş" -> {
                        mainActivityVM.connectChannel("C1")
                        MainActivity.navigate?.navigate("chat")

                    }
                    "Goygoy" -> {

                        mainActivityVM.update_VoiceChannelRefused(false)
                        mainActivityVM.updateInStreamChannel(true)
                        mainActivityVM.updateSelectedChannel("Goygoy")
                        mainActivityVM.updateStreamChannelType("video")
                        MainActivity.navigate?.clearBackStack("entryscreen")
                        MainActivity.navigate?.navigate("chat")

                    }
                    "Dırdır" -> {

                        mainActivityVM.update_VideoChannelRefused(false)
                        mainActivityVM.updateInStreamChannel(true)
                        mainActivityVM.updateSelectedChannel("Dırdır")
                        mainActivityVM.updateStreamChannelType("voice")
                        mainActivityVM.updateChannelName("Dırdır")
                        MainActivity.navigate?.clearBackStack("entryscreen")
                        MainActivity.navigate?.navigate("chat")

                    }
                }


            } ,
                border = null , 
                shape = RoundedCornerShape(16.dp) , 
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colorResource(id = R.color.blue)
                ) ,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(200.dp)
                    .constrainAs(goButton) {
                        top.linkTo(channelList.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "")
            }
            
            Text(
                text = explainChannel(choice = SelectedChannel) ,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                    fontSize = 18.sp
                ),
                modifier = Modifier
                    .constrainAs(channelExplainText){
                        top.linkTo(goButton.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )


        }
    }
}

@Composable
fun explainChannel(choice:String):String{
    
    when(choice){
        "Mavi Boncuk" -> {
            return stringResource(id = R.string.textchannel)
        }
        "Hoşbeş" -> {
            return stringResource(id = R.string.textchannel)
        }
        "Goygoy" -> {
            return stringResource(id = R.string.videostremchannel)
        }
        "Dırdır" -> {
            return stringResource(id = R.string.voicestremchannel)
        }
        else -> return stringResource(id = R.string.selectchannel)
    }
    
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun EntryScreenPreview(){
    HoşbeşTheme {
        EntryScreen(MainActivityVM())
    }
}