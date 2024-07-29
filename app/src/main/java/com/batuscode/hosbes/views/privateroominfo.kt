package com.batuscode.hosbes.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.PrivateRoom
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.utility.ParticipantsViewModel
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateRoomInfo(room: PrivateRoom , mainActivityVM: MainActivityVM , participantsViewModel: ParticipantsViewModel){


    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
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
fun InfoContent(room:PrivateRoom , scope: CoroutineScope , mainActivityVM: MainActivityVM , context:Context , participantsViewModel: ParticipantsViewModel){
    Column {

        // TODO: oda bilgileri içinde odayı sil butonu ...
        TextButton(onClick = { /*TODO*/ } ,
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