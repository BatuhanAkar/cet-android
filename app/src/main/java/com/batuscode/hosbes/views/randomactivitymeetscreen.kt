package com.batuscode.hosbes.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.RandomActivityViewModel
import kotlinx.coroutines.delay

@Composable
fun RandomActivityMeetScreen(randomActivityViewModel: RandomActivityViewModel){

    var audioMuted by remember {
        mutableStateOf(false)
    }

    var cameraMuted by remember {
        mutableStateOf(false)
    }
    var flipCamera by remember {
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
            .indication(indication = null, interactionSource = interactionSource)
            .clickable {
                isVisible = isVisible.not()
            }
    ){
        val mainScreen = createRef()

        if (isVisible){

            Surface(
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 15.dp,
                color = colorResource(id = R.color.e),
                modifier = Modifier
                    .padding(20.dp)
                    .constrainAs(mainScreen) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
            {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(15.dp) ,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(10.dp)
                )
                {
                    OutlinedIconButton(
                        onClick = {
                            audioMuted = audioMuted.not()

                            randomActivityViewModel.update_AudioMute(audioMuted)
                        } ,
                        border = null ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White
                        )
                    )
                    {
                        Icon(
                            painter = if (!audioMuted) painterResource(id = R.drawable.mic_40px) else painterResource(
                                id = R.drawable.mic_off_40px
                            ),
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }

                    OutlinedIconButton(
                        onClick = {
                            cameraMuted = cameraMuted.not()

                            randomActivityViewModel.update_VideoMute(cameraMuted)
                        },
                        border = null ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White
                        )
                    )
                    {
                        Icon(
                            painter = if (!cameraMuted) painterResource(id = R.drawable.videocam_48px) else painterResource(
                                id = R.drawable.videocam_off_40px
                            ),
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                    OutlinedIconButton(
                        onClick = {
                            flipCamera = flipCamera.not()

                            randomActivityViewModel.update_flipCamera(cameraMuted)
                        },
                        border = null ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White
                        )
                    )
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.flip_camera_ios_48px),
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }
                    OutlinedIconButton(
                        onClick = {

                            randomActivityViewModel.update_changeMatch(true)
                        },
                        border = null ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White
                        )
                    )
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.find_replace_48px),
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                    OutlinedIconButton(
                        onClick = {
                            randomActivityViewModel.update_hangup(true)
                        },
                        border = null ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorResource(id = R.color.delete)
                        ) ,
                        modifier = Modifier
                            .size(55.dp)
                    )
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.exit_to_app_48px) ,
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }

                }
            }
        }

    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun RandomActivityMeetScreenPreview(){
    HoşbeşTheme {
        RandomActivityMeetScreen(RandomActivityViewModel())
    }
}