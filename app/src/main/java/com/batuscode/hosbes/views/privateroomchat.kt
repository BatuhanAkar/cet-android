package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.viewmodel.ChatViewModel
import com.batuscode.hosbes.utility.FirebaseManager
import com.batuscode.hosbes.viewmodel.MainActivityVM
import com.batuscode.hosbes.viewmodel.ParticipantsViewModel
import com.batuscode.hosbes.views.ui.MessageTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateRoomChat(mainActivityVM: MainActivityVM, chatViewModel: ChatViewModel, participantsViewModel: ParticipantsViewModel){


    val context: Context = LocalContext.current
    val room by mainActivityVM.privateRoom.collectAsState()

    val showPermissionDialog by mainActivityVM.showPermissionDialog.collectAsState()

    var message by remember {
        mutableStateOf("")
    }

    val lifecycle = LocalLifecycleOwner.current
    val channelId by mainActivityVM.channelId.collectAsState()
    val outForSelectImage by mainActivityVM.outForSelectImage.collectAsState()
    val showMessageOption by mainActivityVM.showMessageOption.collectAsState()
    val showRoomInfo by mainActivityVM.showRoomInfo.collectAsState()
    val loadMoreChat by mainActivityVM.loadMoreChat.collectAsState()
    val Cscope = CoroutineScope(Dispatchers.Default)


    DisposableEffect(lifecycle) {

        val observe = LifecycleEventObserver { _, event ->


            when(event){
                Lifecycle.Event.ON_CREATE -> {

                    Cscope.launch {
                        while (isActive){

                            delay(900000)

                            chatViewModel.refreshChat()
                            mainActivityVM.connectChannel("P1")
                            mainActivityVM.updateInWhisper(true) // ( fısıltı için olanı burdada kullan ... ) fısıltıda mesaj seçeneklerinin kontrolü için ...

                            Log.d("mainchat" , "PrivateRoomChat on create....")

                            MainActivity.fm.removePrChatListener(FirebaseManager.P1 , room!!)
                            MainActivity.fm.pullPRChat(FirebaseManager.P1 , room!! , loadMoreChat!! , true)

                            MainActivity.PreferenceManager?.saveSession("inPrivateRoom" , true)
                            MainActivity.PreferenceManager?.saveuid("privateRoomId" , room?.roomId!!)
                        }
                    }

                    chatViewModel.refreshChat()
                    mainActivityVM.connectChannel("P1")
                    mainActivityVM.updateInWhisper(true) // ( fısıltı için olanı burdada kullan ... ) fısıltıda mesaj seçeneklerinin kontrolü için ...

                    Log.d("mainchat" , "PrivateRoomChat on create....")

                    MainActivity.fm.removePrChatListener(FirebaseManager.P1 , room!!)
                    MainActivity.fm.pullPRChat(FirebaseManager.P1 , room!! , loadMoreChat!! , false)

                    MainActivity.PreferenceManager?.saveSession("inPrivateRoom" , true)
                    MainActivity.PreferenceManager?.saveuid("privateRoomId" , room?.roomId!!)
                }
                Lifecycle.Event.ON_START -> {


                }
                Lifecycle.Event.ON_RESUME -> {

                    when
                    {



                        outForSelectImage == true -> {

                            Log.d("privateroomchat" , "ON_RESUME....")
                            mainActivityVM.updateOutForSelectImage(false)
                        }

                    }

                }
                Lifecycle.Event.ON_PAUSE -> {

                    mainActivityVM.updateLoadMoreChat(false)
                    MainActivity.fm.loadMoreChat = false
                    when{

                        outForSelectImage == false -> {
                            Log.d("privateroomchat" , "ON_PAUSE....")
                            MainActivity.fm.removePrChatListener(FirebaseManager.P1 , room!!)
                        }

                        outForSelectImage == true -> {

                            Log.d("privateroomchat" , "ON_PAUSE....")
                            mainActivityVM.updateOutForSelectImage(false)
                        }

                    }


                }
                Lifecycle.Event.ON_STOP -> {
                    mainActivityVM.updateInWhisper(false) // fısıltıda mesaj seçeneklerinin kontrolü için ...

                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d("privateroomchat" , "on destory....")


                    MainActivity.fm.removePrChatListener(FirebaseManager.P1 , room!!)
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
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .imePadding(),
        topBar = {

            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                              Text(
                                  text = room?.roomName.toString() ,
                                  maxLines = 2 ,

                              )
            } , actions = {
                /*IconButton(onClick = {
                    mainActivityVM.updateShowRoomInfo(true)
                }) {
                    Icon(painter = painterResource(id = R.drawable.info_24px), contentDescription = "")
                }*/
            },


                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack ,
                        contentDescription = "" ,
                        modifier = Modifier
                            .clickable {
                                MainActivity.PreferenceManager?.saveSession("inPrivateRoom" , false)
                                MainActivity.fm.handleJoinRoom( mainActivityVM ,"exit" , room!!)
                                MainActivity.navigate?.popBackStack()
                            }
                    )
                }
            )
        }

    ){innerPadding ->


        if (showRoomInfo == true){

            PrivateRoomInfo(room = room!!, mainActivityVM = mainActivityVM , participantsViewModel = participantsViewModel)
        }


        ConstraintLayout (modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {



            val (messageRecyclerView , messageTextField) = createRefs()

            if (showMessageOption == true){
                MessageOption(mainActivityVM = mainActivityVM , chatViewModel = chatViewModel)
            }

            ChatFlow(mainActivityVM ,
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


            MessageTextField ( chatViewModel , mainActivityVM = mainActivityVM ,
                modifier = Modifier
                    .background(Color.White)
                    .imePadding()
                    .fillMaxWidth()
                    .constrainAs(
                        messageTextField
                    )
                    {
                        top.linkTo(messageRecyclerView.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
            ) {
                if (!it.isEmpty()){
                    message = it
                }
            }

        }

    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun PrivateRoomPreview(){
    val mainActivityVM: MainActivityVM = viewModel()
    HoşbeşTheme {
        val chatViewModel: ChatViewModel = viewModel()
        PrivateRoomChat(mainActivityVM , chatViewModel , ParticipantsViewModel())
    }
}