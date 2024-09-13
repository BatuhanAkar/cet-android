package com.batuscode.hosbes.views

import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.utility.PrivateRoomsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateRooms(mainActivityVM: MainActivityVM){

    Log.d("firedb" , "view created...")

    val lifecycle = LocalLifecycleOwner.current




    val privateRoomsViewModel:PrivateRoomsViewModel = viewModel()
    val showCreatePrivateRoom by mainActivityVM.showCreatePrivateRoom.collectAsState()
    val roomExist by mainActivityVM.roomExist.collectAsState()

    if (roomExist == false){
        AlertDialog(
            onDismissRequest = {
                mainActivityVM.updateRoomExist(true)
            } ,
            title = {
                Text(text = stringResource(id = R.string.roomisdeleted))
            } ,
            confirmButton = {
                TextButton(
                    onClick = {

                    } ,
                )
                {
                    Text(text = stringResource(id = R.string.ok))
                }


            } ,
            dismissButton = {
            } ,
            properties = DialogProperties(
                decorFitsSystemWindows = true ,
                usePlatformDefaultWidth = true ,
                dismissOnClickOutside = true
            )
        )
    }

    Scaffold (
        containerColor = Color.White,
        topBar = {
                 TopAppBar(
                     colors = TopAppBarDefaults.topAppBarColors(
                         containerColor = Color.White
                     ),
                     title = {

                             Text(text = stringResource(id = R.string.privaterooms))

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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    mainActivityVM.updateShowCreatePrivateRoom(true)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add ,
                    contentDescription = ""
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){



        DisposableEffect(lifecycle) {
            val observe = LifecycleEventObserver { _, event ->


                when(event){
                    Lifecycle.Event.ON_CREATE -> {
                        Log.d("privaterooms" , "ON_CREATE....")


                        privateRoomsViewModel.refreshRooms()
                        MainActivity.fm.detachPrivateRoomsListener()
                        MainActivity.fm.pullPrivateRooms(privateRoomsViewModel , mainActivityVM)
                    }
                    Lifecycle.Event.ON_START -> {
                        Log.d("privaterooms" , "ON_START....")


                    }
                    Lifecycle.Event.ON_RESUME -> {
                        Log.d("privaterooms" , "ON_RESUME....")


                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        Log.d("privaterooms" , "ON_PAUSE....")

                    }
                    Lifecycle.Event.ON_STOP -> {

                        Log.d("privaterooms" , "ON_STOP....")


                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        Log.d("privaterooms" , "on destory....")

                        MainActivity.fm.detachPrivateRoomsListener()
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

        // show private rooms flow
        PrivateRoomsFlow(paddingValues = it , privateRoomsViewModel = privateRoomsViewModel , mainActivityVM = mainActivityVM)

        if (showCreatePrivateRoom == true){
            CreatePrivateRoom(mainActivityVM = mainActivityVM)
        }

    }

}


@Preview(showBackground = true , showSystemUi = true)
@Composable
fun PrivateRoomsPreview(){
    val mainActivityVM:MainActivityVM = viewModel()
    HoşbeşTheme {
        PrivateRooms(mainActivityVM)
    }
}