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
fun Whisper(mainActivityVM: MainActivityVM , whisperViewModel: WhisperViewModel){
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver{_,event ->

            when(event){
                Lifecycle.Event.ON_CREATE -> {
                    Log.d("whisperScreen" , "ON_CREATE")

                    whisperViewModel.refreshWhispers()
                    MainActivity.fm.detachWhisperListener()
                    MainActivity.fm.pullWhisper()

                }
                Lifecycle.Event.ON_START -> {
                    Log.d("whisperScreen" , "ON_START")

                }
                Lifecycle.Event.ON_RESUME -> {
                    Log.d("whisperScreen" , "ON_RESUME")

                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("whisperScreen" , "ON_PAUSE")

                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d("whisperScreen" , "ON_STOP")
                    MainActivity.fm.detachWhisperListener()

                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d("whisperScreen" , "ON_DESTROY")

                }
                Lifecycle.Event.ON_ANY -> {
                    Log.d("whisperScreen" , "ON_ANY")

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

                    Text(text = stringResource(id = R.string.hosbeslerim))

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

        ConstraintLayout(
            modifier = Modifier
                .padding(innerPadding)
        ) {

        }
        WhisperFlow(
            whisperViewModel = whisperViewModel ,
            mainActivityVM = mainActivityVM ,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()
        )
    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun WhisperPreview(){
    HoşbeşTheme {
        Whisper(mainActivityVM = MainActivityVM(), whisperViewModel = WhisperViewModel())
    }
}