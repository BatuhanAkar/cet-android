package com.batuscode.hosbes.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.viewmodel.VoiceChannelViewModel
import kotlinx.coroutines.delay

@Composable
fun VoiceChannelMeetScreen(voiceChannelViewModel: VoiceChannelViewModel){
    val selectedChannel by MainActivity.mMainActivityVM.selectedChannel.collectAsState()

    var audioMuted by remember {
        mutableStateOf(false)
    }

    var cameraMuted by remember {
        mutableStateOf(false)
    }

    var isVisible by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = isVisible) {
        if (isVisible){
            delay(3000)
            isVisible = false
        }
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 120.dp)
            .clickable(
                indication = null ,
                interactionSource = interactionSource
            ) {
                isVisible = isVisible.not()
            }
    ){
        val (mainScreen , roomName) = createRefs()

        if (isVisible){

            Surface(
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 15.dp,
                color = colorResource(id = R.color.e),
                modifier = Modifier
                    .padding(10.dp)
                    .constrainAs(mainScreen) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
            {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp) ,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(5.dp)
                )
                {
                    OutlinedIconButton(
                        onClick = {
                            audioMuted = audioMuted.not()

                            voiceChannelViewModel.update_voicechannelAudioMute(audioMuted)
                        } ,
                        border = null ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent
                        ) ,
                        modifier = Modifier
                            .size(30.dp)
                    )
                    {
                        Icon(
                            painter = if (!audioMuted) painterResource(id = R.drawable.mic_40px) else painterResource(
                                id = R.drawable.mic_off_40px
                            ) ,
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                    OutlinedIconButton(
                        enabled = false,
                        onClick = {
                            cameraMuted = cameraMuted.not()

                            voiceChannelViewModel.update_voicechannelVideoMute(cameraMuted)
                        },
                        border = null ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White
                        ) ,
                        modifier = Modifier
                            .size(30.dp)
                    )
                    {
                        Icon(
                            painter = if (!cameraMuted) painterResource(id = R.drawable.videocam_off_40px) else painterResource(
                                id = R.drawable.videocam_off_40px
                            ),
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center ,
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        Text(
                            text = selectedChannel!! ,
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                                textAlign = TextAlign.Center ,
                                fontSize = 25.sp
                            ),
                            modifier = Modifier
                                .padding(10.dp)

                        )
                    }

                    OutlinedIconButton(
                        onClick = {
                            voiceChannelViewModel.update_voicechannelhangup(true)
                        },
                        border = null ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorResource(id = R.color.delete)
                        )
                    )
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_close_24) ,
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }


                }
            }
        }

    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun VoiceChannelMeetScreenPreview(){
    HoşbeşTheme {
        VoiceChannelMeetScreen(VoiceChannelViewModel())
    }
}