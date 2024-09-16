package com.batuscode.hosbes.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.PrivateRoom
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.viewmodel.MainActivityVM
import com.batuscode.hosbes.viewmodel.ParticipantsViewModel
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateRoomInfo(room: PrivateRoom, mainActivityVM: MainActivityVM, participantsViewModel: ParticipantsViewModel){


    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        containerColor = Color.White,
        onDismissRequest = { /*TODO*/ mainActivityVM.updateShowRoomInfo(false) } ,
        sheetState = sheetState ,
        modifier = Modifier.fillMaxSize() ,
        windowInsets = WindowInsets(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() ,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ) ,
        tonalElevation = 10.dp ,
        shape = RectangleShape ,


        ) {

        InfoContent( room , scope , mainActivityVM , context , participantsViewModel)

    }

}

@Composable
fun InfoContent(room:PrivateRoom, scope: CoroutineScope, mainActivityVM: MainActivityVM, context:Context, participantsViewModel: ParticipantsViewModel){

    var isDeleteRoom by remember {
        mutableStateOf(false)
    }

    val uid = MainActivity.PreferenceManager?.getuidShared("uid")

    if (isDeleteRoom){

        AlertDialog(
            onDismissRequest = {
                isDeleteRoom = false
            } ,
            title = {
                Text(text = stringResource(id = R.string.deletingpivateroom))
            } ,
            confirmButton = {
                TextButton(
                    onClick = {
                        MainActivity.fm.deletePrivateRoom(room)

                    } ,
                )
                {
                    Text(text = stringResource(id = R.string.deleteRoom))
                }


            } ,
            dismissButton = {
                TextButton(
                    onClick = {
                        isDeleteRoom = false
                    } ,
                )
                {
                    Text(text = stringResource(id = R.string.cancel))
                }
            } ,
            properties = DialogProperties(
                decorFitsSystemWindows = true ,
                usePlatformDefaultWidth = true ,
                dismissOnClickOutside = true
            )
        )
    }

    Column {

        // TODO: oda bilgileri içinde odayı sil butonu ...

        if (room.ownerId.equals(uid)){

            TextButton(onClick = {
                isDeleteRoom = true
            } ,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.deleteRoom) ,
                    style = TextStyle(
                        color = Color.Red ,
                        fontWeight = FontWeight.Bold ,
                        fontSize = 18.sp
                    )
                )

            }
        }

        // TODO: oda katılımcılarının gösterilecği kısım ...
        ParticipantsFlow(participantsViewModel = participantsViewModel , room)

    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun InfoContentPreview(){

    HoşbeşTheme {
        InfoContent(room = PrivateRoom(), scope = rememberCoroutineScope(), mainActivityVM = MainActivityVM(), context = LocalContext.current , ParticipantsViewModel())
    }
}