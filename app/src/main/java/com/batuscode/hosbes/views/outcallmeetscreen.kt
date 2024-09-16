package com.batuscode.hosbes.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.Participnat
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.viewmodel.OutCallActivityViewModel

@Composable
fun OutCallMeetScreen(outCallActivityViewModel: OutCallActivityViewModel, historyCalls:Participnat){

    val WillJoin by outCallActivityViewModel.WillJoin.collectAsState()


    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }
    var callStateText by remember {
        mutableStateOf("")
    }
    var audioMuted by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    )
    { innerPadding ->


        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = colorResource(id = R.color.pianoblack))

        ) {


            val (ppImage , ToolBar) = createRefs()


            Column ( verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 100.dp)
                    .wrapContentSize()
                    .constrainAs(ppImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
            {

                if (image != null) {
                    Image(
                        bitmap = image!!,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .width(120.dp)
                            .height(120.dp),
                        contentScale = ContentScale.Crop
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
                    null -> ""
                    true -> callStateText = "Bağlaniyor ..."
                    false -> callStateText = "Arama devam ediyor..."
                }

                Text(
                    text = historyCalls?.displayName!!,
                    color = Color.White
                )
                Text(
                    text = callStateText ,
                    color = Color.White
                )



            }


            Surface(
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 15.dp,
                color = colorResource(id = R.color.e),
                modifier = Modifier
                    .padding(bottom = 120.dp)
                    .constrainAs(ToolBar) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically ,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) ,
                    modifier = Modifier
                        .padding(5.dp)
                ) {

                    OutlinedIconButton(
                        onClick = {
                            audioMuted = audioMuted.not()

                            outCallActivityViewModel.update_WcallMuteAudio(audioMuted)
                        } ,
                        border = null ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent
                        ) ,
                        modifier = Modifier
                            .padding(0.dp)
                            .size(30.dp)
                    )
                    {
                        Icon(
                            painter = if (!audioMuted) painterResource(id = R.drawable.mic_40px) else painterResource(
                                id = R.drawable.mic_off_40px
                            ),
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(25.dp)
                        )
                    }


                    OutlinedIconButton(
                        onClick = {
                            outCallActivityViewModel.update_WcallHangUp(true)
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
                            painter = painterResource(id = R.drawable.baseline_call_end_24) ,
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }


                    OutlinedIconButton(
                        enabled = false,
                        onClick = {
                        },
                        border = null ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent
                        ) ,
                        modifier = Modifier
                            .size(30.dp)
                    )
                    {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.videocam_off_40px
                            ),
                            contentDescription = "" ,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }



                }
            }





        }



    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun OutCallMeetScreenPreview(){
    HoşbeşTheme {
        OutCallMeetScreen(OutCallActivityViewModel() , Participnat())
    }
}