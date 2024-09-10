package com.batuscode.hosbes.views

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.Whisper
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.utility.WhisperViewModel
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WhisperFlow(whisperViewModel: WhisperViewModel , mainActivityVM: MainActivityVM , modifier: Modifier){
    val whispers by whisperViewModel.whisper.collectAsState()

    LazyColumn(
        modifier = modifier
    ) {
        items(whispers , key = {it.wid!!}){ whisper ->
            WhisperView(whisper = whisper , mainActivityVM = mainActivityVM)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WhisperView(whisper: Whisper , mainActivityVM: MainActivityVM){
    val context: Context = LocalContext.current
    val timeStamp by remember { mutableStateOf(  dateformatHour(whisper.lt!!) ) }
    val showMessageOption by mainActivityVM.showMessageOption.collectAsState()

    var image by remember {
        mutableStateOf<ImageBitmap?>(null)
    }



    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
            .combinedClickable(
                onClick = {
                    val intent = Intent(context , WhisperChatActivity::class.java)
                    intent.putExtra("wdisplayName" , whisper.wdisplayName)
                    intent.putExtra("wphotoUrl" , whisper.wphotoUrl)
                    intent.putExtra("wuid" , whisper.wuid)
                    intent.putExtra("wid" , whisper.wid)
                    intent.putExtra("lm" , whisper.lm)
                    intent.putExtra("lt" , whisper.lt)
                    intent.putExtra("lwuid" , whisper.lwuid)
                    intent.putExtra("readed" , whisper.readed)

                    context.startActivity(intent)

                    mainActivityVM.updateWhisperItem(whisper)
                   // MainActivity.navigate?.navigate("whisperchat")
                } ,
                onLongClick = {
                    mainActivityVM.updateShowMenu(true)
                    mainActivityVM.updateWhisperItem(whisper = whisper)
                }
            )
    )
    {

        val (profileImage , explainlayout) = createRefs()

        GlideApp.with(context)
            .asBitmap()
            .load(whisper.wphotoUrl)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    image = resource.asImageBitmap()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }


            })


        if (image != null){


            Image(
                bitmap = image!!,
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp)
                    .clip(CircleShape)
                    .width(60.dp)
                    .height(60.dp)
                    .constrainAs(profileImage) {
                        start.linkTo(parent.start)
                        end.linkTo(explainlayout.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                contentScale = ContentScale.Crop
            )

        }




        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 9.dp)
                .constrainAs(explainlayout) {
                    start.linkTo(profileImage.end)
                    end.linkTo(parent.end)
                    top.linkTo(profileImage.top)
                    bottom.linkTo(profileImage.bottom)
                    width = Dimension.fillToConstraints
                }
        )
        {
            Text(
                text = whisper.wdisplayName!!,
                style = TextStyle(
                    fontWeight = FontWeight.Bold ,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            ConstraintLayout(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
            )
            {

                val (message , time) = createRefs()


                if (whisper.lwuid != null && whisper.lwuid?.equals(whisper.wuid) == true){

                    if (whisper.lm != null){

                        Text(
                            text = whisper.lm!!,
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold ,
                                fontSize = 17.sp ,
                                color = if (whisper.readed == false) colorResource(id = R.color.message) else Color.Gray
                            ),
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .constrainAs(message){
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    end.linkTo(time.start)
                                    width = Dimension.fillToConstraints
                                }
                        )


                    }

                } else{

                    if (whisper.lm != null){


                        Text(
                            text = whisper.lm!!,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold ,
                                fontSize = 20.sp ,
                                color = Color.Gray
                            ),
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .constrainAs(message){
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    end.linkTo(time.start)
                                    width = Dimension.fillToConstraints
                                }
                        )

                    }

                }

                Text(
                    text = timeStamp ,
                    style = TextStyle(
                        fontSize = 12.sp
                    ) ,
                    modifier = Modifier
                        .padding(2.5.dp)
                        .constrainAs(time) {

                            top.linkTo(message.top)
                            bottom.linkTo(message.bottom)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints

                        }
                )
            }

            // gönderen adı ile seçenek butonunu aralarında boşlukla yanyana koy...

            /*   Row (
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.CenterVertically ,
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(top = 5.dp)
               )
               {


                   // seçenekler butonu
                 /*  OutlinedIconButton(
                       onClick = {
                           mainActivityVM.updateShowMenu(true)
                           mainActivityVM.updateWhisperItem(whisper = whisper)
                       } ,
                       border = null ,
                       modifier = Modifier
                           .padding(0.dp)
                           .size(24.dp)
                   ) {
                       Icon(
                           painter = painterResource(id = R.drawable.more_horiz_24px) ,
                           contentDescription = "" ,
                           modifier = Modifier
                               .align(Alignment.Top)
                       )
                   }*/
               }*/

            /*   Row ( horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.CenterVertically,
                   modifier = Modifier
                       .fillMaxWidth()
               )
               {


               }*/
        }

    }
    HorizontalDivider()


  /*  ElevatedCard(
        onClick = {
            mainActivityVM.updateWhisperItem(whisper)
            MainActivity.navigate?.navigate("whisperchat")

            Log.d( "firstcall", "aramayı başlatacak kişi karşı tarafı seçti ... ")
        } ,
        shape = RectangleShape ,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {


        if (showMessageOption == true){
          //  MessageOption(mainActivityVM = mainActivityVM, chatViewModel = )
        }

        // karşı kullanıcının resmi ...


    }*/
}


fun dateformathour(timestamp: Long): String {
    val pattern = "HH:mm"
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())

    val date: Date = Date(timestamp)

    val formattedDate: String = simpleDateFormat.format(date)

    return formattedDate
}
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun WhisperViewPreview(){
    HoşbeşTheme {
        WhisperView(whisper = Whisper() , mainActivityVM = MainActivityVM())
    }
}