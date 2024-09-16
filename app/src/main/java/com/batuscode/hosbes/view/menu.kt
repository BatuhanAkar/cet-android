package com.batuscode.hosbes.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.FirebaseManager
import com.batuscode.hosbes.viewmodel.MainActivityVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(mainActivityVM: MainActivityVM){

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { mainActivityVM.updateShowMenu(false) } ,
        sheetState = sheetState ,
        modifier = Modifier.wrapContentSize() ,
        windowInsets = WindowInsets(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() ,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ) ,
        tonalElevation = 10.dp ,
        shape = RectangleShape ,


        ) {

        MenuContent(mainActivityVM = mainActivityVM)
    }

}

@Composable
fun MenuContent(mainActivityVM: MainActivityVM){

    val whisperItem by mainActivityVM.whisperItem.collectAsState()

    Column(
        modifier = Modifier
            .wrapContentSize()
    ) {

        // sohbeti benden sil butonu

        OutlinedButton(
            onClick = {

                /*TODO: sohbeti sil butonu */

                MainActivity.fm.deleteChatFromMe(whisperItem = whisperItem!! , Wref = FirebaseManager.W , mainActivityVM = mainActivityVM)

            } ,
            border = null,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Text(
                text = stringResource(id = R.string.delete_message_just_for_me) ,
                color = colorResource(id = R.color.delete)
            )
        }


        // sohbeti herkesden sil ...


        OutlinedButton(
            onClick = {

                /*TODO: sohbeti sil butonu */


                MainActivity.fm.deleteChatEveryone(whisperItem = whisperItem!! , Wref = FirebaseManager.W , W_Cref = FirebaseManager.W_C , mainActivityVM = mainActivityVM)

            } ,
            border = null,
            modifier = Modifier
                .fillMaxWidth()
        ) {


            Text(
                text = stringResource(id = R.string.delete_message_everyone) ,
                color = colorResource(id = R.color.delete)
            )
        }


    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun MenuContentPreview(){
    HoşbeşTheme {
        MenuContent(MainActivityVM())
    }
}