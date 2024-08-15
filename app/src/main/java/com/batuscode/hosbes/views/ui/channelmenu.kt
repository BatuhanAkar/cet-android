package com.batuscode.hosbes.views.ui

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.ChatViewModel
import com.batuscode.hosbes.utility.FirebaseManager
import com.batuscode.hosbes.utility.MainActivityVM
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@Composable
fun ChannelMenu( mainActivityVM: MainActivityVM ,chatViewModel: ChatViewModel ,
    modifier: Modifier = Modifier ,
    selectedChannel:String?
    ){
    
    val scope = rememberCoroutineScope()

    val channels = listOf("Mavi Boncuk" , "Hoşbeş" , "Goygoy" , "Dırdır")
    var SelectedChannel by remember {
        mutableStateOf(selectedChannel)
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var selected by remember {
        mutableStateOf(false)
    }

    val rotateUp by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, animationSpec = tween(durationMillis = 100 , easing = LinearEasing))

    if (selected){

        Log.d("chatChannels" , "is selected...")

        Log.d("chatChannels" , "selected channel is :: $SelectedChannel")
        SelectedChannel?.let {
            changeChannel(selectedChannelId = it , chatViewModel = chatViewModel , mainActivityVM = mainActivityVM)
            selected = false
        }

    }

    Column {
        Row ( horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .padding(2.dp)
                .clickable {
                    Log.d("textClicked", "ok:::")
                    isExpanded = true
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row( verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentSize()
            ) {
                SelectedChannel?.let {

                    Text(
                        text = it ,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                            fontSize = 23.sp
                        )
                    )

                    if (it == "Goygoy"){
                        Icon(
                            painter = painterResource(id = R.drawable.video_chat_24px) ,
                            contentDescription = "" ,
                            modifier = Modifier
                                .padding(start = 10.dp)

                                .size(24.dp)
                        )
                    } else if (it == "Dırdır"){
                        Icon(
                            painter = painterResource(id = R.drawable.voice_chat_24px) ,
                            contentDescription = "" ,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(24.dp)
                        )

                    } else if (it == "Mavi Boncuk"){
                        Icon(
                            painter = painterResource(id = R.drawable.chat_24px) ,
                            contentDescription = "" ,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(24.dp)
                        )
                    } else if (it == "Hoşbeş"){
                        Icon(
                            painter = painterResource(id = R.drawable.chat_24px) ,
                            contentDescription = "" ,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(24.dp)
                        )
                    }

                }

            }
            Image(
                imageVector = Icons.Filled.KeyboardArrowDown ,
                contentScale = ContentScale.Crop ,
                modifier = Modifier
                    .height(25.dp)
                    .width(25.dp)
                    .rotate(rotateUp),
                contentDescription = null
                )


        }
        if (isExpanded){
            Column {
                Surface (
                    modifier = modifier ,
                    shape = RoundedCornerShape(24.dp) ,
                    color = MaterialTheme.colorScheme.surface ,
                    tonalElevation = 1.dp
                ) {


                    DropdownMenu(
                        modifier = Modifier
                            .background(color = Color.White)
                            .clip(RoundedCornerShape(20.dp)),
                        offset = DpOffset(-2.dp , -2.dp) ,
                        expanded = isExpanded ,
                        onDismissRequest = { isExpanded = false }
                    ) {

                        channels.forEachIndexed { index, channel ->

                            Row (
                                modifier = modifier
                                    .padding(2.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        Log.d("textClicked", "ok:::")
                                        SelectedChannel = channel
                                        selected = true
                                        isExpanded = false
                                    },
                                verticalAlignment = Alignment.CenterVertically ,
                            ) {



                                Text(
                                    text = channel ,
                                    color = if (channel == SelectedChannel) Color.Black else Color.LightGray ,
                                    style = TextStyle(
                                        fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                                        fontSize = 23.sp

                                    )


                                )


                                if (channel == "Goygoy"){
                                    Icon(
                                        painter = painterResource(id = R.drawable.video_chat_24px) ,
                                        contentDescription = "" ,
                                        modifier = Modifier
                                            .padding(start = 15.dp)
                                            .size(24.dp)
                                    )
                                } else if (channel == "Dırdır"){
                                    Icon(
                                        painter = painterResource(id = R.drawable.voice_chat_24px) ,
                                        contentDescription = "" ,
                                        modifier = Modifier
                                            .padding(start = 20.dp)
                                            .size(24.dp)
                                        )

                                } else if (channel == "Mavi Boncuk"){
                                    Icon(
                                        painter = painterResource(id = R.drawable.chat_24px) ,
                                        contentDescription = "" ,
                                        modifier = Modifier
                                            .padding(start = 20.dp)
                                            .size(24.dp)
                                    )
                                } else if (channel == "Hoşbeş"){
                                    Icon(
                                        painter = painterResource(id = R.drawable.chat_24px) ,
                                        contentDescription = "" ,
                                        modifier = Modifier
                                            .padding(start = 20.dp)
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
            }
        }
    }
}


@Composable
fun changeChannel(selectedChannelId:String , chatViewModel: ChatViewModel , mainActivityVM: MainActivityVM){
    val loadMoreChat by mainActivityVM.loadMoreChat.collectAsState()

    var handler = Handler(Looper.getMainLooper())

    if (selectedChannelId.equals("Mavi Boncuk")){
        mainActivityVM.updateInVoiceChannel(false)

        MainActivity.fm.removeChatEventListener(FirebaseManager.C1)
        MainActivity.fm.removeChatEventListener(FirebaseManager.C1)
        MainActivity.fm.removeChatEventListener(FirebaseManager.C1)
        MainActivity.fm.removeChatEventListener(FirebaseManager.C1)
        MainActivity.fm.removeChatEventListener(FirebaseManager.C1)

        mainActivityVM.connectChannel("C2")
        mainActivityVM.updateSelectedChannel("Mavi Boncuk")

        chatViewModel.refreshChat()

        mainActivityVM.updateChatLoading(false)



        handler.postDelayed({

            MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C2 , false)



        } , 2000)

    } else if (selectedChannelId.equals("Hoşbeş")) {
        mainActivityVM.updateInVoiceChannel(false)

        MainActivity.fm.removeChatEventListener(FirebaseManager.C2)

        MainActivity.fm.removeChatEventListener(FirebaseManager.C2)
        MainActivity.fm.removeChatEventListener(FirebaseManager.C2)
        MainActivity.fm.removeChatEventListener(FirebaseManager.C2)

        mainActivityVM.connectChannel("C1")

        mainActivityVM.updateSelectedChannel("Hoşbeş")
        mainActivityVM.updateChatLoading(false)
        chatViewModel.refreshChat()


        handler.postDelayed({

            MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C1 , false)

        } , 5000)


    } else if (selectedChannelId.equals("Goygoy")){
        mainActivityVM.updateInVoiceChannel(true)
        mainActivityVM.updateSelectedChannel("Goygoy")
        mainActivityVM.updateStreamChannelType("video")
        mainActivityVM.updateChannelName("Goygoy")

    } else if (selectedChannelId.equals("Dırdır")){
        mainActivityVM.updateInVoiceChannel(true)
        mainActivityVM.updateSelectedChannel("Dırdır")
        mainActivityVM.updateStreamChannelType("voice")
        mainActivityVM.updateChannelName("Dırdır")

    }

}

@Preview(showBackground = true)
@Composable
fun ChannelMenuPreview(){
    HoşbeşTheme {
/*
        ChannelMenu(selectedChannel = "Fıldır Fıldır")
*/
    }
}