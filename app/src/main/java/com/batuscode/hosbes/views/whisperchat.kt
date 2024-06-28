package com.batuscode.hosbes.views

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.models.Whisper
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.ChatViewModel
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.views.ui.MessageTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhisperChat(mainActivityVM: MainActivityVM , chatViewModel: ChatViewModel){
    val lifecycleOwner = LocalLifecycleOwner.current
    val user by mainActivityVM.user.collectAsState()
    val whisperItem by mainActivityVM.whisperItem.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver{_,event ->

            when(event){
                Lifecycle.Event.ON_CREATE -> {
                    Log.d("WhisperChat" , "ON_CREATE")

                    chatViewModel.refreshChat() // sohbeti sıfırla ...
                    mainActivityVM.connectChannel("W") // kanal id güncelle ...

                    // burda ilk fısıltı mı bak ...

                    MainActivity.fm.detachWhisperChatListener(whisperItem?.wid!!)
                    MainActivity.fm.pullWhisperChat(whisperItem?.wid!!)

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

                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d("WhisperChat" , "ON_STOP")
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
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(

        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .imePadding(),
        topBar = {
            TopAppBar(
                title = {

                    Text(text = whisperItem?.wdisplayName!!)

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
                    .constrainAs(
                        messageTextField
                    )
                    {
                        top.linkTo(messageRecyclerView.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
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

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun WhisperChatPreview(){
    HoşbeşTheme {
        WhisperChat(mainActivityVM = MainActivityVM() , chatViewModel = ChatViewModel())
    }
}