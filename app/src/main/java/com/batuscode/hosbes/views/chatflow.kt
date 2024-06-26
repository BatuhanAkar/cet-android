package com.batuscode.hosbes.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.ChatViewModel
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.Message
import com.batuscode.hosbes.utility.FirebaseManager
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.MainActivityVM
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


@Composable
fun ChatFlow( mainActivityVM: MainActivityVM , chatViewModel: ChatViewModel , modifier: Modifier = Modifier){

    val state = rememberLazyListState()
    val lifecycle = LocalLifecycleOwner.current
    val uid = FirebaseManager.currentUser?.uid.toString()
    val chats = chatViewModel.chat.collectAsState()

    LaunchedEffect(chats.value.size) {
        state.animateScrollToItem(chats.value.size - 1)
    }

    LazyColumn(
        state = state,
        modifier = modifier
    ) {

        items(chats.value!! , key = {it.messageId!!}){ message ->
/*

            if (it.senderId.equals(uid)){
                MyMessage( mainActivityVM , it.type!! , message = it)
            } else {

                MessageItemView(message = it , it.type!!)
            }
*/

            MessageItemView(message = message , type =  message.type!! , mainActivityVM = mainActivityVM , chatViewModel)

        }

    }

}

@Composable
fun getColor():Color{



    val colors = listOf(
        colorResource(id = R.color.a) ,
        colorResource(id = R.color.b) ,
        colorResource(id = R.color.c) ,
        colorResource(id = R.color.d) ,
        colorResource(id = R.color.e) ,
        colorResource(id = R.color.f) ,
        colorResource(id = R.color.g) ,
        colorResource(id = R.color.h) ,
        colorResource(id = R.color.i) ,
        colorResource(id = R.color.j)
    )



    return colors.get(Random.nextInt(colors.size))
}

@Composable
fun MessageItemView(message:Message , type:String , mainActivityVM: MainActivityVM , chatViewModel: ChatViewModel){


    val context: Context = LocalContext.current

    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }

    val timeStamp by remember { mutableStateOf(  dateformatHour(message.time!!) ) }


    // gönderen resmi ile mesaj görünümünü yan yana koy...


    Row (
        verticalAlignment = Alignment.Top ,
        modifier = Modifier
            .padding(5.5.dp)
    )
    {

        GlideApp.with(context)
            .asBitmap()
            .load(message.senderImage)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
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
                        text = message.senderName!!,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold ,
                            fontSize = 17.sp
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (message.edited == true){

                        Text(
                            text = stringResource(id = R.string.edited) ,
                            style = TextStyle(
                                fontSize = 12.sp
                            )
                        )

                    }

                    Text(
                        text = timeStamp ,
                        style = TextStyle(
                            fontSize = 12.sp
                        )
                    )


                }



                OutlinedIconButton(
                    onClick = {
                        chatViewModel.updateMessageItem(message)
                        mainActivityVM.updateShowMessageOption(true)
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
                }
            }

            Row (
            ) {

                Surface (
                    shape = RoundedCornerShape(10.dp) ,
                    color = Color.Transparent ,
                    tonalElevation = 1.dp
                )
                {


                    when
                    {
                        /*

                                            type.equals("media")->{


                                                GlideApp.with(context)
                                                    .asBitmap()
                                                    .load(message.message)
                                                    .into(object: CustomTarget<Bitmap>(){
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
                                                        bitmap = image!! ,
                                                        contentDescription = "" ,
                                                        contentScale = ContentScale.FillBounds,
                                                        modifier = Modifier
                                                            .padding(8.dp)
                                                            .width(180.dp)
                                                            .height(220.dp)
                                                    )

                                                }

                                            }
                        */

                        type.equals("text")->{
                            Text(
                                text = message.message!! ,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold ,
                                    fontSize = 20.sp
                                ),
                                modifier = Modifier

                            )


                        }

                    }




                }


            }


        }

    }


}


fun dateformatHour(timestamp: Long): String {
    val pattern = "HH:mm"
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())

    val date: Date = Date(timestamp)

    val formattedDate: String = simpleDateFormat.format(date)

    return formattedDate
}
@Composable
fun MyMessage( mainActivityVM: MainActivityVM , type:String ,message: Message){

    val context: Context = LocalContext.current

    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }

    val timeStamp by remember { mutableStateOf(  dateformatHour(message.time!!) ) }

    val mediaUploaded by mainActivityVM.mediauploaded.collectAsState()

    val mId by mainActivityVM.messageId.collectAsState()
    val placeholderImage by mainActivityVM.privateChatPlaceHolderImage.collectAsState()
    val newMediaSended by mainActivityVM.newMediaSended.collectAsState()
    val uploadingMediaProgress by mainActivityVM.mediaMessageProgress.collectAsState()


    Column (horizontalAlignment = Alignment.End , modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 20.dp, end = 8.5.dp)) {
        Surface (
            shape = RoundedCornerShape(10.dp) ,
            color = colorResource(id = R.color.x) ,
            tonalElevation = 1.dp
        ) {


            when {
                type.equals("media") -> {





                    if (newMediaSended == true && message.messageId?.equals(mId) == true){


                        Box {

                            Image(
                                bitmap = placeholderImage!! ,
                                contentDescription = "" ,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .blur(30.dp, BlurredEdgeTreatment.Rectangle)
                                    .padding(8.dp)
                                    .width(180.dp)
                                    .height(220.dp)
                            )

                            if (uploadingMediaProgress != null){


                                CircularProgressIndicator(
                                    progress = { uploadingMediaProgress?.toFloat()!! } ,

                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )

                            }

                        }



                        

                    } else {


                        GlideApp.with(context)
                            .asBitmap()
                            .load(message.message)
                            .into(object: CustomTarget<Bitmap>(){
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
                                bitmap = image!! ,
                                contentDescription = "" ,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .width(180.dp)
                                    .height(220.dp)
                            )

                        }

                    }



                }

                type.equals("text") -> {

                    Text(
                        text = message.message!! ,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(8.dp)
                    )


                }
            }

        }

        Text(
            text = timeStamp ,
            style = TextStyle(
                fontSize = 12.sp
            ) ,
            modifier = Modifier
                .align(Alignment.End)
        )
    }

}

/*
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun MyMessagePreview(){
    val message = Message()
    val mainActivityVM:MainActivityVM = viewModel()
    HoşbeşTheme {
        MyMessage( mainActivityVM ,"media" , message = message)
    }
}*/


@Preview(showBackground = true , showSystemUi = true)
@Composable
fun MessageItemViewPreview(){
    val message = Message()

    val mainActivityVM:MainActivityVM = viewModel()
    val chatViewModel:ChatViewModel = viewModel()
    HoşbeşTheme {
        MessageItemView(message = message, type = "text" , mainActivityVM = mainActivityVM , chatViewModel)
    }
}

