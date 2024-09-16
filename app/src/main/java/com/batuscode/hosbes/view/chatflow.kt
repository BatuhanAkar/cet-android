package com.batuscode.hosbes.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.viewmodel.ChatViewModel
import com.batuscode.hosbes.R
import com.batuscode.hosbes.model.Message
import com.batuscode.hosbes.utility.FirebaseManager
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.viewmodel.MainActivityVM
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.giphy.sdk.core.GPHCore
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.views.GPHMediaView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatFlow(mainActivityVM: MainActivityVM, chatViewModel: ChatViewModel, modifier: Modifier = Modifier) {
    val state = rememberLazyListState()
    val lifecycle = LocalLifecycleOwner.current
    val uid = FirebaseManager.currentUser?.uid.toString()
    val chats = chatViewModel.chat.collectAsState()
    val scope = rememberCoroutineScope()
    val loadMoreChat by mainActivityVM.loadMoreChat.collectAsState()

    val inWhisper by mainActivityVM.inWhisper.collectAsState()

    var isAtTop by remember {
        mutableStateOf(false)
    }
    var isAtBottom by remember {
        mutableStateOf(true)
    }
    val density = LocalDensity.current
    val view = LocalView.current
    val whisperItem by mainActivityVM.whisperItem.collectAsState()
    val room by mainActivityVM.privateRoom.collectAsState()

    var inPrivateRoom = MainActivity.PreferenceManager?.getSession("inPrivateRoom")

    val keyboardIsvisible = WindowInsets.isImeVisible
    val messageSended by mainActivityVM.messageSended.collectAsState()



    LaunchedEffect(key1 = chats.value.size , key2 = keyboardIsvisible , key3 = messageSended) {

        if (-1 != (chats.value.size) - 1) {
            state.animateScrollToItem((chats.value.size) - 1) // son ogeye kaydır ...
            if (messageSended == true){
                mainActivityVM.updateMessageSended(false)
            }
        }



        snapshotFlow { state.firstVisibleItemIndex }
            .collect { index ->
                if (index == 0 && chats.value.size > 10){
                    if (inWhisper == true){
                        mainActivityVM.updateLoadMoreChat(true)
                        mainActivityVM.update_LoadMoreChat(true)
                        MainActivity.fm.loadMoreChat = true


                        if (inPrivateRoom!!){
                            Log.d("whisperChatItems" , "chatflowda privateroomda...")
                            MainActivity.fm.pullPRChat(FirebaseManager.P1 , room!! , true , false)

                        } else {
                            MainActivity.fm.pullWhisperChat(whisperItem?.wid!! , true , false)
                        }

                    }

                    Log.d("swipeTop" , "en üstte...")
                }
                isAtBottom =
                    state.layoutInfo.visibleItemsInfo.lastOrNull()?.index == state.layoutInfo.totalItemsCount - 1
            }


    }

    if (isAtBottom){
        MainActivity.fm.loadMoreChat = false
    }

    LazyColumn(
        state = state,
        modifier = modifier
            .background(Color.White)
            .imePadding()
            .fillMaxSize()
            .nestedScroll(rememberNestedScrollInteropConnection()),
    ) {
        items(chats.value!!, key = { it.messageId!! }) { message ->

            if (inWhisper == true && inPrivateRoom == false){
                MyMessage(mainActivityVM = mainActivityVM, type = message.type!!, message = message)
            } else if (inWhisper == false){
                Log.d("jokermessage", "öğe eklendi... :: " + message.type)

                MessageItemView(
                    message = message,
                    type = message.type!!,
                    mainActivityVM = mainActivityVM,
                    chatViewModel
                )
            } else if (inWhisper == true && inPrivateRoom == true){
                MessageItemView(
                    message = message,
                    type = message.type!!,
                    mainActivityVM = mainActivityVM,
                    chatViewModel
                )
            }

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

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun MessageItemView(message: Message, type:String, mainActivityVM: MainActivityVM, chatViewModel: ChatViewModel){
    val context: Context = LocalContext.current

    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }

    val inRandom by mainActivityVM.inRandom.collectAsState()
    val timeStamp by remember { mutableStateOf(  dateformatHour(message.time!!) ) }

    val inWhisper by mainActivityVM.inWhisper.collectAsState()

    val uid = MainActivity.PreferenceManager?.getString("uid")


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

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding()
            .nestedScroll(rememberNestedScrollInteropConnection())
            .padding(bottom = 8.dp)
    ) {


        val (profileImageView , messagebody , divider) = createRefs()

        // profile image
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
                .constrainAs(profileImageView) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(messagebody.start)
                    start.linkTo(parent.start)
                    width = Dimension.wrapContent

                }

        )
        {
            if (image != null){


                Image(
                    bitmap = image!!,
                    contentDescription = "",
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(40.dp)
                        .height(40.dp),
                    contentScale = ContentScale.Crop
                )

            } else {
                Image(
                    painter = painterResource(id = R.drawable.account_circle_24px),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        // .blur(10.dp, BlurredEdgeTreatment.Rectangle)
                        .width(40.dp)
                        .height(40.dp),
                    contentScale = ContentScale.Crop
                )
            }


        }

        // message surface

        Surface (
            shape = RoundedCornerShape(16.dp),
            color = if (message.senderId?.equals(uid) == true && type.equals("text")) colorResource(id = R.color.d)
            else if (type.equals("gif")) Color.Transparent
            else colorResource(id = R.color.de),
            modifier = Modifier
                .padding( start = 8.dp , end = 8.dp)
                .constrainAs(messagebody) {
                    start.linkTo(profileImageView.end)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
                .combinedClickable(
                    onClick = {},
                    onLongClick = {

                        if (inWhisper == false) {
                            mainActivityVM.updateWhisperUserUid(message.senderId!!)
                            mainActivityVM.updateMessageItem(message)
                            mainActivityVM.updateShowMessageOption(true)
                        }
                    }
                )
        )
        {


            Column(
                modifier = Modifier
                    .padding(8.dp)

            )
            {


                // gönderen adı ile seçenek butonunu aralarında boşlukla yanyana koy...

                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically ,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                {




                    if (message.edited == true){

                        Row(
                            verticalAlignment = Alignment.CenterVertically ,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            if (message.senderId?.equals(uid) == false) {
                                Text(
                                    text = message.senderName!!,
                                    style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier
                                        .wrapContentWidth()
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(id = R.string.edited) ,
                                style = TextStyle(
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier
                                    .wrapContentWidth()
                            )
                            Text(
                                text = timeStamp ,
                                style = TextStyle(
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier
                                    .wrapContentWidth()
                            )
                        }



                    } else {

                        Row(
                            verticalAlignment = Alignment.CenterVertically ,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (message.senderId?.equals(uid) == false){
                                Text(
                                    text = message.senderName!!,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold ,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier
                                        .wrapContentWidth()
                                )
                            }

                            Text(
                                text = timeStamp ,
                                style = TextStyle(
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier
                                    .wrapContentWidth()
                            )
                        }

                    }



                }

                if (type.equals("text")){
                    Log.d("loadgif" , "text function runn...")

                    Text(
                        text = message.message!!,
                        style = TextStyle(
                            fontWeight = FontWeight.W400 ,
                            fontSize = 14.sp ,
                            lineHeight = 1.4.em

                        ),
                        modifier = Modifier
                            .padding(top = 4.dp)

                    )
                }
                else if (type.equals("gif")){
                    Log.d("loadgif" , "function runn...")

                    gifView(mediaId = message.message!!)
                }

            }
        }

    }


}

@Composable
fun gifView(mediaId:String){

    var media by remember {
        mutableStateOf<Media?>(null)
    }
    LaunchedEffect(mediaId) {
        GPHCore.gifById(mediaId!!){
                result, e -> media = result?.data
            e?.let {
                Log.d("loadgif" , "error :: " + e.message)
            }
        }

    }
    if (media != null){
        AndroidView(factory = {
                ctx ->
            GPHMediaView(ctx).apply {
                media?.let {
                    setMedia(it , RenditionType.original)
                }
            }
        } , modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(16.dp))
        )
    }

}


fun dateformatHour(timestamp: Long): String {
    val pattern = "HH:mm"
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())

    val date: Date = Date(timestamp)

    val formattedDate: String = simpleDateFormat.format(date)

    return formattedDate
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyMessage(mainActivityVM: MainActivityVM, type:String, message: Message){

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
    val selfUid = MainActivity.PreferenceManager?.getuidShared("uid")


    Column (
        horizontalAlignment = if (message.senderId?.equals(selfUid) == true) Alignment.End else  Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .nestedScroll(rememberNestedScrollInteropConnection())
            .padding(bottom = 20.dp, end = 8.5.dp)) {
        Surface (
            shape = RoundedCornerShape(10.dp) ,
            color = if (message.senderId?.equals(selfUid) == true && type.equals("text")) colorResource(id = R.color.d)
            else if (type.equals("gif")) colorResource(
                id = R.color.white
            ) else colorResource(id = R.color.de),
            tonalElevation = 1.dp
        ) {


            when(type) {
                "text" -> {

                    Text(
                        text = message.message!! ,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(8.dp)
                    )


                }

                "gif" -> {
                    gifView(mediaId = message.message!!)
                }


            }

        }

        Text(
            text = timeStamp ,
            style = TextStyle(
                fontSize = 12.sp
            ) ,
            modifier = Modifier
                .align(
                    if (message.senderId?.equals(selfUid) == true) Alignment.End else  Alignment.Start
                )
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
        MyMessage( mainActivityVM ,"text" , message = message)
    }
}
*/


@Preview(showBackground = true , showSystemUi = true)
@Composable
fun MessageItemViewPreview(){
    val message = Message()

    val mainActivityVM: MainActivityVM = viewModel()
    val chatViewModel: ChatViewModel = viewModel()
    HoşbeşTheme {
        MessageItemView(Message(), type = "text" , mainActivityVM = mainActivityVM , chatViewModel)
    }
}
