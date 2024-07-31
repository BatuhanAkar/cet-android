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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.ChatViewModel
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.utility.WhisperViewModel
import com.batuscode.hosbes.views.ui.MessageTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun _WhisperChat(mainActivityVM: MainActivityVM , chatViewModel: ChatViewModel){

    val lifecycleOwner = LocalLifecycleOwner.current // yaşam döngüsü kontrolcüsü ...
    val whisperfirst by mainActivityVM.whisperfirst.collectAsState()
    val wid by mainActivityVM.whisperId.collectAsState() // fısıltı oda id si ...
    val loadMoreChat by mainActivityVM.loadMoreChat.collectAsState()
    val user by mainActivityVM.user.collectAsState()
    val showMessageOption by mainActivityVM.showMessageOption.collectAsState()

    if (whisperfirst == true){
        mainActivityVM.updatewhisperfirst(false)
        MainActivity.fm.detachWhisperChatListener(wid!!)
        MainActivity.fm.pullWhisperChat(wid!! , loadMoreChat!!)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver{_,event ->

            when(event){
                Lifecycle.Event.ON_CREATE -> {
                    Log.d("WhisperChat" , "ON_CREATE")

                    mainActivityVM.updateInWhisper(true) // fısıltıda mesaj seçeneklerinin kontrolü için ...

                    mainActivityVM.update_whisper(true)

                    chatViewModel.refreshChat() // sohbeti sıfırla ...
                    mainActivityVM.connectChannel("W") // kanal id güncelle ...


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

                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d("WhisperChat" , "ON_STOP")
                    mainActivityVM.updateInWhisper(false) // fısıltıda mesaj seçeneklerinin kontrolü için ...

                    MainActivity.fm.detachWhisperChatListener(wid!!)
                    mainActivityVM.update_whisper(false)
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
                    Text(
                        text = stringResource(id = R.string.whisper) + " " + user?.displayName
                    )

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
            if (showMessageOption == true){
                MessageOption(mainActivityVM = mainActivityVM , chatViewModel = chatViewModel)
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

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun _WhisperChatPreview(){
    HoşbeşTheme {
        _WhisperChat(mainActivityVM = MainActivityVM() , chatViewModel = ChatViewModel())
    }
}