package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.Whisper
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.ChatViewModel
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.views.ui.MessageTextField
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhisperChat(mainActivityVM: MainActivityVM , chatViewModel: ChatViewModel){
    val lifecycleOwner = LocalLifecycleOwner.current
    val whisperItem by mainActivityVM.whisperItem.collectAsState()
    val showMessageOption by mainActivityVM.showMessageOption.collectAsState()
    val loadMoreChat by mainActivityVM.loadMoreChat.collectAsState()
    val Cscope = CoroutineScope(Dispatchers.Default)
    val context = LocalContext.current

    val _isOnline by mainActivityVM.isOnline.collectAsState()

    val snackBarHostState = remember{
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver{_,event ->

            when(event){
                Lifecycle.Event.ON_CREATE -> {

                    Cscope.launch {
                        delay(900000)
                        chatViewModel.refreshChat() // sohbeti sıfırla ...

                        mainActivityVM.updateInWhisper(true) // fısıltıda mesaj seçeneklerinin kontrolü için ...
                        MainActivity.PreferenceManager?.saveSession("inPrivateRoom" , false)

                        mainActivityVM.connectChannel("W") // kanal id güncelle ...

                        // burda ilk fısıltı mı bak ...

                        MainActivity.fm.detachWhisperChatListener(whisperItem?.wid!!)
                        MainActivity.fm.pullWhisperChat(whisperItem?.wid!! , loadMoreChat!! , true)

                        setReaded(whisperItem = whisperItem!!)


                    }

                    Log.d("WhisperChat" , "ON_CREATE")
                    chatViewModel.refreshChat() // sohbeti sıfırla ...

                    mainActivityVM.updateInWhisper(true) // fısıltıda mesaj seçeneklerinin kontrolü için ...
                    MainActivity.PreferenceManager?.saveSession("inPrivateRoom" , false)
                    mainActivityVM.connectChannel("W") // kanal id güncelle ...

                    // burda ilk fısıltı mı bak ...

                    MainActivity.fm.detachWhisperChatListener(whisperItem?.wid!!)
                    MainActivity.fm.pullWhisperChat(whisperItem?.wid!! , loadMoreChat!! , false)

                    setReaded(whisperItem = whisperItem!!)


                }
                Lifecycle.Event.ON_START -> {
                    Log.d("WhisperChat" , "ON_START")

                }
                Lifecycle.Event.ON_RESUME -> {
                    Log.d("WhisperChat" , "ON_RESUME")

                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("WhisperChat" , "ON_PAUSE")
                    mainActivityVM.updateLoadMoreChat(false)
                    MainActivity.fm.loadMoreChat = false
                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d("WhisperChat" , "ON_STOP")
                    mainActivityVM.updateInWhisper(false) // fısıltıda mesaj seçeneklerinin kontrolü için ...

                    MainActivity.fm.detachWhisperChatListener(whisperItem?.wid!!)

                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d("WhisperChat" , "ON_DESTROY")

                }
                Lifecycle.Event.ON_ANY -> {
                    Log.d("WhisperChat" , "ON_ANY")

                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            Cscope.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState)},
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .imePadding(),
        topBar = {
            TopAppBar(
                title = {

                    Text(text = whisperItem?.wdisplayName!!) // degistir ...

                } ,
                navigationIcon = {

                    Icon(
                        imageVector = Icons.Filled.ArrowBack ,
                        contentDescription = "" ,
                        modifier = Modifier
                            .clickable {
                                MainActivity.navigate?.popBackStack()
                            }
                    )
                } ,

                actions = {
                    OutlinedIconButton( border = null , onClick = {
                        val uid = whisperItem?.wuid

                        Log.d("yanit" , "tikladi...")
                        /**
                         * çevrimiçi ise ara ...
                         * */

                        MainActivity.fm.checkSession(uid!! , mainActivityVM)

                        val intent = Intent(context , InCallActivity::class.java)
                        intent.putExtra("type" , "OGG")
                        intent.putExtra("wuid" , whisperItem?.wuid)
                        intent.putExtra("wphotoUrl" , whisperItem?.wphotoUrl)
                        intent.putExtra("wdisplayName" , whisperItem?.wdisplayName)
                        context.startActivity(intent)

                       /* if (_isOnline == true){
                            *//**
                             * Arama yapılmak istendi ... VoiceCall aktivitesi başlat ... Aranan kişinin bilgilerini geçir ...
                             * *//*
                            //  startCall(MainActivity.context , uid!! , photoUrl!! , displayName!!)


                            //TODO: arama istendi ...
                            Log.d( "firstcall", "karşı taraf aranmak isteniyor ... ")
                            MainActivity.navigate?.navigate("callscorridor")

                        } else {
                            Log.d("kllnci" , "çevrim dışı ...")
                            scope.launch {

                                snackBarHostState.showSnackbar(
                                    message = "kullanici çevrimdışı" ,
                                    duration = SnackbarDuration.Short ,
                                    withDismissAction = false
                                )
                            }
                        }*/
                    }
                        ) {
                        Icon(painter = painterResource(id = R.drawable.call_24px), contentDescription = "")
                    }
                }
            )
        }

    )
    { innerPadding ->


        ConstraintLayout (modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            val (messageRecyclerView , messageTextField) = createRefs()

            if (showMessageOption == true){
                MessageOption(mainActivityVM = mainActivityVM, chatViewModel = chatViewModel)
            }

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

            MessageTextField ( chatViewModel , mainActivityVM = mainActivityVM ,
                modifier = Modifier
                    .padding(2.dp)
                    .background(Color.White)
                    .imePadding()
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
            ) {}

        }


    }

}

fun setReaded(whisperItem: Whisper){

    // fısıltının okunup okunmadığını al son mesaj karşı tarafa ait ise okundu olarak güncelle ...

    val remoteId = whisperItem.wuid
    val lastId = whisperItem.lwuid

    if ((remoteId == lastId) && whisperItem.readed == false){

        // okundu işaretle ...

        MainActivity.fm.updateReaded(whisperItem)

    }

}
/*

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun WhisperChatPreview(){
    HoşbeşTheme {
        WhisperChat(mainActivityVM = MainActivityVM() , chatViewModel = ChatViewModel())
    }
}*/
