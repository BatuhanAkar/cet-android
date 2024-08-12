package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.ChatViewModel
import com.batuscode.hosbes.utility.FirebaseManager
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.views.ui.ChannelMenu
import com.batuscode.hosbes.views.ui.MessageTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Chat(mainActivityVM: MainActivityVM , chatViewModel: ChatViewModel){


    val context:Context = LocalContext.current
    lateinit var delete:String



    val channelId by mainActivityVM.channelId.collectAsState()


    val imageBitmap by mainActivityVM.photo.collectAsState()



    MainActivity.fm.chatViewModel = chatViewModel

    val lifecycle = LocalLifecycleOwner.current

    val selectedChannel by mainActivityVM.selectedChannel.collectAsState()


    val loadMoreChat by mainActivityVM.loadMoreChat.collectAsState()

    val wid by mainActivityVM.whisperId.collectAsState()

    val Cscope = CoroutineScope(Dispatchers.Default)

    val inVoiceChannel by mainActivityVM.inVoiceChannel.collectAsState()

    DisposableEffect(lifecycle) {
        val observe = LifecycleEventObserver { _, event ->


            when(event){
                Lifecycle.Event.ON_CREATE -> {

                    Cscope.launch {
                        while (isActive){
                            delay(900000)
                            Log.d("reloadmessage" , "tetikledi...")

                            if (channelId == "C1"){
                                Log.d("mainchat" , "on create.... channelId == C1 ")

                                mainActivityVM.connectChannel("C1")

                                mainActivityVM.updateSelectedChannel("Hoşbeş")
                                chatViewModel.refreshChat()

                                MainActivity.fm.removeChatEventListener(FirebaseManager.C1)
                                MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C1 , true)
                            }
                            else if (channelId == "C2"){
                                Log.d("mainchat" , "on create.... channelId == C2 ")

                                mainActivityVM.connectChannel("C2")

                                mainActivityVM.updateSelectedChannel("Mavi Boncuk")
                                chatViewModel.refreshChat()

                                MainActivity.fm.removeChatEventListener(FirebaseManager.C2)
                                MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C2 , true)


                            }
                            else if (channelId == "P1"){


                                Log.d("mainchat" , "channelId == P1....")
                                if (selectedChannel == "Hoşbeş"){
                                    mainActivityVM.connectChannel("C1")
                                    mainActivityVM.updateSelectedChannel("Hoşbeş")
                                    chatViewModel.refreshChat()
                                    MainActivity.fm.removeChatEventListener(FirebaseManager.P1)
                                    MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C1 , true)
                                } else if (selectedChannel == "Mavi Boncuk"){
                                    mainActivityVM.connectChannel("C2")
                                    chatViewModel.refreshChat()
                                    mainActivityVM.updateSelectedChannel("Mavi Boncuk")
                                    MainActivity.fm.removeChatEventListener(FirebaseManager.P1)
                                    MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C2 , true)
                                }

                            }
                            else if (channelId == "W"){


                                // sohbet dinleyicisini silmek için wid gerekir .... çekk ...

                                Log.d("mainchat" , "channelId == W....")
                                if (selectedChannel == "Hoşbeş"){
                                    chatViewModel.refreshChat()
                                    mainActivityVM.connectChannel("C1")
                                    mainActivityVM.updateSelectedChannel("Hoşbeş")
                                    // MainActivity.fm.detachWhisperChatListener(wid!!)

                                    MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C1 , true)
                                } else if (selectedChannel == "Mavi Boncuk"){
                                    chatViewModel.refreshChat()
                                    mainActivityVM.connectChannel("C2")
                                    mainActivityVM.updateSelectedChannel("Mavi Boncuk")
                                    //  MainActivity.fm.detachWhisperChatListener(wid!!)
                                    MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C2 , true)
                                }

                            }
                        }
                    }

                    Log.d("mainchat" , "on create....")

                    if (channelId == "C1"){
                        Log.d("mainchat" , "on create.... channelId == C1 ")

                        mainActivityVM.connectChannel("C1")

                        mainActivityVM.updateSelectedChannel("Hoşbeş")
                        chatViewModel.refreshChat()

                        MainActivity.fm.removeChatEventListener(FirebaseManager.C1)
                        MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C1 , false)
                    }
                    else if (channelId == "C2"){
                        Log.d("mainchat" , "on create.... channelId == C2 ")

                        mainActivityVM.connectChannel("C2")

                        mainActivityVM.updateSelectedChannel("Mavi Boncuk")
                        chatViewModel.refreshChat()

                        MainActivity.fm.removeChatEventListener(FirebaseManager.C2)
                        MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C2 , false)


                    }
                    else if (channelId == "P1"){


                        Log.d("mainchat" , "channelId == P1....")
                        if (selectedChannel == "Hoşbeş"){
                            mainActivityVM.connectChannel("C1")
                            mainActivityVM.updateSelectedChannel("Hoşbeş")
                            chatViewModel.refreshChat()
                            MainActivity.fm.removeChatEventListener(FirebaseManager.P1)
                            MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C1 , false)
                        } else if (selectedChannel == "Mavi Boncuk"){
                            mainActivityVM.connectChannel("C2")
                            chatViewModel.refreshChat()
                            mainActivityVM.updateSelectedChannel("Mavi Boncuk")
                            MainActivity.fm.removeChatEventListener(FirebaseManager.P1)
                            MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C2 , false)
                        }

                    }
                    else if (channelId == "W"){


                        // sohbet dinleyicisini silmek için wid gerekir .... çekk ...

                        Log.d("mainchat" , "channelId == W....")
                        if (selectedChannel == "Hoşbeş"){
                            chatViewModel.refreshChat()
                            mainActivityVM.connectChannel("C1")
                            mainActivityVM.updateSelectedChannel("Hoşbeş")
                           // MainActivity.fm.detachWhisperChatListener(wid!!)

                            MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C1 , false)
                        } else if (selectedChannel == "Mavi Boncuk"){
                            chatViewModel.refreshChat()
                            mainActivityVM.connectChannel("C2")
                            mainActivityVM.updateSelectedChannel("Mavi Boncuk")
                          //  MainActivity.fm.detachWhisperChatListener(wid!!)
                            MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C2 , false)
                        }

                    }



                }
                Lifecycle.Event.ON_START -> {


                }
                Lifecycle.Event.ON_RESUME -> {


                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("mainchat" , "on pause....")
                    if (channelId == "C1"){
                        Log.d("mainchat" , "channelId == C1")

                        MainActivity.fm.removeChatEventListener(FirebaseManager.C1)
                    } else if (channelId == "C2"){


                        MainActivity.fm.removeChatEventListener(FirebaseManager.C2)


                    }

                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d("mainchat" , "on pause....")

                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d("mainchat" , "on destory....")


                    MainActivity.fm.removeChatEventListener(FirebaseManager.C1)
                }
                Lifecycle.Event.ON_ANY -> {


                }
            }

        }

        lifecycle.lifecycle.addObserver(observe)

        onDispose {

            Cscope.cancel()
            lifecycle.lifecycle.removeObserver(observe)



        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .imePadding(),
        topBar = {


            TopAppBar(title = {},
                navigationIcon = {
                    ChannelMenu( mainActivityVM = mainActivityVM ,chatViewModel = chatViewModel ,
                        selectedChannel = selectedChannel!!, modifier = Modifier)
                },
                actions = {


                    if (imageBitmap != null){
                        Image(
                            bitmap = imageBitmap!! ,
                            contentDescription = "" ,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .clip(CircleShape)
                                .width(30.dp)
                                .height(30.dp)
                                .clickable {
                                    Log.d("chatProfilePicture", "clicked...")
                                    mainActivityVM.updateShowMore(true)
                                }
                        )
                    }


                }
            )
        }

    ){innerPadding ->
        
        if (inVoiceChannel == false){
            ChatUI(chatViewModel = chatViewModel, mainActivityVM = mainActivityVM, innerPadding = innerPadding)
        } else if (inVoiceChannel == true){
            ConnectStremChannels(mainActivityVM = mainActivityVM)
        }

    }

    }

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ChatUI(chatViewModel: ChatViewModel , mainActivityVM: MainActivityVM , innerPadding:PaddingValues){

    val showMessageOption by mainActivityVM.showMessageOption.collectAsState()
    val loadingChat by mainActivityVM.loadingChat.collectAsState()
    val showMore by mainActivityVM.showMore.collectAsState()

    var dot1offset by remember{ mutableStateOf(0.dp) }
    var dot2offset by remember{ mutableStateOf(0.dp) }
    var dot3offset by remember{ mutableStateOf(0.dp) }
    LaunchedEffect(loadingChat == false) {
        dot1offset = 0.dp
        delay(1)
        dot1offset = -5.dp

        delay(1)
        dot1offset = 0.dp
        dot2offset = -5.dp

        delay(1)
        dot2offset = 0.dp
        dot3offset = -5.dp

        delay(1)
        dot3offset = 0.dp
    }

    var message by remember {
        mutableStateOf("")
    }
    val messageSended by mainActivityVM.messageSended.collectAsState()
    ConstraintLayout (modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {


        if (showMessageOption == true){
            MessageOption(mainActivityVM = mainActivityVM , chatViewModel = chatViewModel)
        }

        if (showMore == true){
            More(mainActivityVM)
        }

        val (messageRecyclerView , messageTextField) = createRefs()

        if (loadingChat == true){

            Log.d("chatFlow" , "created again...")

            ChatFlow( mainActivityVM ,
                chatViewModel = chatViewModel ,
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .constrainAs(
                        messageRecyclerView
                    ) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(messageTextField.top)
                        height = Dimension.fillToConstraints

                    } ,

                )

        } else {

            Column (
                verticalArrangement = Arrangement.Center ,
                horizontalAlignment = Alignment.CenterHorizontally ,
                modifier = Modifier.fillMaxSize()
            ) {

                Row {
                    Text(
                        text = stringResource(id = R.string.youareconnectingChat) ,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.pacifico_regular)),
                            fontSize = 20.sp
                        )
                    )
                    Text(text = "." , modifier = Modifier
                        .padding(start = 4.dp)
                        .offset(y = dot1offset) ,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.pacifico_regular)),
                            fontSize = 20.sp
                        ))
                    Text(text = "." , modifier = Modifier
                        .padding(start = 4.dp)
                        .offset(y = dot2offset) ,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.pacifico_regular)),
                            fontSize = 20.sp
                        ))
                    Text(text = "." , modifier = Modifier
                        .padding(start = 4.dp)
                        .offset(y = dot3offset) ,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.pacifico_regular)),
                            fontSize = 20.sp
                        ))
                }


            }

        }


        MessageTextField ( chatViewModel , mainActivityVM = mainActivityVM ,
            modifier = Modifier
                .constrainAs(
                    messageTextField
                )
                {
                    top.linkTo(messageRecyclerView.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            if (!it.isEmpty()){
                message = it
                if (messageSended == true){

                }
            }
        }

    }
}



@RequiresApi(Build.VERSION_CODES.R)
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun ChatPreview(){
    HoşbeşTheme {
        val mainActivityVM:MainActivityVM = viewModel()
        val chatViewModel:ChatViewModel = viewModel()
        Chat(mainActivityVM , chatViewModel)
    }
}