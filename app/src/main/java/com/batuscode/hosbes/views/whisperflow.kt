package com.batuscode.hosbes.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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

            Image(
                bitmap = image!!,
                contentDescription = "" ,
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .width(60.dp)
                    .height(60.dp),
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier
                    .padding(2.5.dp)
            ) {
                // kullanıcı adı

                Text(
                    text = whisper.wdisplayName!! ,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold ,
                        fontSize = 17.sp
                    ) ,
                    modifier = Modifier
                        .padding(start = 2.5.dp)
                )

                Text(
                    text = whisper.lm!! ,
                    style = TextStyle(
                        fontSize = 15.sp ,
                        color = Color.Gray.copy(0.8f)
                    ) ,
                    modifier = Modifier
                        .padding(start = 2.5.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun WhisperViewPreview(){
    HoşbeşTheme {
        WhisperView(whisper = Whisper() , mainActivityVM = MainActivityVM())
    }
}