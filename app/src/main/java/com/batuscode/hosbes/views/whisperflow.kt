package com.batuscode.hosbes.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun WhisperView(whisper: Whisper , mainActivityVM: MainActivityVM){
    val context: Context = LocalContext.current
    val timeStamp by remember { mutableStateOf(  dateformatHour(whisper.lt!!) ) }

    var image by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    ElevatedCard(
        onClick = {
            mainActivityVM.updateWhisperItem(whisper)
            MainActivity.navigate?.navigate("whisperchat")
        } ,
        shape = RectangleShape ,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Row {
            // karşı kullanıcının resmi ...


            Row (
                verticalAlignment = Alignment.Top ,
                modifier = Modifier
                    .padding(5.5.dp)
            )
            {

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
                            TODO("Not yet implemented")
                        }


                    })


                if (image != null){


                    Image(
                        bitmap = image!!,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .width(60.dp)
                            .height(60.dp),
                        contentScale = ContentScale.FillBounds
                    )

                }



                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 9.dp)
                )
                {


                    // gönderen adı ile seçenek butonunu aralarında boşlukla yanyana koy...

                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically ,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp)
                    )
                    {

                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            Text(
                                text = whisper.wdisplayName!!,
                                style = TextStyle(
                                    fontWeight = FontWeight.SemiBold ,
                                    fontSize = 17.sp
                                )
                            )
                        }
                    }

                    Row ( horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    {
                        if (whisper.lwuid != null && whisper.lwuid?.equals(whisper.wuid) == true){

                            if (whisper.lm != null){
                                Text(
                                    text = whisper.lm!! ,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold ,
                                        fontSize = 20.sp ,
                                        color = if (whisper.readed == false ) Color.Green else Color.Gray
                                    ),
                                    maxLines = 1,
                                    softWrap = true,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                )
                            }

                        } else{

                            if (whisper.lm != null){


                                Text(
                                    text = whisper.lm!! ,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold ,
                                        fontSize = 20.sp ,
                                        color = Color.Gray
                                    ),
                                    maxLines = 1,
                                    softWrap = true,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                )

                            }

                        }

                        Text(
                            text = timeStamp ,
                            style = TextStyle(
                                fontSize = 12.sp
                            ) ,
                        )
                    }
                }
            }
        }
    }
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