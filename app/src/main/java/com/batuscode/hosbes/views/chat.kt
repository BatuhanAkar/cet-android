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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Chat(mainActivityVM: MainActivityVM , chatViewModel: ChatViewModel){


    val context:Context = LocalContext.current

    val showMore by mainActivityVM.showMore.collectAsState()

    var message by remember {
        mutableStateOf("")
    }


    val channelId by mainActivityVM.channelId.collectAsState()

    val loadingChat by mainActivityVM.loadingChat.collectAsState()

    val imageBitmap by mainActivityVM.photo.collectAsState()

    val messageSended by mainActivityVM.messageSended.collectAsState()


    MainActivity.fm.chatViewModel = chatViewModel

    val lifecycle = LocalLifecycleOwner.current

    val selectedChannel by mainActivityVM.selectedChannel.collectAsState()

    DisposableEffect(lifecycle) {
        val observe = LifecycleEventObserver { _, event ->


            when(event){
                Lifecycle.Event.ON_CREATE -> {

                    Log.d("mainchat" , "on create....")

                    if (channelId == "C1"){
                        mainActivityVM.updateSelectedChannel("Hoşbeş")
                        chatViewModel.refreshChat()

                        MainActivity.fm.removeChatEventListener(FirebaseManager.C1)
                        MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C1)
                    } else if (channelId == "C2"){

                        mainActivityVM.updateSelectedChannel("Mavi Boncuk")
                        chatViewModel.refreshChat()

                        MainActivity.fm.removeChatEventListener(FirebaseManager.C2)
                        MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C2)


                    } else if (channelId == "P1"){


                        Log.d("mainchat" , "channelId == P1....")
                        if (selectedChannel == "Hoşbeş"){
                            mainActivityVM.connectChannel("C1")
                            mainActivityVM.updateSelectedChannel("Hoşbeş")
                            chatViewModel.refreshChat()
                            MainActivity.fm.removeChatEventListener(FirebaseManager.C1)
                            MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C1)
                        } else if (selectedChannel == "Maiv Boncuk"){
                            mainActivityVM.connectChannel("C2")
                            chatViewModel.refreshChat()
                            mainActivityVM.updateSelectedChannel("Mavi Boncuk")
                            MainActivity.fm.removeChatEventListener(FirebaseManager.C2)
                            MainActivity.fm.pullChat(mainActivityVM = mainActivityVM , FirebaseManager.C2)
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
                Lifecycle.Event.ON_STOP -> {}
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


            lifecycle.lifecycle.removeObserver(observe)



        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .imePadding(),
        topBar = {

            TopAppBar(title = { Row {
                ConstraintLayout () {
                    val (title , channelMenu) = createRefs()
                    ChannelMenu( mainActivityVM = mainActivityVM ,chatViewModel = chatViewModel , selectedChannel = selectedChannel!!, modifier = Modifier

                        .constrainAs(
                            channelMenu
                        ){
                            start.linkTo(title.end)
                            end.linkTo(parent.end)
                            top.linkTo(title.top)
                            bottom.linkTo(title.bottom)
                        }
                    )
                }

            }} ,

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


                })
        }

    ){innerPadding ->

        ConstraintLayout (modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {

            if (showMore == true){
                More(mainActivityVM)
            }

            val (messageRecyclerView , messageTextField) = createRefs()

            if (loadingChat == true){

                Log.d("chatFlow" , "created again...")

                if (channelId == "C1" ){


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

                            }
                    )


                } else if(channelId == "C2"){

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

                            }
                    )


                }
            } else {

                Column (
                    verticalArrangement = Arrangement.Center ,
                    horizontalAlignment = Alignment.CenterHorizontally ,
                    modifier = Modifier.fillMaxSize()
                ) {



                    CircularProgressIndicator(
                        color = Color.Blue ,
                        strokeWidth = 5.dp ,
                        strokeCap = StrokeCap.Round
                    )

                    Text(
                        text = stringResource(id = R.string.youareconnectingChat) ,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.pacifico_regular)),
                            fontSize = 20.sp
                        )
                    )

                }

            }


            MessageTextField ( mainActivityVM = mainActivityVM ,
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